package com.mephone.lovelynote.write;

import android.content.Context;
import android.widget.EditText;

public class HandwriterEditText extends EditText {

    private TouchHandlerText mHandlerText = null;

    public HandwriterEditText(Context context, TouchHandlerText text) {
        super(context);
        mHandlerText = text;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
    }
}
