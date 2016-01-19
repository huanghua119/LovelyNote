package com.mephone.lovelynote;

import com.mephone.lovelynote.write.Page;

abstract public class Command {
    private final static String TAG = "Command";

    private Page page;

    protected Command(Page currentPage) {
        page = currentPage;
    }

    public Page getPage() {
        return page;
    }

    abstract public void execute();

    abstract public void revert();

    abstract public String toString();
}
