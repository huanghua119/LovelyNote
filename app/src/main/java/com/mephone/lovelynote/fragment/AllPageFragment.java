package com.mephone.lovelynote.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.mephone.lovelynote.QuillWriterActivity;
import com.mephone.lovelynote.R;
import com.mephone.lovelynote.data.Book;
import com.mephone.lovelynote.data.Bookshelf;
import com.mephone.lovelynote.view.PageThumbView;
import com.mephone.lovelynote.write.Page;

public class AllPageFragment extends Fragment implements AdapterView.OnItemClickListener {

    private PageThumbView mThumbView = null;

    private TextView mCountText = null;

    public AllPageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        dataChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (mThumbView != null) {
//            mThumbView.setAdapter(null);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.allpage_fragment_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mThumbView = (PageThumbView) view.findViewById(R.id.page_thumb_view);
        mThumbView.setOnItemClickListener(this);
        mThumbView.setMultiChoiceModeListener(new MultiselectCallback());
        mCountText = (TextView) view.findViewById(R.id.page_count);
        mCountText.setText(getActivity().getResources().getString(R.string.page_count, Bookshelf.getCurrentBook().filteredPagesSize()));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AllPageAdapter.ViewHolder holder = (AllPageAdapter.ViewHolder) view.getTag();
        if (holder != null) {
            AllPageAdapter.Thumbnail thumb = holder.thumb;
            launchQuillWriterActivity(thumb.page);
        }
    }

    private void launchQuillWriterActivity(Page page) {
        Book book = Bookshelf.getCurrentBook();
        book.setCurrentPage(page);
        Intent i = new Intent();
        i.setClass(getContext(), QuillWriterActivity.class);
        startActivity(i);
    }

    public void dataChanged() {
        Bookshelf.getCurrentBook().filterChanged();
        if (mThumbView != null) {
            mCountText.setText(getActivity().getResources().getString(R.string.page_count, Bookshelf.getCurrentBook().filteredPagesSize()));
            mThumbView.notifyDataChanged();
        }
    }

    private class MultiselectCallback implements PageThumbView.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }
}
