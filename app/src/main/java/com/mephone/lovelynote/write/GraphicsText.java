package com.mephone.lovelynote.write;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.widget.EditText;

import com.mephone.lovelynote.artist.Artist;

public class GraphicsText extends GraphicsControlpoint {

    private StaticLayout mTextLayout;

    private Controlpoint top_left, top_right, bottom_left, bottom_right;

    private TextPaint mPaint = new TextPaint();

    private float mTextSize;
    private float mHeight;

    protected GraphicsText(Transformation transform, float x, float y) {
        super(Tool.TEXT);
        setTransform(transform);
        bottom_left = new Controlpoint(transform, x, y);
        bottom_right = new Controlpoint(transform, x, y);
        top_left = new Controlpoint(transform, x, y);
        top_right = new Controlpoint(transform, x, y);
        controlpoints.add(top_left);
        controlpoints.add(top_right);
        controlpoints.add(bottom_left);
        controlpoints.add(bottom_right);
        init();
    }

    protected GraphicsText(GraphicsText text) {
        super(text);
        bottom_left = new Controlpoint(text.bottom_left);
        bottom_right = new Controlpoint(text.bottom_right);
        top_left = new Controlpoint(text.top_left);
        top_right = new Controlpoint(text.top_right);
        controlpoints.add(top_left);
        controlpoints.add(top_right);
        controlpoints.add(bottom_left);
        controlpoints.add(bottom_right);
        init();
    }

    protected GraphicsText(DataInputStream in) throws IOException {
        super(Tool.TEXT);
        int version = in.readInt();
        if (version > 1) {
            throw new IOException("Unknown line version!");
        }

        tool = Tool.values()[in.readInt()];
        if (tool != Tool.TEXT)
            throw new IOException("Unknown tool type!");

        top_left = new Controlpoint(in.readFloat(), in.readFloat());
        top_right = new Controlpoint(in.readFloat(), in.readFloat());
        bottom_left = new Controlpoint(in.readFloat(), in.readFloat());
        bottom_right = new Controlpoint(in.readFloat(), in.readFloat());

        mTextSize = in.readFloat();
        mHeight = in.readFloat();
        setPaint();

        mTextLayout = new StaticLayout(in.readUTF(), mPaint, in.readInt(), Layout.Alignment.ALIGN_NORMAL, 1, 0, false);

        controlpoints.add(top_left);
        controlpoints.add(top_right);
        controlpoints.add(bottom_left);
        controlpoints.add(bottom_right);

    }

    @Override
    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeInt(1); // protocol #1
        out.writeInt(tool.ordinal());
        out.writeFloat(top_left.x);
        out.writeFloat(top_left.y);
        out.writeFloat(top_right.x);
        out.writeFloat(top_right.y);
        out.writeFloat(bottom_left.x);
        out.writeFloat(bottom_left.y);
        out.writeFloat(bottom_right.x);
        out.writeFloat(bottom_right.y);
        out.writeFloat(mTextSize);
        out.writeFloat(mHeight);
        out.writeUTF(mTextLayout.getText().toString());
        out.writeInt(mTextLayout.getWidth()); // Layout宽度
    }

    private void init() {

    }

    protected void setEditable(EditText edit, float height) {
        mTextSize = edit.getTextSize();
        mHeight = height;
        setPaint();
        // 计算内容物理长度
        String text = edit.getText().toString();
        String[] texts = text.split("\n");
        float textWidth = 0;
        for (String src : texts) {
            float l1 = edit.getPaint().measureText(src);
            textWidth = Math.max(l1, textWidth);
        }
        mTextLayout = new StaticLayout(edit.getText(), mPaint, (int) textWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0,
                false);
        computeRect();
    }

    public int getLayoutHeight() {
        return mTextLayout == null ? 0 : mTextLayout.getHeight();
    }

    public int getLayoutWidth() {
        return mTextLayout == null ? 0 : mTextLayout.getWidth();
    }

    private void computeRect() {
        // top_right.move(top_left.screenX() + mTextLayout.getWidth(),
        // top_left.screenY());
        // bottom_left.move(top_left.screenX(), top_left.screenY() +
        // mTextLayout.getHeight());
        // bottom_right.move(top_left.screenX() + mTextLayout.getWidth(),
        // top_left.screenY() + mTextLayout.getHeight());
        movePoint(top_right, top_left.screenX() + mTextLayout.getWidth(), top_left.screenY());
        movePoint(bottom_left, top_left.screenX(), top_left.screenY() + mTextLayout.getHeight());
        movePoint(bottom_right, top_left.screenX() + mTextLayout.getWidth(),
                top_left.screenY() + mTextLayout.getHeight());

        android.util.Log.i("love_note", "w:" + mTextLayout.getWidth() + " h:" + mTextLayout.getHeight());
    }

    private void movePoint(Controlpoint point, float x, float y) {
        point.x = transform.inverseX(x);
        point.y = transform.inverseY(y);
    }

    protected String getText() {
        if (mTextLayout != null) {
            return mTextLayout.getText().toString();
        }
        return "";
    }

    protected float getTextSize() {
        return mTextSize;
    }

    private void setPaint() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mTextSize);
        mPaint.setAntiAlias(true);
    }

    @Override
    public boolean intersects(RectF r_screen) {
        return false;
    }

    @Override
    public void draw(Canvas c, RectF bounding_box) {
        if (mTextLayout == null) {
            return;
        }

        float x0, y0;
        x0 = top_left.screenX();
        y0 = top_left.screenY();

        c.save();
        c.translate(x0, y0);
        float scale2 = scale / mHeight;
        c.scale(scale2, scale2);
        mTextLayout.draw(c);
        c.restore();
    }

    @Override
    void controlpointMoved(Controlpoint point) {
        super.controlpointMoved(point);
        if (mTextLayout == null) {
            return;
        }
        if (point == top_left) {
            top_left = point;
            computeRect();
        } else if (point == top_right) {
            top_right = point;
            movePoint(top_left, top_right.screenX() - mTextLayout.getWidth(), top_right.screenY());
            movePoint(bottom_left, top_right.screenX() - mTextLayout.getWidth(),
                    top_right.screenY() + mTextLayout.getHeight());
            movePoint(bottom_right, top_right.screenX(), top_right.screenY() + mTextLayout.getHeight());
        } else if (point == bottom_left) {
            bottom_left = point;
            movePoint(top_right, bottom_left.screenX() + mTextLayout.getWidth(),
                    bottom_left.screenY() - mTextLayout.getHeight());
            movePoint(top_left, bottom_left.screenX(), bottom_left.screenY() - mTextLayout.getHeight());
            movePoint(bottom_right, bottom_left.screenX() + mTextLayout.getWidth(), bottom_left.screenY());
        } else if (point == bottom_right) {
            bottom_right = point;
            movePoint(top_right, bottom_right.screenX(), bottom_right.screenY() - mTextLayout.getHeight());
            movePoint(top_left, bottom_right.screenX() - mTextLayout.getWidth(),
                    bottom_right.screenY() - mTextLayout.getHeight());
            movePoint(bottom_left, bottom_right.screenX() - mTextLayout.getWidth(), bottom_right.screenY());
        }
    }

    public float[] computeEditTextOffset(Controlpoint point) {
        return new float[]{point.screenX() - top_left.screenX(), point.screenY() - top_left.screenY()};
    }

    @Override
    public void render(Artist artist) {

    }

    @Override
    protected Controlpoint initialControlpoint() {
        return top_left;
    }
}
