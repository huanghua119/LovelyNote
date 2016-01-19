package com.mephone.lovelynote.write;

import java.util.LinkedList;

import junit.framework.Assert;

import com.mephone.lovelynote.write.GraphicsControlpoint.Controlpoint;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class TouchHandlerText extends TouchHandlerControlpointABC implements TextWatcher {
    private final static String TAG = "TouchHandlerText";

    private InputMethodManager inputMethodManager;
    private EditText editText;
    private GraphicsText mText;
    private float[] mOffsetPoint = null;

    private int penID = -1;
    private int fingerId1 = -1;
    private int fingerId2 = -1;
    private float oldPressure, newPressure;
    private float oldX, oldY, newX, newY; // main pointer (usually pen)
    private float oldX1, oldY1, newX1, newY1; // for 1st finger
    private float oldX2, oldY2, newX2, newY2; // for 2nd finger
    private long oldT, newT;

    private final RectF bBox = new RectF();
    private final Rect rect = new Rect();

    private Controlpoint activeControlpoint = null;
    private Controlpoint clickControlpoint = null;

    protected TouchHandlerText(HandwriterView view) {
        super(view, view.getOnlyPenInput());
        view.setFocusable(false);
        view.setFocusableInTouchMode(false);

        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromInputMethod(view.getApplicationWindowToken(), 0);

        editText = new EditText(getContext());
        editText.setImeOptions(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, 75f);
        editText.setTextColor(Color.TRANSPARENT);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setPadding(0, 0, 0, 0);
        editText.addTextChangedListener(this);
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.i("love_note", "keyCode:" + keyCode);
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    String text = editText.getText().toString();
                    if (TextUtils.isEmpty(text)) {
                        removeText();
                    }
                }
                return false;
            }
        });

        boolean kbd = (view.getResources().getConfiguration().keyboardHidden == Configuration.KEYBOARDHIDDEN_YES);
        Log.d(TAG, "TouchHandlerText " + kbd);
    }

    @Override
    protected void destroy() {
        editText.removeTextChangedListener(this);
        getPage().draw(view.canvas);
        view.invalidate();
        view.removeView(editText);
        removeText();

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void removeText() {
        String text = editText.getText().toString();
        if (TextUtils.isEmpty(text) && mText != null) {
            view.remove(mText);
        }
    }

    @Override
    protected void draw(Canvas canvas, Bitmap bitmap) {
        Log.d(TAG, "painting text");
        canvas.drawBitmap(bitmap, 0, 0, null);
        drawControlpoints(canvas);
    }

    @Override
    protected boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "onTOUCH");
        onTouchEventActivePen(event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                float x = event.getX();
                float y = event.getY();
                if (editText != null) {

                    if (clickControlpoint != null) {
                        x = clickControlpoint.screenX();
                        y = clickControlpoint.screenY();
                    }

                    if (mOffsetPoint != null) {
                        x -= mOffsetPoint[0];
                        y -= mOffsetPoint[1];
                    }

                    editText.setX(x);
                    editText.setY(y);
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);

                    if (editText.getParent() == null) {
                        view.addView(editText);
                    }
                    editText.requestFocus();
                    inputMethodManager.showSoftInput(editText, 0);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (editText != null && editText.hasFocus()) {
                    editText.setFocusable(false);
                }
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            default:
                break;
        }
        return true;
    }

    protected boolean onTouchEventActivePen(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_MOVE) {
            if (clickControlpoint != null) {
                return true;
            }
            if (getMoveGestureWhileWriting() && fingerId1 != -1 && fingerId2 == -1) {
                int idx1 = event.findPointerIndex(fingerId1);
                if (idx1 != -1) {
                    oldX1 = newX1 = event.getX(idx1);
                    oldY1 = newY1 = event.getY(idx1);
                }
            }
            if (getMoveGestureWhileWriting() && fingerId2 != -1) {
                Assert.assertTrue(fingerId1 != -1);
                int idx1 = event.findPointerIndex(fingerId1);
                int idx2 = event.findPointerIndex(fingerId2);
                if (idx1 == -1 || idx2 == -1)
                    return true;
                newX1 = event.getX(idx1);
                newY1 = event.getY(idx1);
                newX2 = event.getX(idx2);
                newY2 = event.getY(idx2);
                view.invalidate();
                return true;
            }
            if (penID == -1)
                return true;
            int penIdx = event.findPointerIndex(penID);
            if (penIdx == -1)
                return true;

            oldT = newT;
            newT = System.currentTimeMillis();
            // Log.v(TAG, "ACTION_MOVE index="+pen+" pointerID="+penID);
            oldX = newX;
            oldY = newY;
            oldPressure = newPressure;
            newX = event.getX(penIdx);
            newY = event.getY(penIdx);
            newPressure = event.getPressure(penIdx);
            if (newT - oldT > 300) { // sometimes ACTION_UP is lost, why?
                Log.v(TAG, "Timeout in ACTION_MOVE, " + (newT - oldT));
                oldX = newX;
                oldY = newY;
                saveGraphics(activeControlpoint.getGraphics());
            }
            drawOutline(oldX, oldY, newX, newY, oldPressure, newPressure);
            // view.getToolBox().onControlpointMotion(event);
            return true;
        } else if (action == MotionEvent.ACTION_DOWN) {
            Assert.assertTrue(event.getPointerCount() == 1);
            newT = System.currentTimeMillis();
            if (useForTouch(event) && getDoubleTapWhileWriting() && Math.abs(newT - oldT) < 250) {
                // double-tap
                view.centerAndFillScreen(event.getX(), event.getY());
                abortMotion();
                return true;
            }
            oldT = newT;
            if (useForTouch(event) && getMoveGestureWhileWriting() && event.getPointerCount() == 1) {
                fingerId1 = event.getPointerId(0);
                fingerId2 = -1;
                newX1 = oldX1 = event.getX();
                newY1 = oldY1 = event.getY();
            }
            if (penID != -1) {
                Log.e(TAG, "ACTION_DOWN without previous ACTION_UP");
                abortMotion();
                return true;
            }
            // Log.v(TAG, "ACTION_DOWN");
            if (!useForWriting(event))
                return true; // eat non-pen events
            penID = event.getPointerId(0);
            activeControlpoint = findControlpoint(event.getX(), event.getY());
            clickControlpoint = intersects(event.getX(), event.getY());

            Log.i("love_note", "clickControlpoint:" + clickControlpoint + " activeControlpoint:"
                    + activeControlpoint);
            if (activeControlpoint == null && clickControlpoint != null) {
                activeControlpoint = clickControlpoint;
                clickControlpoint.getGraphics().backup();
                bBox.set(clickControlpoint.getGraphics().getBoundingBox());
                onPenDown(clickControlpoint, false);
                return true;
            }

            if (activeControlpoint == null) {
                // none within range, create new graphics
                newGraphicsObject = newGraphics(event.getX(), event.getY(), event.getPressure());
                activeControlpoint = newGraphicsObject.initialControlpoint();
                bBox.setEmpty();
                onPenDown(activeControlpoint, true);
            } else {
                activeControlpoint.getGraphics().backup();
                bBox.set(activeControlpoint.getGraphics().getBoundingBox());
                onPenDown(activeControlpoint, false);
            }
            return true;
        } else if (action == MotionEvent.ACTION_UP) {
            Assert.assertTrue(event.getPointerCount() == 1);
            int id = event.getPointerId(0);
            onPenUp();
            abortMotion();
            return true;
        } else if (action == MotionEvent.ACTION_CANCEL) {
            // e.g. you start with finger and use pen
            // if (event.getPointerId(0) != penID) return true;
            Log.v(TAG, "ACTION_CANCEL");
            abortMotion();
            getPage().draw(view.canvas);
            view.invalidate();
            return true;
        } else if (action == MotionEvent.ACTION_POINTER_DOWN) { // start move
            // gesture
            if (penID != -1)
                return true; // ignore, we are currently moving a control point
            if (fingerId1 == -1)
                return true; // ignore after move finished
            if (fingerId2 != -1)
                return true; // ignore more than 2 fingers
            int idx2 = event.getActionIndex();
            oldX2 = newX2 = event.getX(idx2);
            oldY2 = newY2 = event.getY(idx2);
            float dx = newX2 - newX1;
            float dy = newY2 - newY1;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance >= getMoveGestureMinDistance()) {
                fingerId2 = event.getPointerId(idx2);
            }
            // Log.v(TAG,
            // "ACTION_POINTER_DOWN "+fingerId2+" + "+fingerId1+" "+oldX1+" "+oldY1+" "+oldX2+" "+oldY2);
        } else if (action == MotionEvent.ACTION_POINTER_UP) {
            int idx = event.getActionIndex();
            int id = event.getPointerId(idx);
            if (getMoveGestureWhileWriting() && (id == fingerId1 || id == fingerId2) && fingerId1 != -1
                    && fingerId2 != -1) {
                Page page = getPage();

                Transformation t = pinchZoomTransform(page.getTransform(), oldX1, newX1, oldX2, newX2, oldY1, newY1,
                        oldY2, newY2);
                page.setTransform(t, view.canvas);

                page.draw(view.canvas);
                view.invalidate();
                abortMotion();
            }
        }
        return false;
    }

    private void abortMotion() {
        penID = fingerId1 = fingerId2 = -1;
        newGraphicsObject = null;
        activeControlpoint = null;
        //view.getToolBox().stopControlpointMove();
    }

    protected void drawOutline(float oldX, float oldY, float newX, float newY, float oldPressure, float newPressure) {
        Assert.assertNotNull(activeControlpoint);
        activeControlpoint.move(newX, newY);
        GraphicsControlpoint graphics = activeControlpoint.getGraphics();
        RectF newBoundingBox = graphics.getBoundingBox();
        final float dr = graphics.controlpointRadius();
        newBoundingBox.inset(-dr, -dr);
        bBox.union(newBoundingBox);
        getPage().draw(view.canvas, bBox);
        if (newGraphicsObject != null)
            newGraphicsObject.draw(view.canvas, newGraphicsObject.getBoundingBox());
        bBox.roundOut(rect);
        view.invalidate(rect);
        bBox.set(newBoundingBox);
    }

    private Controlpoint intersects(float xScreen, float yScreen) {
        final Transformation transform = getPage().getTransform();
        final float x = transform.inverseX(xScreen);
        final float y = transform.inverseY(yScreen);

        for (GraphicsControlpoint graphics : getGraphicsObjects()) {
            if (graphics.controlpoints.size() == 4) {
                Controlpoint top_left = graphics.controlpoints.get(0);
                Controlpoint bottom_right = graphics.controlpoints.get(3);
                boolean x1 = x <= bottom_right.x;
                boolean x2 = x >= top_left.x;
                boolean y1 = y <= bottom_right.y;
                boolean y2 = y >= top_left.y;
                if (x1 && x2 && y1 && y2) {
                    return top_left;
                }
            }
        }
        return null;
    }

    @Override
    protected LinkedList<? extends GraphicsControlpoint> getGraphicsObjects() {
        return getPage().texts;
    }

    @Override
    protected void onPenDown(Controlpoint controlpoint, boolean isNew) {
        Log.i("love_note", "onPenDown isNew:" + isNew);
        if (!isNew) {
            mText = (GraphicsText) controlpoint.getGraphics();
            mOffsetPoint = mText.computeEditTextOffset(controlpoint);
            editText.setText(mText.getText());
            editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mText.getTextSize());
            editText.setX(controlpoint.screenX());
            editText.setY(controlpoint.screenY());
            editText.setSelection(editText.getText().length());
            getPage().draw(view.canvas);
            view.invalidate();
        }
        // view.getToolBox().startControlpointMove(!isNew, true);
    }

    @Override
    protected void saveGraphics(GraphicsControlpoint graphics) {

        Log.i("love_note", "saveGraphics graphics:" + graphics);
        if (mText == null) {
            view.saveGraphics(graphics);
            mText = (GraphicsText) graphics;
        }
    }

    @Override
    protected GraphicsControlpoint newGraphics(float x, float y, float pressure) {

        Log.i("love_note", "newGraphics pressure:" + pressure);
        GraphicsText text = new GraphicsText(getPage().getTransform(), x, y);
        return text;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.i("love_note", "afterTextChanged s:" + s);
        if (editText.hasFocus()) {
            mText.setEditable(editText, view.getHeight());
            getPage().draw(view.canvas);
            view.invalidate();
        }
    }

}
