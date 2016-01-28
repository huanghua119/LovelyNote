package com.mephone.lovelynote.write;

import android.content.Context;
import android.graphics.RectF;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.mephone.lovelynote.R;

import java.util.LinkedList;

/**
 * @author huanghua
 */
public class Clipbox extends FrameLayout implements View.OnClickListener {

    private Button mCopy = null;
    private Button mCut = null;
    private Button mDelete = null;
    private Button mPaste = null;
    private Button mCancel = null;

    private HandwriterView mHandwriterView = null;
    private GraphicsSelect mSelect = null;

    public Clipbox(Context context, HandwriterView view) {
        super(context);
        mHandwriterView = view;
        View.inflate(context, R.layout.clipbox, this);

        mCopy = (Button) findViewById(R.id.clip_copy);
        mCut = (Button) findViewById(R.id.clip_cut);
        mDelete = (Button) findViewById(R.id.clip_delete);
        mPaste = (Button) findViewById(R.id.clip_paste);
        mCancel = (Button) findViewById(R.id.clip_cancel);
        mCopy.setOnClickListener(this);
        mCut.setOnClickListener(this);
        mDelete.setOnClickListener(this);
        mPaste.setOnClickListener(this);
        mCancel.setOnClickListener(this);
    }

    public void toCopy(GraphicsSelect select) {
        mSelect = select;
        mCopy.setVisibility(View.VISIBLE);
        mCut.setVisibility(View.VISIBLE);
        mDelete.setVisibility(View.VISIBLE);
        mPaste.setVisibility(View.GONE);
        mCancel.setVisibility(View.GONE);
    }

    public void toPaste() {
        mCopy.setVisibility(View.GONE);
        mCut.setVisibility(View.GONE);
        mDelete.setVisibility(View.GONE);
        mPaste.setVisibility(View.VISIBLE);
        mCancel.setVisibility(View.VISIBLE);
    }

    public void show(float x, float y) {
        setVisibility(View.VISIBLE);
        setX(x);
        setY(y);
    }

    public void dismiss() {
        setVisibility(View.GONE);
        mSelect = null;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.clip_copy:
                break;
            case R.id.clip_cut:
                break;
            case R.id.clip_delete:
                RectF r = mSelect.rectF;
                mHandwriterView.deleteStrokesIn(r);
                break;
            case R.id.clip_paste:
                break;
            case R.id.clip_cancel:
                break;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
