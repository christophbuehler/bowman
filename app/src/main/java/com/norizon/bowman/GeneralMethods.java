package com.norizon.bowman;

/**
 * Created by Christoph on 03/29/2015.
 */
public abstract class GeneralMethods {
    static float addToAngle(float angle, float addAngle) {
        float newAngle = angle + addAngle;
        return (float) ((newAngle < 0) ? Math.PI * 2 - newAngle : newAngle % (Math.PI * 2));
    }

    static float lerp(float from, float reference) {
        return (reference - from) / 2 + from;
    }

    static float radToDeg(float rad) { return rad * 57.295f; }

    static float round(float number, int decimals) { return (float) (((int) (Math.pow(10, decimals) * number)) / Math.pow(10, decimals)); }
}
