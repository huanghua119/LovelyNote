package com.mephone.lovelynote.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.mephone.lovelynote.R;
import com.mephone.lovelynote.data.Bookshelf;
import com.mephone.lovelynote.fragment.AllNoteFragment;
import com.mephone.lovelynote.fragment.AllPageFragment;
import com.mephone.lovelynote.inkml.Ink;

import java.util.List;

public class MainActivity extends BaseActivity {

    private AllNoteFragment mAllNoteFragment;

    private AllPageFragment mAllPageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    public void pageDataChange() {
        mAllPageFragment.dataChanged();
    }

    private void initView() {
        mAllNoteFragment = new AllNoteFragment();
        mAllPageFragment = new AllPageFragment();
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.add(R.id.note_view, mAllNoteFragment);
        trx.add(R.id.page_view, mAllPageFragment);
        trx.commit();
    }

    @Override
    public void onBackPressed() {
        boolean isBack = mAllNoteFragment.onBackPressed();
        if (isBack) {
            super.onBackPressed();
        }
    }
}
