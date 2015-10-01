package com.norizon.bowman;

/**
 * Created by Christoph on 03/29/2015.
 */
public class TrainingData {
    private float phi;
    private float rad;
    private float dmg;

    private int enemyDistance;

    public float getPhi() { return phi; }
    public float getRad() { return rad; }
    public float getDmg() { return dmg; }
    public int getEnemyDistance() { return enemyDistance; }

    public void setDmg(float dmg) { this.dmg = dmg; }
    public void setPhi(float phi) { this.phi = phi; }
    public void setRad(float rad) { this.rad = rad; }
    public void setEnemyDistance(int enemyDistance) { enemyDistance = enemyDistance; }

    public TrainingData(float phi, float rad, float dmg) {
        this.phi = phi;
        this.rad = rad;
        this.dmg = dmg;
    }



    public TrainingData copy() {
        return new TrainingData(phi, rad, dmg);
    }
}
