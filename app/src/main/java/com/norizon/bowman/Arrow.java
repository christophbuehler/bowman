package com.norizon.bowman;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.cbuehler.weartest.R;

/**
 * Created by Christoph on 03/28/2015.
 */
public class Arrow {
    private V position;

    // used for collision detection
    private V lastPosition;

    private V force;
    private V velocity;

    private V textureDimensions;

    private float phi;

    private boolean isStuck = false;

    private Bitmap image;

    public V getPosition() {
        return position;
    }
    public void setPosition(V v) { this.position = v; };

    public V getVelocity() { return velocity; };
    public void setVelocity(V v) { this.velocity = v; };

    public V getLastPosition() {
        return lastPosition;
    }

    public float getPhi() {
        phi = (float) Math.atan2(velocity.getX(), -velocity.getY());
        return phi;
    }

    public float getRad() {
        return (float) new V(0, 0).distance(velocity);
    }

    public void stuck() {
        this.isStuck = true;
    }

    public void update() {
        if (isStuck) return;

        this.velocity = this.velocity.add(this.force);

        this.lastPosition = this.position.copy();

        this.position = this.position.add(this.velocity);
    }

    public Arrow(V position, V force, V velocity, GameView view) {
        this.position = position;
        this.force = force;
        this.velocity = velocity;

        image = BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.arrow);
        textureDimensions = new V(image.getWidth(), image.getHeight());
    }

    public void draw(Canvas canvas, Paint paint) {
        Matrix rotator;

        rotator = new Matrix();

        phi = (float) Math.atan2(velocity.getX(), -velocity.getY());

        // rotate around center
        rotator.postRotate((float) (phi * 360 / (Math.PI * 2) - 90),
                textureDimensions.getX() / 2,
                textureDimensions.getY() / 2);

        rotator.postTranslate(
                position.getX() - textureDimensions.getX() / 2,
                position.getY() - textureDimensions.getY() / 2);

        // draw arrow
        canvas.drawBitmap(image, rotator, paint);
    }

    public V getTip(V pos) {
        float tmpPhi = (float) (phi - Math.PI / 2);

        return new V(
                (float) (pos.getX() + (textureDimensions.getX() / 2) * Math.cos(tmpPhi)),
                (float) (pos.getY() + (textureDimensions.getX() / 2) * Math.sin(tmpPhi)));
    }

    public V getCenter(V pos) {
        float tmpPhi = GeneralMethods.addToAngle((float) (phi - Math.PI / 2), (float) Math.PI);

        return new V(
                (float) (pos.getX() + (textureDimensions.getX() / 2) * Math.cos(tmpPhi)),
                (float) (pos.getY() + (textureDimensions.getX() / 2) * Math.sin(tmpPhi)));
    }
}
