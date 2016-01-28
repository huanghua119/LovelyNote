package com.mephone.lovelynote.write;

import java.util.LinkedList;

public interface GraphicsModifiedListener {
    public void onGraphicsCreateListener(Page page, Graphics toAdd);

    public void onGraphicsModifyListener(Page page, Graphics toRemove, Graphics toReplaceWith);

    public void onGraphicsEraseListener(Page page, Graphics toErase);

    public void onPageClearListener(Page page);

    public void onGraphicsDeleteListener(Page page, LinkedList<Graphics> toDelete);
}
