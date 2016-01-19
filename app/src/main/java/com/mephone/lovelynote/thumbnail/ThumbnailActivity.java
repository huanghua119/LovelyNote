package com.mephone.lovelynote.thumbnail;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.mephone.lovelynote.ActivityBase;
import com.mephone.lovelynote.R;
import com.mephone.lovelynote.UndoManager;
import com.mephone.lovelynote.bookshelf.BookshelfActivity;
import com.mephone.lovelynote.data.Book;
import com.mephone.lovelynote.data.Bookshelf;
import com.mephone.lovelynote.data.TagManager;
import com.mephone.lovelynote.data.TagManager.Tag;
import com.mephone.lovelynote.data.TagManager.TagSet;
import com.mephone.lovelynote.export.EvernoteExportDialog;
import com.mephone.lovelynote.export.SendDialogEvernote;
import com.mephone.lovelynote.tag.TagEditDialog;
import com.mephone.lovelynote.tag.TagListView;
import com.mephone.lovelynote.thumbnail.ThumbnailAdapter.Thumbnail;
import com.mephone.lovelynote.write.Page;

import junit.framework.Assert;

import java.util.LinkedList;


public class ThumbnailActivity extends ActivityBase implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener,
        DialogInterface.OnDismissListener {

    private static final String TAG = "Overview";

    private boolean backPressedImmediately = true;
    public static final String RESULT_BACK_KEY_PRESSED = "result_back_key_pressed";

    private View layout;
    private Menu menu;
    private MenuItem menuTagsVisible;
    private boolean showTags;

    protected TagListView tagList;
    protected ThumbnailView thumbnailGrid;

    protected TagManager tagManager;
    protected TagSet tags;

    private void launchQuillWriterActivity(Page page) {
        Book book = Bookshelf.getCurrentBook();
        book.setCurrentPage(page);
        launchQuillWriterActivity();
    }

    /* Catch user interaction
     * If there is no user interaction and the user presses back again, then we quit.
     * Pressing back is a user interaction, too, so we need a delay.
     * @see android.app.Activity#onUserInteraction()
     */
    @Override
    public void onUserInteraction() {
        if (backPressedImmediately) {
            Handler handler = new Handler();
            handler.postDelayed(this.afterUserInteraction, 250);
        }
        super.onUserInteraction();
    }

    private Runnable afterUserInteraction = new Runnable() {
        public void run() {
            backPressedImmediately = false;
        }
    };

    private void launchQuillWriterActivity() {
        Intent i = new Intent();
        i.putExtra(RESULT_BACK_KEY_PRESSED, false);
        setResult(RESULT_OK, i);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent();
        i.putExtra(RESULT_BACK_KEY_PRESSED, backPressedImmediately);
        setResult(RESULT_OK, i);
        finish();
        // Log.e(TAG, "back = "+backPressedImmediately);
        super.onBackPressed();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        switch (parent.getId()) {
            case R.id.tag_list:
                Tag t = tagManager.get(position);
                if (tags.contains(t))
                    tags.remove(t);
                else
                    tags.add(t);
                dataChanged();
                break;
            case R.id.thumbnail_grid:
                Thumbnail thumb = (Thumbnail) view;
                launchQuillWriterActivity(thumb.page);
                break;
        }
        Log.d(TAG, "Click: " + tags.size());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        Log.d(TAG, "onItemLongClick " + parent.getId());
        switch (parent.getId()) {
            case R.id.tag_list:
                longClickedTag = tagManager.get(position);
                Assert.assertNotNull(longClickedTag);
                showDialog(DIALOG_EDIT_TAG);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu mMenu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.thumbnail, mMenu);
        menu = mMenu;
        menuTagsVisible = menu.findItem(R.id.show_tags);
        menuTagsVisible.setChecked(showTags);
        return true;
    }

    protected void dataChanged() {
        Bookshelf.getCurrentBook().filterChanged();
        tagList.notifyTagsChanged();
        thumbnailGrid.notifyTagsChanged();
    }

    private Tag longClickedTag;

    @Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
        switch (id) {
            case DIALOG_EDIT_TAG:
                TagEditDialog dlg = (TagEditDialog) dialog;
                dlg.setTag(longClickedTag);
                longClickedTag = null;
                dlg.setOnDismissListener(this);
                break;
            default:
                super.onPrepareDialog(id, dialog);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dataChanged();
    }

    protected static final int RESULT_FILTER_CHANGED = 2;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                launchQuillWriterActivity();
                return true;
            case R.id.thumbnail_sync:
//                Intent intent = new Intent(this, SyncActivity.class);
//                startActivity(intent);
                return true;
            case R.id.show_tags:
                setTagsVisible(!item.isChecked());
                return true;
            case R.id.switch_notebook:
                i = new Intent(getApplicationContext(), BookshelfActivity.class);
                startActivity(i);
                return true;
            case R.id.send_to_evernote:
                Book book = Bookshelf.getCurrentBook();
                LinkedList<Page> pages = book.getFilteredPages();
                SendDialogEvernote dialog = SendDialogEvernote.newInstance(book, pages);
                dialog.show(getFragmentManager(), "sendDialogEvernote");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final int DIALOG_SEND_TO_EVERNOTE = 1;
    private static final int DIALOG_EDIT_TAG = 2;

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_SEND_TO_EVERNOTE:
                return new EvernoteExportDialog(this);
            case DIALOG_EDIT_TAG:
                return new TagEditDialog(this);
        }
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = getLayoutInflater().inflate(R.layout.thumbnail_activity, null);
        setContentView(layout);
        tagList = (TagListView) findViewById(R.id.tag_list_view);
        tagList.setOnItemClickListener(this);
        tagList.setOnItemLongClickListener(this);
        Assert.assertTrue("Tag list not created.", tagList != null);
        tagList.showNewTextEdit(false);

        thumbnailGrid = (ThumbnailView) findViewById(R.id.thumbnail_grid);
        Assert.assertTrue("Thumbnail grid not created.", thumbnailGrid != null);
        thumbnailGrid.setOnItemClickListener(this);
        thumbnailGrid.setMultiChoiceModeListener(new MultiselectCallback());

        ActionBar bar = getActionBar();
        bar.setTitle(R.string.thumbnail_title_filter);
        bar.setDisplayHomeAsUpEnabled(true);
    }

    private void reloadTags() {
        Book book = Bookshelf.getCurrentBook();
        tagManager = book.getTagManager();
        tagManager.sort();
        tags = Bookshelf.getCurrentBook().getFilter();
        tagList.setTagSet(tags);
        dataChanged();
    }

    private final static String KEY_TAGS_VISIBLE = "thumbnail_activity_tags_visible";

    @Override
    protected void onResume() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        showTags = settings.getBoolean(KEY_TAGS_VISIBLE, true);
        setTagsVisible(showTags);
        reloadTags();
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        thumbnailGrid.setAdapter(null); // stop updates
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(KEY_TAGS_VISIBLE, showTags);
        editor.commit();
        super.onPause();
        // tool.debug.MemDebug.logHeap(this.getClass());
        thumbnailGrid.setAdapter(null); // stop updates
    }

    private void setTagsVisible(boolean visible) {
        showTags = visible;
        if (menuTagsVisible != null)
            menuTagsVisible.setChecked(visible);
        tagList.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private class MultiselectCallback implements
            ThumbnailView.MultiChoiceModeListener {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.thumbnail_action_mode, menu);
            mode.setTitle(R.string.thumbnail_multiselect_title);
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            tagList.setOnItemClickListener(null);
            tagList.setOnItemLongClickListener(null);
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.thumbnail_action_delete:
                    deleteSelectedPages();
                    mode.finish();
                    break;
                default:
                    Toast.makeText(getBaseContext(), "Clicked " + item.getTitle(),
                            Toast.LENGTH_SHORT).show();
                    mode.finish();
                    break;
            }
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
            tagList.setOnItemClickListener(ThumbnailActivity.this);
            tagList.setOnItemLongClickListener(ThumbnailActivity.this);
            thumbnailGrid.uncheckAll();
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                              long id, boolean checked) {
            final int checkedCount = thumbnailGrid.getCheckedItemCount();
            thumbnailGrid.checkedStateChanged(position, checked);
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                case 1:
                    mode.setSubtitle(R.string.thumbnail_multiselect_single);
                    break;
                default:
                    mode.setSubtitle(getString(R.string.thumbnail_multiselect_multiple,
                            checkedCount));
                    break;
            }
        }

        private LinkedList<Page> getSelectedPages() {
            LinkedList<Page> result = new LinkedList<Page>();
            SparseBooleanArray isChecked = thumbnailGrid.getCheckedItemPositions();
            LinkedList<Page> filtered = Bookshelf.getCurrentBook().getFilteredPages();
            int n = thumbnailGrid.getCount();
            for (int i = 0; i < n; i++)
                if (isChecked.get(i)) {
                    // Log.e(TAG, "isChecked "+i);
                    result.add(filtered.get(n - i - 1));
                }
            return result;
        }

        private void deleteSelectedPages() {
            UndoManager.getUndoManager().clearHistory();
            LinkedList<Page> toRemove = getSelectedPages();
            Book book = Bookshelf.getCurrentBook();
            Page currentPage = book.currentPage();
            book.getPages().removeAll(toRemove);
            if (book.getPages().isEmpty())
                book.getPages().add(currentPage);
            if (toRemove.contains(currentPage))
                book.setCurrentPage(book.getPages().getLast());
            else
                book.setCurrentPage(currentPage);
            book.filterChanged();
            dataChanged();
        }
    }

}
