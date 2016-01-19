package com.mephone.lovelynote;

import com.mephone.lovelynote.write.Graphics;
import com.mephone.lovelynote.write.Page;

public class CommandModifyGraphics extends Command {

    protected final Graphics graphicsOld, graphicsNew;

    public CommandModifyGraphics(Page page, Graphics toErase, Graphics toReCreate) {
        super(page);
        graphicsOld = toErase;
        graphicsNew = toReCreate;
    }

    @Override
    public void execute() {

    }

    @Override
    public void revert() {

    }

    @Override
    public String toString() {
        return null;
    }

}
