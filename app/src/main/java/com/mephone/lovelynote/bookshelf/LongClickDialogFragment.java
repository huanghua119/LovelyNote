package com.mephone.lovelynote.bookshelf;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mephone.lovelynote.R;
import com.mephone.lovelynote.data.Bookshelf;
import com.mephone.lovelynote.data.Bookshelf.BookPreview;
import com.mephone.lovelynote.export.ExportActivity;
import com.mephone.lovelynote.fragment.AllNoteFragment;

import java.util.LinkedList;

public class LongClickDialogFragment extends DialogFragment implements OnClickListener {
    private static final String TAG = "LongClickDialogFragment";

    private int position;
    private boolean is_new_notebook_dialog;

    private BookPreview notebook;
    private Button okButton, cancelButton, exportButton, deleteButton;
    private EditText text;

    private AllNoteFragment mNoteFragment = null;

    public static LongClickDialogFragment newInstance(int title, int position) {
        LongClickDialogFragment frag = new LongClickDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("position", position);
        frag.setArguments(args);
        return frag;
    }

    public static LongClickDialogFragment newInstance(int title, int position, AllNoteFragment fragment) {
        LongClickDialogFragment frag = new LongClickDialogFragment();
        frag.setNoteFragment(fragment);
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("position", position);
        frag.setArguments(args);
        return frag;
    }

    private void setNoteFragment(AllNoteFragment fragment) {
        this.mNoteFragment = fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        position = getArguments().getInt("position");
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.edit_notebook_dialog);
        dialog.setTitle(title);

        LinkedList<BookPreview> notebooks = Bookshelf.getBookPreviewList();
        text = (EditText) dialog.findViewById(R.id.edit_notebook_title);
        is_new_notebook_dialog = (position == -1);
        //新建
        if (is_new_notebook_dialog) {
                text.setText(R.string.new_notebook_default_title);
        } else {
            notebook = notebooks.get(position);
            text.setText(notebook.getTitle());
        }
        text.setSelection(text.length());
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    EditText editText = (EditText) v;
                    String text = editText.getText().toString();
                    int editTextRowCount = text.split("\n").length;
                    if (editTextRowCount >= 3)
                        return true;
                }
                return false;
            }
        });

        okButton = (Button) dialog.findViewById(R.id.edit_notebook_button);
        cancelButton = (Button) dialog.findViewById(R.id.edit_notebook_cancel);
        exportButton = (Button) dialog.findViewById(R.id.edit_notebook_export);
        deleteButton = (Button) dialog.findViewById(R.id.edit_notebook_delete);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        exportButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);

        if (notebooks.size() == 1)
            deleteButton.setEnabled(false);
        if (is_new_notebook_dialog) {
            deleteButton.setVisibility(View.INVISIBLE);
            exportButton.setVisibility(View.INVISIBLE);
        }
        return dialog;
    }

    @Override
    public void onClick(View v) {
        Bookshelf bookshelf = Bookshelf.getBookshelf();
        switch (v.getId()) {
            case R.id.edit_notebook_button:
                String title = text.getText().toString();
                LinkedList<BookPreview> list = Bookshelf.getBookPreviewList();
                for (BookPreview pb : list) {
                    if (title.equals(pb.getTitle())) {
                        Toast.makeText(getActivity(), "title existence", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                bookshelf.newBook(title);
                mNoteFragment.refreshData();
                dismiss();
                break;
            case R.id.edit_notebook_cancel:
//                if (is_new_notebook_dialog) {
//                    Bookshelf.getBookshelf().deleteBook(notebook.getUUID());
//                    mNoteFragment.refreshData();
//                }
                dismiss();
                break;
            case R.id.edit_notebook_export:
                bookshelf.setCurrentBook(notebook);
                Intent exportIntent = new Intent(getActivity(), ExportActivity.class);
                exportIntent.putExtra("filename", text.getText().toString());
                startActivity(exportIntent);
                dismiss();
                break;
            case R.id.edit_notebook_delete:
                dismiss();
                //activity.showDeleteConfirmationDialog(position);
                break;
        }
    }
}
