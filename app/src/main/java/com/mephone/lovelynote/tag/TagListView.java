package com.mephone.lovelynote.tag;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mephone.lovelynote.R;
import com.mephone.lovelynote.data.TagManager.TagSet;

public class TagListView extends RelativeLayout {
    private static final String TAG = "TagsListView";

    protected Context context;
    protected View layout;
    protected TagSetAdapter adapter = null;
    protected ListView list;
    protected EditText edittext;
    protected TextView label;

    public void showNewTextEdit(boolean show) {
        if (show) {
            edittext.setVisibility(View.VISIBLE);
            label.setVisibility(View.VISIBLE);
        } else {
            edittext.setVisibility(View.GONE);
            label.setVisibility(View.GONE);
            list.setPadding(
                    list.getPaddingLeft(),
                    0,
                    list.getPaddingRight(),
                    list.getPaddingBottom());
        }
    }

    public void notifyTagsChanged() {
        adapter.notifyDataSetChanged();
    }

    public TagListView(Context mContext, AttributeSet attrs) {
        super(mContext, attrs);
        context = mContext;
        createLayout(context);
    }

    public TagListView(Context mContext) {
        super(mContext);
        context = mContext;
        createLayout(context);
    }

    private void createLayout(Context context) {
        Log.d(TAG, "created layout");
        LayoutInflater layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = layoutInflater.inflate(R.layout.tag_list, this);
        list = (ListView) findViewById(R.id.tag_list);
        edittext = (EditText) findViewById(R.id.tag_list_text);
        label = (TextView) findViewById(R.id.tag_list_new_label);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        list.setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        list.setOnItemLongClickListener(listener);
    }

    public void setOnKeyListener(OnKeyListener listener) {
        edittext.setOnKeyListener(listener);
    }

    public ListView getAdapterView() {
        return list;
    }

    public void setTagSet(TagSet tags) {
        adapter = new TagSetAdapter(context, tags);
        adapter.setHighlightColor(Color.BLUE);
        list.setAdapter(adapter);
    }

}
