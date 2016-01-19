package com.mephone.lovelynote.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;

import com.mephone.lovelynote.R;
import com.mephone.lovelynote.fragment.AllNoteFragment;
import com.mephone.lovelynote.fragment.AllPageFragment;
import com.mephone.lovelynote.inkml.Ink;
import com.mephone.lovelynote.inkml.InkMLProcessor;

import org.xml.sax.InputSource;

import java.io.IOException;
import java.util.List;

public class MainActivity extends BaseActivity {

    List<Ink> mInks = null;

    private AllNoteFragment mAllNoteFragment;

    private AllPageFragment mAllPageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        InkMLProcessor inkMLProcessor = new InkMLProcessor();
        inkMLProcessor.setListener(new InkMLProcessor.InkMLProcessorListener() {
            @Override
            public void onSuccess(List<Ink> inks) {
                mInks = inks;
            }

            @Override
            public void onFail(Exception e) {

            }
        });
        try {
            inkMLProcessor.parseInkMLFile(new InputSource(getAssets().open("Paper-portrait.inkml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //inkMLProcessor.parseInkMLFile("/sdcard/lovelynote/Paper-portrait.inkml");
    }

    private void initView() {
        mAllNoteFragment = new AllNoteFragment();
        mAllPageFragment = new AllPageFragment();
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.add(R.id.note_view, mAllNoteFragment);
        trx.add(R.id.page_view, mAllPageFragment);
        trx.commit();
    }
}
