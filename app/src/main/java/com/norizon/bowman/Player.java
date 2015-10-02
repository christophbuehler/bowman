package com.norizon.bowman;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.cbuehler.weartest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cbuehler on 23.03.2015.
 */
public class Player {
    private V position;

    private Bitmap[] images = new Bitmap[5];

    private List<BloodStain> bloodStains;

    private V aimingRotationPivot = new V(32, 30);

    private V textureDimensions;

    private int hp = 80;
    private int hpAnimation = hp;

    private boolean lost = false;

    private boolean isFlipped;

    private int lastDamage = 0;

    private Bitmap base, state01, state02, state03, state04;

    private Animation
        stillAnim,  // character does nothing (like a potato)
        aimAnim,    // character is aiming
        shootAnim;  // character shoots the arrow

    private int currentImageIndex = 1;

    private Animation currentAnim;
    private float phi = 0;

    private GameView view;

    private long gameTime = 0;

    public boolean hasLost() { return lost; }

    public V getPosition() { return position; }
    public int getLastDamage() { return lastDamage; }

    public float getPhi() { return phi; }
    public void setPhi(float phi) {
        this.phi = phi;
    }

    public void aim() {
        aimAnim.setState(0);
        currentAnim = aimAnim;
    }

    public void shoot() {
        currentAnim = shootAnim;
    }

    public void update(long time) {
        gameTime = time;
        currentImageIndex = currentAnim.getNextIndex();

        // update blood
        for (BloodStain bloodStain : bloodStains) {
            bloodStain.update();
        }

        if (hp == hpAnimation) return;
        hpAnimation += hpAnimation > hp ? -1 : 1;
    }

    public Player(V position, boolean isFlipped, GameView view) {
        this.view = view;

        this.position = position;
        this.isFlipped = isFlipped;

        bloodStains = new ArrayList<BloodStain>();

        images[0] = BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.base);
        images[1] = BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.state01);
        images[2] = BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.state02);
        images[3] = BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.state03);
        images[4] = BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.state04);

        textureDimensions = new V(images[0].getWidth(), images[0].getHeight());

        if (isFlipped) {
            // aimingRotationOffset.setX(-aimingRotationOffset.getX());
            aimingRotationPivot.setX(textureDimensions.getX() - aimingRotationPivot.getX());
        }

        if (isFlipped)
            for (int i=0; i<images.length; i++)
                images[i] = flip(images[i]);

        stillAnim = new Animation(new int[] { 1 }, new Runnable() {

            @Override
            public void run() {
                currentAnim = stillAnim;
            }
        });

        aimAnim = new Animation(new int[] { 2, 2, 2, 3 }, new Runnable() {

            @Override
            public void run() {

                // loading animation finished
                // ensure player is aiming
                currentImageIndex = currentAnim.getNextIndex();
                currentImageIndex = currentAnim.getNextIndex();
                currentImageIndex = currentAnim.getNextIndex();
            }
        });

        shootAnim = new Animation(new int[] { 4, 4 }, new Runnable() {

            @Override
            public void run() {
                currentAnim = stillAnim;
            }
        });

        currentAnim = stillAnim;
        update(0);
    }

    Bitmap flip(Bitmap src) {
        Matrix m = new Matrix();
        m.preScale(-1, 1);

        Bitmap dst = Bitmap.createBitmap(src, 0, 0, (int) src.getWidth(), (int) src.getHeight(), m, false);

        return dst;
    }

    public void draw(Canvas canvas, Paint paint) {
        Matrix rotator;

        // base
        canvas.drawBitmap(images[0], position.getX() - textureDimensions.getX() / 2, position.getY() - textureDimensions.getY() / 2, paint);

        switch (currentImageIndex) {
            case 1: // stand still

                // animate body
                canvas.drawBitmap(images[currentImageIndex],
                        position.getX() - textureDimensions.getX() / 2,
                        (float) (getFluentGameTime(.006, 2) - 2 + position.getY() - textureDimensions.getY() / 2), paint);
            break;
            case 2: // loading
                canvas.drawBitmap(images[currentImageIndex], position.getX() - textureDimensions.getX() / 2, position.getY() - textureDimensions.getY() / 2, paint);
            break;
            case 3: // aiming
            case 4: // shooting

                // rotate the image by phi
                rotator = new Matrix();

                rotator.postRotate((float) (phi * 360 / (Math.PI * 2) - 90),
                        aimingRotationPivot.getX(),
                        aimingRotationPivot.getY());

                rotator.postTranslate(
                    position.getX() - textureDimensions.getX() / 2,
                    position.getY() - textureDimensions.getY() / 2);

                canvas.drawBitmap(images[currentImageIndex], rotator, paint);
        }

        // draw health bar
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);

        // bounding box
        paint.setColor(Color.rgb(180, 180, 180));
        canvas.drawRect(
                position.getX() - 40, position.getY() - textureDimensions.getY() / 2 - 20,
                position.getX() + 40, position.getY() - textureDimensions.getY() / 2 - 10, paint);

        // health
        paint.setColor(Color.rgb(250, 100, 100));
        canvas.drawRect(
                position.getX() - 40, position.getY() - textureDimensions.getY() / 2 - 20,
                position.getX() - 40 + hpAnimation, position.getY() - textureDimensions.getY() / 2 - 10, paint);

        // draw blood
        for (BloodStain bloodStain : bloodStains) {
            bloodStain.draw(canvas, paint);
        }
    }

    public boolean checkArrowHit(V arrowTip, boolean makesDamage) {
        V relPos;
        int pixel;
        int damage;

        // bounding box collision
        if (arrowTip.getX() < position.getX() - textureDimensions.getX() / 2) return false;
        if (arrowTip.getY() < position.getY() - textureDimensions.getY() / 2) return false;
        if (arrowTip.getX() > position.getX() + textureDimensions.getX() / 2) return false;
        if (arrowTip.getY() > position.getY() + textureDimensions.getY() / 2) return false;

        // per pixel collision
        relPos = arrowTip.subtract(new V(
                (float) (position.getX() - textureDimensions.getX() / 2),
                (float) (position.getY() - textureDimensions.getY() / 2)));

        pixel = Color.alpha(images[0].getPixel((int) relPos.getX(), (int) relPos.getY()));

        // player got hit
        if (pixel == 0) return false;

        if (!makesDamage) return true;

        damage = (int) ((textureDimensions.getY() - relPos.getY()) / (textureDimensions.getY() / 20));

        bloodStains.add(new BloodStain(arrowTip.copy(), damage + 4));

        this.hp = Math.max(0, hp - damage);

        lastDamage = damage;

        if (this.hp != 0) return true;

        lost = true;

        return true;
    }

    private float getFluentGameTime(double speed, double rad) {
        return (float) (rad * Math.sin(gameTime * speed));
    }
}
