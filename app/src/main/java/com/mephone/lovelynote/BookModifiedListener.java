package com.mephone.lovelynote;

import com.mephone.lovelynote.write.Page;

public interface BookModifiedListener {
    public void onPageInsertListener(Page page, int position);

    public void onPageDeleteListener(Page page, int position);
}
