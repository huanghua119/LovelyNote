package com.mephone.lovelynote.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mephone.lovelynote.R;

public class AllNoteFragment extends Fragment {


    private ListView mNoteList;
    private AllNoteAdapter mNoteAdapter;

    public AllNoteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.allnote_fragment_layout, container, false);
        mNoteList = (ListView) view.findViewById(R.id.note_list);
        mNoteAdapter = new AllNoteAdapter();
        mNoteList.setAdapter(mNoteAdapter);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
