package com.mephone.lovelynote.image;

import java.io.File;

public abstract class ThreadBase extends Thread {
    private final static String TAG = "ThreadBase";

    protected DialogBase fragment;
    protected int progress = 0;
    protected boolean finished = false;

    protected final File file;

    protected ThreadBase(File file) {
        this.file = file;
    }

    protected void incrementProgress() {
        progress++;
    }

    protected int getProgress() {
        return progress;
    }

    protected synchronized void setDialog(DialogBase dialog) {
        this.fragment = dialog;
    }

    protected synchronized void toast(int resId, Object... values) {
        if (fragment != null)
            fragment.toast(resId, values);
    }

    protected synchronized void toast(String s) {
        if (fragment != null)
            fragment.toast(s);
    }

    public void run() {
        worker();
        finished = true;
    }

    public boolean isFinished() {
        return finished;
    }

    protected void debuggingDelay() {
        return;
//		try {
//			sleep(100);
//		} catch (InterruptedException e) {}
    }


    abstract protected void worker();

}
