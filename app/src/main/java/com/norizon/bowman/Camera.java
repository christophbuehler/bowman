package com.norizon.bowman;

import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * Created by cbuehler on 26.03.2015.
 */
public class Camera {

    // screen center
    private V originalOffset = new V(160, 140);

    // the actual position of the camera
    private V position = new V(0, 0);

    // the position, the camera is moving to
    private V slopePosition = new V(0, 0);

    // the actual zoom of the camera
    private float zoom = 1;

    // the zoom, the camera is zooming to
    private float slopeZoom = 1;

    public void setSlopePosition(V slopePosition) {
        this.slopePosition = slopePosition;
    }

    public void setSlopeZoom(float slopeZoom) {
        this.slopeZoom = slopeZoom;
    }

    public Camera() {  }

    public void draw(Canvas canvas) {
        Matrix m = new Matrix();

        canvas.translate(-position.getX() + originalOffset.getX(), -position.getY() + originalOffset.getY());

        /*m.setScale(zoom, zoom);

        canvas.setMatrix(m);*/
    }

    public void update() {
        position = position.lerp(slopePosition);
        zoom = GeneralMethods.lerp(zoom, slopeZoom);
    }
}
