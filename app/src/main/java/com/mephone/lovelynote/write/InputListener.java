package com.mephone.lovelynote.write;

import java.util.UUID;

public interface InputListener {
    void onStrokeFinishedListener();

    void onPickImageListener(GraphicsImage image);

    void onEditImageListener(GraphicsImage image);

}
