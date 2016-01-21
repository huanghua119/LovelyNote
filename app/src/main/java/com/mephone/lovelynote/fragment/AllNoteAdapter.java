package com.mephone.lovelynote.fragment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mephone.lovelynote.R;
import com.mephone.lovelynote.data.Bookshelf;
import com.mephone.lovelynote.utils.ViewHolder;

public class AllNoteAdapter extends ArrayAdapter<Bookshelf.BookPreview> {

    private Context mContext;
    private int mSelectPosition = -1;

    public AllNoteAdapter(Context context) {
        super(context, R.layout.note_item, Bookshelf.getBookPreviewList());
        mContext = context;
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void setSelectPosition(int position) {
        this.mSelectPosition = position;
    }

    public int getSelectPosition() {
        return this.mSelectPosition;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.note_item, parent, false);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.note_title);
            holder.thumb = (ImageView) convertView.findViewById(R.id.note_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == mSelectPosition) {
            convertView.setBackgroundColor(Color.parseColor("#FF0000"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#00000000"));
        }
        if (position == 0) {
            holder.title.setText(mContext.getString(R.string.note_defalut_name));
            //Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_150);
            //holder.thumb.setImageBitmap(icon);
            return convertView;
        }
        Bookshelf.BookPreview nb = Bookshelf.getBookPreviewList().get(position - 1);
        holder.title.setText(nb.getTitle());
        //holder.thumb.setImageBitmap(nb.getThumbnail(150, 150));
        return convertView;
    }
}
