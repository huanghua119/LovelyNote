package com.mephone.lovelynote.write;

import com.mephone.lovelynote.write.GraphicsControlpoint.Controlpoint;

import java.util.LinkedList;

public class TouchHandlerImage extends TouchHandlerControlpointABC {
    private static final String TAG = "TouchHandlerImage";

    protected TouchHandlerImage(HandwriterView view) {
        super(view, view.getOnlyPenInput());
    }

    /**
     * Called when the user touches with the pen
     */
    @Override
    protected void onPenDown(Controlpoint controlpoint, boolean isNew) {
        view.getToolBox().startControlpointMove(!isNew, true);
    }

    @Override
    protected LinkedList<? extends GraphicsControlpoint> getGraphicsObjects() {
        return getPage().images;
    }

    protected float maxDistanceControlpointScreen() {
        return 25f;
    }

    @Override
    protected void saveGraphics(GraphicsControlpoint graphics) {
        view.saveGraphics(graphics);
        GraphicsImage image = (GraphicsImage) graphics;
        view.callOnEditImageListener(image);
    }

    @Override
    protected void editGraphics(GraphicsControlpoint graphics) {
        graphics.restore();
        GraphicsImage image = (GraphicsImage) graphics;
        view.callOnEditImageListener(image);
    }

    @Override
    protected GraphicsControlpoint newGraphics(float x, float y, float pressure) {
        GraphicsImage image = new GraphicsImage(
                getPage().getTransform(), x, y);
        return image;
    }

    @Override
    protected void destroy() {
    }

}
