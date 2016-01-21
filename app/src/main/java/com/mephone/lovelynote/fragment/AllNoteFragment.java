package com.mephone.lovelynote.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mephone.lovelynote.R;
import com.mephone.lovelynote.activity.MainActivity;
import com.mephone.lovelynote.bookshelf.LongClickDialogFragment;
import com.mephone.lovelynote.data.Bookshelf;

import junit.framework.Assert;

import java.util.UUID;

public class AllNoteFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, View.OnClickListener {

    private ListView mNoteList;
    private AllNoteAdapter mNoteAdapter;

    private View mNormalView = null;
    private TextView mCountText = null;
    private ImageView mSearchNote = null;
    private ImageView mAddNote = null;

    private View mSelectView = null;
    private ImageView mEditNote = null;
    private ImageView mDeleteNote = null;

    private MainActivity mActivity;

    private Mode mCurrentMode = Mode.NORMAL;

    private enum Mode {
        NORMAL, SELECT
    }

    public AllNoteFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onResume() {
        super.onResume();
        Bookshelf.sortBookPreviewList();
        mNoteAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.allnote_fragment_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mNoteAdapter = new AllNoteAdapter(getContext());
        mNoteList = (ListView) view.findViewById(R.id.note_list);
        mNoteList.setOnItemLongClickListener(this);
        mNoteList.setOnItemClickListener(this);
        mNoteList.setAdapter(mNoteAdapter);
        mNoteList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mNormalView = view.findViewById(R.id.normal_mode);
        mSearchNote = (ImageView) view.findViewById(R.id.note_search);
        mAddNote = (ImageView) view.findViewById(R.id.note_add);
        mSelectView = view.findViewById(R.id.select_mode);
        mEditNote = (ImageView) view.findViewById(R.id.note_edit);
        mDeleteNote = (ImageView) view.findViewById(R.id.note_delete);
        mCountText = (TextView) view.findViewById(R.id.note_count);

        mSearchNote.setOnClickListener(this);
        mAddNote.setOnClickListener(this);
        mEditNote.setOnClickListener(this);
        mDeleteNote.setOnClickListener(this);
        changeView(mCurrentMode, -1);
    }

    private void changeView(Mode mode, int position) {
        mCurrentMode = mode;
        switch (mode) {
            case NORMAL:
                mNormalView.setVisibility(View.VISIBLE);
                mSelectView.setVisibility(View.GONE);
                mCountText.setText(getActivity().getResources().getString(R.string.note_count, Bookshelf.getBookPreviewList().size()));
                mNoteAdapter.setSelectPosition(-1);
                break;
            case SELECT:
                mNormalView.setVisibility(View.GONE);
                mSelectView.setVisibility(View.VISIBLE);
                mCountText.setText(getActivity().getResources().getString(R.string.note_select));
                mNoteAdapter.setSelectPosition(position);
                break;
        }
        mNoteAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            return false;
        }
        changeView(Mode.SELECT, position);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mCurrentMode == Mode.NORMAL) {
            if (position == 0) {
                showNewNotebookDialog();
            } else {
                Bookshelf.BookPreview nb = Bookshelf.getBookPreviewList().get(position - 1);
                Bookshelf bookshelf = Bookshelf.getBookshelf();
                if (!nb.equals(Bookshelf.getCurrentBookPreview())) {
                    bookshelf.setCurrentBook(nb);
                    ((MainActivity) getActivity()).pageDataChange();
                }
            }
        } else {
            changeView(Mode.NORMAL, -1);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.note_search:
                break;
            case R.id.note_add:
                showNewNotebookDialog();
                break;
            case R.id.note_edit:
                break;
            case R.id.note_delete:
                showDeleteConfirmationDialog(mNoteAdapter.getSelectPosition() - 1);
                break;
        }
    }

    public boolean onBackPressed() {
        if (mCurrentMode == Mode.SELECT) {
            changeView(Mode.NORMAL, -1);
            return false;
        } else {
            return true;
        }
    }

    public void refreshData() {
        Bookshelf.sortBookPreviewList();
        mNoteAdapter.notifyDataSetChanged();
        ((MainActivity) getActivity()).pageDataChange();
    }

    private void deleteIsConfirmed(UUID uuidToDelete) {
        Bookshelf.getBookshelf().deleteBook(uuidToDelete);
        changeView(Mode.NORMAL, -1);
        refreshData();
    }

    private void showNewNotebookDialog() {
        DialogFragment newFragment = LongClickDialogFragment.newInstance(
                R.string.edit_notebook_title_new, -1, this);
        newFragment.show(getActivity().getFragmentManager(), "newNotebookDialog");
    }

    private void showDeleteConfirmationDialog(int position) {
        if (position == -1) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final Bookshelf.BookPreview nb = Bookshelf.getBookPreviewList().get(position);
        builder.setTitle(android.R.string.dialog_alert_title).setMessage(getString(R.string.delete_note_confirm, nb.getTitle()));
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteIsConfirmed(nb.getUUID());
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }
}
