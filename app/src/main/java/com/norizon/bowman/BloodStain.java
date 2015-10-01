package com.norizon.bowman;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by Christoph on 03/29/2015.
 */
public class BloodStain {
    private V position;
    private float dropHeight;
    private List<V> bloodPoints;

    private float force = .8f;
    private int size;

    public BloodStain(V position, float dropHeight) {
        this.position = position;
        this.dropHeight = dropHeight;

        size = (int) (dropHeight / 4) + 1;

        bloodPoints = new ArrayList<V>();
    }

    public void update() {
        if (size > bloodPoints.size() && Math.random() > .90)
            bloodPoints.add(this.position.copy());

        for (V point : bloodPoints) {
            if (point.getY() - position.getY() > dropHeight) {
                point.setY(position.getY());
                continue;
            }

            point.setY(point.getY() + force);
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(Color.RED);

        for (V point : bloodPoints)
            canvas.drawCircle(point.getX(), point.getY(), 2, paint);
    }
}
