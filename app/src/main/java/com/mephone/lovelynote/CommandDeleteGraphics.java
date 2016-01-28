package com.mephone.lovelynote;

import com.mephone.lovelynote.data.Bookshelf;
import com.mephone.lovelynote.write.Graphics;
import com.mephone.lovelynote.write.Page;

import java.util.LinkedList;

public class CommandDeleteGraphics extends Command {

    protected final LinkedList<Graphics> graphics;

    public CommandDeleteGraphics(Page page, LinkedList<Graphics> toDelete) {
        super(page);
        graphics = toDelete;
    }

    @Override
    public void execute() {
        if (graphics != null) {
            for (Graphics g : graphics) {
                UndoManager.getApplication().remove(getPage(), g);
            }
        }
    }

    @Override
    public void revert() {
        if (graphics != null) {
            for (Graphics g : graphics) {
                UndoManager.getApplication().add(getPage(), g);
            }
        }
    }

    @Override
    public String toString() {
        int n = Bookshelf.getCurrentBook().getPageNumber(getPage());
        QuillWriterActivity app = UndoManager.getApplication();
        return app.getString(R.string.command_erase_graphics, n);
    }

}
