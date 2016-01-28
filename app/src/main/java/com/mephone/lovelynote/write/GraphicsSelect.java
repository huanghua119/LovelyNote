package com.mephone.lovelynote.write;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;

import com.mephone.lovelynote.artist.Artist;

import junit.framework.Assert;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class GraphicsSelect extends GraphicsControlpoint {
    private static final String TAG = "GraphicsSelect";

    private Controlpoint bottom_left, bottom_right, top_left, top_right,
            center;
    private final Paint paint = new Paint();
    private final Paint outline = new Paint();
    private final Rect rect = new Rect();
    protected final RectF rectF = new RectF();

    private int height, width;
    private float sqrtAspect;

    // persistent data
    protected UUID uuid = null;
    protected boolean constrainAspect = true;
    protected Rect cropRect = new Rect();

    public UUID getUuid() {
        if (uuid == null)
            uuid = UUID.randomUUID();
        return uuid;
    }

    public boolean getConstrainAspect() {
        return constrainAspect;
    }

    public Controlpoint getBottomLeft() {
        return bottom_left;
    }

    /**
     * Construct a new image
     *
     * @param transform The current transformation
     * @param x         Screen x coordinate
     * @param y         Screen y coordinate
     */
    protected GraphicsSelect(Transformation transform, float x, float y) {
        super(Tool.SELECT);
        setTransform(transform);
        bottom_left = new Controlpoint(transform, x, y);
        bottom_right = new Controlpoint(transform, x, y);
        top_left = new Controlpoint(transform, x, y);
        top_right = new Controlpoint(transform, x, y);
        center = new Controlpoint(transform, x, y);
        controlpoints.add(bottom_left);
        controlpoints.add(bottom_right);
        controlpoints.add(top_left);
        controlpoints.add(top_right);
        controlpoints.add(center);
        init();
    }

    /**
     * The copy constructor
     *
     * @param select
     */
    protected GraphicsSelect(final GraphicsSelect select) {
        super(select);
        bottom_left = new Controlpoint(select.bottom_left);
        bottom_right = new Controlpoint(select.bottom_right);
        top_left = new Controlpoint(select.top_left);
        top_right = new Controlpoint(select.top_right);
        center = new Controlpoint(select.center);
        controlpoints.add(bottom_left);
        controlpoints.add(bottom_right);
        controlpoints.add(top_left);
        controlpoints.add(top_right);
        controlpoints.add(center);
        constrainAspect = select.constrainAspect;
        init();
    }

    private void init() {
        paint.setARGB(0xff, 0x5f, 0xff, 0x5f);
        paint.setStyle(Style.FILL);
        paint.setStrokeWidth(0);
        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        outline.setARGB(0xff, 0xaa, 0x00, 0x0);
        outline.setStyle(Style.STROKE);
        outline.setStrokeWidth(4);
        outline.setAntiAlias(true);
        outline.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected Controlpoint initialControlpoint() {
        return bottom_right;
    }

    @Override
    public boolean intersects(RectF screenRect) {
        return false;
    }

    @Override
    public void draw(Canvas c, RectF bounding_box) {
        computeScreenRect();
        c.clipRect(0, 0, c.getWidth(), c.getHeight(), android.graphics.Region.Op.REPLACE);

        //c.drawRect(rect, paint);
        c.drawRect(rect, outline);
    }

    private Controlpoint oppositeControlpoint(Controlpoint point) {
        if (point == bottom_right)
            return top_left;
        if (point == bottom_left)
            return top_right;
        if (point == top_right)
            return bottom_left;
        if (point == top_left)
            return bottom_right;
        if (point == center)
            return center;
        Assert.fail("Unreachable");
        return null;
    }

    private final static float minDistancePixel = 30;

    @Override
    void controlpointMoved(Controlpoint point) {
        super.controlpointMoved(point);
        if (point == center) {
            float width2 = (bottom_right.x - bottom_left.x) / 2;
            float height2 = (top_right.y - bottom_right.y) / 2;
            bottom_right.y = bottom_left.y = center.y - height2;
            top_right.y = top_left.y = center.y + height2;
            bottom_right.x = top_right.x = center.x + width2;
            bottom_left.x = top_left.x = center.x - width2;
        } else {
            Controlpoint opposite = oppositeControlpoint(point);
            float dx = opposite.x - point.x;
            float dy = opposite.y - point.y;
            float minDistance = minDistancePixel / scale;
            if (-minDistance <= dx && dx <= minDistance) {
                float sgn = Math.signum(dx);
                opposite.x = point.x + sgn * minDistance;
                dx = sgn * minDistance;
            }
            if (-minDistance <= dy && dy <= minDistance) {
                float sgn = Math.signum(dy);
                opposite.y = point.y + sgn * minDistance;
                dy = sgn * minDistance;
            }
            if (constrainAspect && false) {
                float r = (Math.abs(dx) + Math.abs(dy)) / 2;
                dx = r * sqrtAspect * Math.signum(dx);
                dy = r / sqrtAspect * Math.signum(dy);
                // Log.d(TAG, "move "+dx + " "+dy + " " + r + " "+(sqrtAspect*sqrtAspect));
            }
            rectF.bottom = opposite.y;
            rectF.top = opposite.y - dy;
            rectF.left = opposite.x;
            rectF.right = opposite.x - dx;
            rectF.sort();
            bottom_right.y = bottom_left.y = rectF.bottom;
            top_right.y = top_left.y = rectF.top;
            bottom_right.x = top_right.x = rectF.right;
            bottom_left.x = top_left.x = rectF.left;
            center.x = rectF.left + (rectF.right - rectF.left) / 2;
            center.y = rectF.bottom + (rectF.top - rectF.bottom) / 2;
        }
    }

    private void computeScreenRect() {
        rectF.bottom = bottom_left.screenY();
        rectF.top = top_left.screenY();
        rectF.left = bottom_left.screenX();
        rectF.right = bottom_right.screenX();
        rectF.sort();
        rectF.round(rect);
    }

    public void writeToStream(DataOutputStream out) throws IOException {
        out.writeInt(1);  // protocol #1
        out.writeUTF(uuid.toString());
        out.writeFloat(top_left.x);
        out.writeFloat(top_right.x);
        out.writeFloat(top_left.y);
        out.writeFloat(bottom_left.y);
        out.writeBoolean(constrainAspect);
    }

    public GraphicsSelect(DataInputStream in) throws IOException {
        super(Tool.SELECT);
        int version = in.readInt();
        if (version > 1)
            throw new IOException("Unknown image version!");

        uuid = UUID.fromString(in.readUTF());
        float left = in.readFloat();
        float right = in.readFloat();
        float top = in.readFloat();
        float bottom = in.readFloat();
        constrainAspect = in.readBoolean();

        bottom_left = new Controlpoint(transform, left, bottom);
        bottom_right = new Controlpoint(transform, right, bottom);
        top_left = new Controlpoint(transform, left, top);
        top_right = new Controlpoint(transform, right, top);
        center = new Controlpoint(transform, (left + right) / 2, (top + bottom) / 2);
        controlpoints.add(bottom_left);
        controlpoints.add(bottom_right);
        controlpoints.add(top_left);
        controlpoints.add(top_right);
        controlpoints.add(center);
        init();
    }

    @Override
    public void render(Artist artist) {
    }

}
