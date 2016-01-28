package com.mephone.lovelynote.write;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mephone.lovelynote.write.GraphicsControlpoint.Controlpoint;

import junit.framework.Assert;

import java.util.LinkedList;

public class TouchHandlerSelect extends TouchHandlerControlpointABC {
    private static final String TAG = "TouchHandlerSelect";
    private int penID = -1;
    private int fingerId1 = -1;
    private int fingerId2 = -1;
    private float oldPressure, newPressure;
    private float oldX, oldY, newX, newY;  // main pointer (usually pen)
    private float oldX1, oldY1, newX1, newY1;  // for 1st finger
    private float oldX2, oldY2, newX2, newY2;  // for 2nd finger
    private long oldT, newT;

    private final RectF bBox = new RectF();
    private final Rect rect = new Rect();

    private Controlpoint activeControlpoint = null;

    private GraphicsSelect mGraphicsSelect = null;

    protected GraphicsControlpoint newGraphicsObject = null;

    protected TouchHandlerSelect(HandwriterView view) {
        super(view, view.getOnlyPenInput());
    }

    /**
     * Called when the user touches with the pen
     */
    @Override
    protected void onPenDown(Controlpoint controlpoint, boolean isNew) {
        view.getClipbox().dismiss();
    }

    @Override
    protected LinkedList<? extends GraphicsControlpoint> getGraphicsObjects() {
        return null;
    }

    protected float maxDistanceControlpointScreen() {
        return 25f;
    }

    @Override
    protected void saveGraphics(GraphicsControlpoint graphics) {
        //view.saveGraphics(graphics);
        GraphicsSelect image = (GraphicsSelect) graphics;
        mGraphicsSelect = image;
        android.util.Log.i("love_note", "saveGraphics mGraphicsSelect:" + mGraphicsSelect);
        //view.callOnEditSelectListener(image);
    }

    @Override
    protected void editGraphics(GraphicsControlpoint graphics) {
        graphics.restore();
        GraphicsSelect image = (GraphicsSelect) graphics;
        //view.callOnEditSelectListener(image);
    }

    @Override
    protected GraphicsControlpoint newGraphics(float x, float y, float pressure) {
        GraphicsSelect select = new GraphicsSelect(
                getPage().getTransform(), x, y);
        mGraphicsSelect = select;
        return select;
    }

    @Override
    protected boolean onTouchEvent(MotionEvent event) {
        return onTouchEventActivePen(event);
    }

    protected boolean onTouchEventActivePen(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_MOVE) {
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
                if (idx1 == -1 || idx2 == -1) return true;
                newX1 = event.getX(idx1);
                newY1 = event.getY(idx1);
                newX2 = event.getX(idx2);
                newY2 = event.getY(idx2);
                view.invalidate();
                return true;
            }
            if (penID == -1) return true;
            int penIdx = event.findPointerIndex(penID);
            if (penIdx == -1) return true;

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
            //view.getToolBox().onControlpointMotion(event);
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
                return true;   // eat non-pen events
            penID = event.getPointerId(0);
            activeControlpoint = findControlpoint(event.getX(), event.getY());
            if (activeControlpoint == null) {
                // none within range, create new graphics
                newGraphicsObject = newGraphics(event.getX(), event.getY(), event.getPressure());
                activeControlpoint = newGraphicsObject.initialControlpoint();
                bBox.setEmpty();
                onPenDown(activeControlpoint, true);
            } else {
                newGraphicsObject = activeControlpoint.getGraphics();
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
        } else if (action == MotionEvent.ACTION_POINTER_DOWN) {  // start move gesture
            if (penID != -1) return true;     // ignore, we are currently moving a control point
            if (fingerId1 == -1) return true; // ignore after move finished
            if (fingerId2 != -1) return true; // ignore more than 2 fingers
            int idx2 = event.getActionIndex();
            oldX2 = newX2 = event.getX(idx2);
            oldY2 = newY2 = event.getY(idx2);
            float dx = newX2 - newX1;
            float dy = newY2 - newY1;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance >= getMoveGestureMinDistance()) {
                fingerId2 = event.getPointerId(idx2);
            }
            // Log.v(TAG, "ACTION_POINTER_DOWN "+fingerId2+" + "+fingerId1+" "+oldX1+" "+oldY1+" "+oldX2+" "+oldY2);
        } else if (action == MotionEvent.ACTION_POINTER_UP) {
            int idx = event.getActionIndex();
            int id = event.getPointerId(idx);
            if (getMoveGestureWhileWriting() &&
                    (id == fingerId1 || id == fingerId2) &&
                    fingerId1 != -1 && fingerId2 != -1) {
                Page page = getPage();

                Transformation t = pinchZoomTransform(page.getTransform(),
                        oldX1, newX1, oldX2, newX2, oldY1, newY1, oldY2, newY2);
                page.setTransform(t, view.canvas);

                page.draw(view.canvas);
                view.invalidate();
                abortMotion();
            }
        }
        return false;
    }

    @Override
    protected void draw(Canvas canvas, Bitmap bitmap) {
        if (fingerId2 != -1) {
            drawPinchZoomPreview(canvas, bitmap, oldX1, newX1, oldX2, newX2, oldY1, newY1, oldY2, newY2);
        } else {
            canvas.drawBitmap(bitmap, 0, 0, null);
            drawControlpoints(canvas);
        }
    }

    protected void drawControlpoints(Canvas canvas) {
        if (mGraphicsSelect != null) {
            mGraphicsSelect.drawControlpoints(canvas);
        }
        view.invalidate();
    }

    protected void onPenUp() {
        boolean isNew = (newGraphicsObject != null);

        if (mGraphicsSelect != null) {
            view.getClipbox().show(mGraphicsSelect.getBottomLeft().screenX(), mGraphicsSelect.getBottomLeft().screenY());
            view.getClipbox().toCopy(mGraphicsSelect);
        }
        newGraphicsObject = null;
        //view.callOnStrokeFinishedListener();
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
        // Log.v(TAG, "drawOutline "+graphics.getBoundingBoxRoundOut());
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

    protected Controlpoint findControlpoint(float xScreen, float yScreen) {
        final Transformation transform = getPage().getTransform();
        final float x = transform.inverseX(xScreen);
        final float y = transform.inverseY(yScreen);
        final float rMax = maxDistanceControlpoint();

        float rMin2 = rMax * rMax;
        Controlpoint closest = null;
        if (mGraphicsSelect != null) {
            for (Controlpoint p : mGraphicsSelect.controlpoints) {
                final float dx = x - p.x;
                final float dy = y - p.y;
                final float r2 = dx * dx + dy * dy;
                if (r2 < rMin2) {
                    rMin2 = r2;
                    closest = p;
                }
            }
        }
        return closest;
    }

    @Override
    protected void destroy() {
        mGraphicsSelect = null;
        view.invalidate();
        getPage().draw(view.canvas);
        view.getClipbox().dismiss();
    }

}
