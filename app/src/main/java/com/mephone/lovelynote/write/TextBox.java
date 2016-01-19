package com.mephone.lovelynote.write;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.widget.EditText;

import com.mephone.lovelynote.artist.Artist;

import java.io.DataOutputStream;
import java.io.IOException;

public class TextBox extends Graphics {
    private final static String TAG = "TextBox";

    private TextPaint paint = new TextPaint();
    private StaticLayout textLayout;

    protected void setEditable(EditText edit) {
        paint.setTextSize(edit.getTextSize());
        paint.setAntiAlias(true);
        textLayout = new StaticLayout(edit.getText(), paint, edit.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
    }

    protected TextBox(Tool mTool) {
        super(mTool);
    }

    @Override
    protected void computeBoundingBox() {
        // TODO Auto-generated method stub

    }

    @Override
    public float distance(float x_screen, float y_screen) {
        offset_x = x_screen;
        offset_y = y_screen;
        return 0;
    }

    @Override
    public boolean intersects(RectF r_screen) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void draw(Canvas c, RectF bounding_box) {
        if (textLayout == null) return;
        c.save();
        c.translate(offset_x, offset_y);
        textLayout.draw(c);
        c.restore();
    }

    @Override
    public void writeToStream(DataOutputStream out) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void render(Artist artist) {
        // TODO Auto-generated method stub

    }


}
