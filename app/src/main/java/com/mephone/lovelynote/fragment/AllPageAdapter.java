package com.mephone.lovelynote.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.mephone.lovelynote.R;
import com.mephone.lovelynote.TagOverlay;
import com.mephone.lovelynote.data.Book;
import com.mephone.lovelynote.data.Bookshelf;
import com.mephone.lovelynote.view.PageThumbView;
import com.mephone.lovelynote.write.Page;

import junit.framework.Assert;

import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class AllPageAdapter extends BaseAdapter {


    protected Paint mPaint = new Paint();
    protected int[] heightOfItem = null;

    private int numColumns = 1;

    private IdentityHashMap<Page, Boolean> selectedPages = new IdentityHashMap<Page, Boolean>();
    private LinkedList<Thumbnail> unfinishedThumbnails = new LinkedList<Thumbnail>();

    public static final int MIN_THUMBNAIL_WIDTH = 300;
    public int thumbnail_width = MIN_THUMBNAIL_WIDTH;

    private Context mContext;

    public AllPageAdapter(Context c) {
        mContext = c;
        computeItemHeights();
    }

    @Override
    public int getCount() {
        return Bookshelf.getCurrentBook().filteredPagesSize();
    }

    @Override
    public Object getItem(int position) {
        Book book = Bookshelf.getCurrentBook();
        Page page = book.getFilteredPage(book.filteredPagesSize() - 1 - position);
        return page;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    protected class Thumbnail extends View {
        protected int position;
        protected Bitmap bitmap;
        protected Page page;
        protected TagOverlay tagOverlay = null;
        protected boolean checked = false;

        public Thumbnail(Context context) {
            super(context);
        }

        public int heightOfRow() {
            int row = position / numColumns;
            int maxHeight = heightOfItem[row * numColumns];
            for (int i = row * numColumns; i < (row + 1) * numColumns && i < heightOfItem.length; i++)
                maxHeight = Math.max(maxHeight, heightOfItem[i]);
            return maxHeight;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            // Log.d(TAG, "onMeasure "+position+" "+heightOfRow());
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(thumbnail_width, heightOfRow());
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (bitmap == null) {
                canvas.drawColor(Color.DKGRAY);
                //tagOverlay.draw(canvas);
                return;
            }
            // Log.d(TAG, "Thumb "+position+" "+getHeight()+" "+bitmap.getHeight());
            float y = (getHeight() - bitmap.getHeight()) / 2;
            canvas.drawBitmap(bitmap, 0, y, mPaint);
            Boolean checked = selectedPages.get(page);
            //tagOverlay.draw(canvas);
            if ((checked != null && checked)) {
                canvas.drawARGB(0x50, 0, 0xff, 0);
            }
        }

    }

    public void setNumColumns(int n) {
        numColumns = n;
    }

    public void computeItemHeights() {
        Bookshelf.getCurrentBook().filterChanged();
        LinkedList<Page> pages = Bookshelf.getCurrentBook().getFilteredPages();
        heightOfItem = new int[pages.size()];
        ListIterator<Page> iter = pages.listIterator();
        int pos = pages.size() - 1;
        while (iter.hasNext())
            heightOfItem[pos--] = (int) (thumbnail_width / iter.next().getAspectRatio());
    }

    public boolean renderThumbnail() {
        if (unfinishedThumbnails.isEmpty()) return false;
        Thumbnail thumb = unfinishedThumbnails.pop();
        Page page = thumb.page;
        thumb.bitmap = page.renderBitmap(thumbnail_width, 2 * thumbnail_width, true);
        Assert.assertTrue(thumb.bitmap != null);
        thumb.invalidate();
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Thumbnail thumb;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.page_item, parent, false);
            FrameLayout root =(FrameLayout) convertView.findViewById(R.id.thumb_root);
            thumb = new Thumbnail(mContext);
            root.addView(thumb);
            root.setPadding(0, PageThumbView.PADDING, PageThumbView.PADDING, 0);
            holder = new ViewHolder();
            holder.thumb = thumb;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            thumb = holder.thumb;
            if (thumb.position == position) {
                return convertView;
            }
            if (thumb.bitmap != null) {
                thumb.bitmap.recycle();
            }
        }
        Book book = Bookshelf.getCurrentBook();
        Page page = book.getFilteredPage(book.filteredPagesSize() - 1 - position);
        thumb.page = page;
        thumb.position = position;
        thumb.bitmap = null;
        //thumb.tagOverlay = new TagOverlay(mContext, page.tags, true);
        thumb.requestLayout();
        unfinishedThumbnails.add(thumb);
        PageThumbView grid = (PageThumbView) parent;
        grid.postIncrementalDraw();
        return convertView;
    }

    public void checkedStateChanged(int position, boolean checked) {
        Book book = Bookshelf.getCurrentBook();
        Page page = book.getFilteredPage(book.filteredPagesSize() - 1 - position);
        selectedPages.put(page, checked);
    }

    public void uncheckAll() {
        selectedPages.clear();
    }

    protected class ViewHolder {
        public Thumbnail thumb;
    }
}
