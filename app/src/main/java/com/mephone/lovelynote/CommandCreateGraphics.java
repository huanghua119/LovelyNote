package com.mephone.lovelynote;

import com.mephone.lovelynote.data.Bookshelf;

import com.mephone.lovelynote.write.Graphics;
import com.mephone.lovelynote.write.Page;

public class CommandCreateGraphics extends Command {

    protected final Graphics graphics;

    public CommandCreateGraphics(Page page, Graphics toAdd) {
        super(page);
        graphics = toAdd;
    }

    @Override
    public void execute() {
        UndoManager.getApplication().add(getPage(), graphics);
    }

    @Override
    public void revert() {
        UndoManager.getApplication().remove(getPage(), graphics);
    }

    @Override
    public String toString() {
        int n = Bookshelf.getCurrentBook().getPageNumber(getPage());
        QuillWriterActivity app = UndoManager.getApplication();
        return app.getString(R.string.command_create_graphics, n);
    }
}
