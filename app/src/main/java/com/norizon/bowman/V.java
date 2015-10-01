package com.norizon.bowman;

/**
 * Vector methods
 */
public class V {
    private float x, y;

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }

    public V(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public double distance(V p) {
        double x = this.x - p.x,
               y = this.y - p.y;

        return Math.sqrt(x * x + y * y);
    }

    public V lerp(V reference) {
        return reference.subtract(this).div(new V(2, 2)).add(this);
    }

    public V div(V v) {
        return new V(this.x / v.x, this.y / v.y);
    }

    public V add(V v) {
        return new V(this.x + v.x, this.y + v.y);
    }

    public V subtract(V v) {
        return new V(this.x - v.x, this.y - v.y);
    }

    public V copy() {
        return new V(this.x, this.y);
    }

    public V getDirection() {
        return new V(
                this.x > 0 ? 1 : (this.x < 0) ? -1 : 0,
                this.y > 0 ? 1 : (this.y < 0) ? -1 : 0);
    }

    public V dot(V v) {
        return new V(this.x * v.x, this.y * v.y);
    }

    public boolean equals(V v) {
        return v.getX() == this.getX() && v.getY() == this.getY();
    }
}
