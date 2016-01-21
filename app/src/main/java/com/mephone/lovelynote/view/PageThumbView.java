package com.mephone.lovelynote.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.GridView;

import com.mephone.lovelynote.fragment.AllPageAdapter;

public class PageThumbView extends GridView {

    public static final int PADDING = 50;
    private Context mContext;
    private AllPageAdapter mPageAdapter = null;
    private Handler mHandler = new Handler();
    private static final int NUM_COLUMNS = 3;

    public void notifyDataChanged() {
        mHandler.removeCallbacks(mIncrementalDraw);
        int width = mPageAdapter.thumbnail_width;
        mPageAdapter = new AllPageAdapter(mContext);
        mPageAdapter.thumbnail_width = width;
        mPageAdapter.setNumColumns(getNumColumns());
        setAdapter(mPageAdapter);
    }

    public PageThumbView(Context c, AttributeSet attrs) {
        super(c, attrs);
        mContext = c;
        setChoiceMode(CHOICE_MODE_SINGLE);
        setClickable(true);
        setFastScrollEnabled(true);
        setGravity(Gravity.CENTER);
        setPadding(PADDING, 0, 0, PADDING);
        mPageAdapter = new AllPageAdapter(mContext);
        setNumColumns(NUM_COLUMNS);
        mPageAdapter.setNumColumns(NUM_COLUMNS);
        setAdapter(mPageAdapter);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int width = r - l;
        mPageAdapter.thumbnail_width = (width - PADDING * (NUM_COLUMNS + 1)) / NUM_COLUMNS;
//
//        int width = r - l;
//        int columns = width / (AllPageAdapter.MIN_THUMBNAIL_WIDTH + PADDING);
//        if (columns == 0) columns = 1;
//        mPageAdapter.thumbnail_width = width / columns - PADDING;
//        mPageAdapter.computeItemHeights();
//        setColumnWidth(mPageAdapter.thumbnail_width + PADDING);
//        setNumColumns(columns);
//        mPageAdapter.setNumColumns(columns);
    }

    public void postIncrementalDraw() {
        mHandler.removeCallbacks(mIncrementalDraw);
        mHandler.post(mIncrementalDraw);
    }


    public void checkedStateChanged(int position, boolean checked) {
        mPageAdapter.checkedStateChanged(position, checked);
        invalidateViews();
    }

    public void uncheckAll() {
        mPageAdapter.uncheckAll();
        invalidateViews();
    }

    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacks(mIncrementalDraw);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler.post(mIncrementalDraw);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHandler.post(mRunInvalidateViews);

    }

    private Runnable mRunInvalidateViews = new Runnable() {
        public void run() {
            invalidateViews();
        }

    };

    private Runnable mIncrementalDraw = new Runnable() {
        public void run() {
            boolean rc = mPageAdapter.renderThumbnail();
            if (rc) {
                postIncrementalDraw();
            }
        }
    };
}
