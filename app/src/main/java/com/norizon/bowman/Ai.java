package com.norizon.bowman;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by Christoph on 03/29/2015.
 */
public class Ai {

    private Player playerEnemy, playerAi;

    private int difficulty;

    // minimum reference distance to enemy
    private int minEnemyDistance = 100;

    // min and max angle for ai arrows
    private float[] angle;

    // min and max force for ai arrows
    private int[] force;

    private float accuracyAngle = .3f;
    private float accuracyRad = 4f;

    private List<TrainingData> trainingDataList;

    public Ai(Player playerEnemy, Player playerAi, int difficulty, float[] angle, int[] force) {
        this.playerEnemy = playerEnemy;
        this.playerAi = playerAi;
        this.difficulty = difficulty;
        this.angle = angle;
        this.force = force;

        switch (difficulty) {
            case 0: // easy
                accuracyAngle = .6f;
                accuracyRad = 8f;
                break;
            case 1: // normal
                accuracyAngle = .4f;
                accuracyRad = 7f;
                break;
            case 2: // hard
                accuracyAngle = .1f;
                accuracyRad = 2f;
        }

        trainingDataList = new ArrayList<TrainingData>();
    }

    public void learnData(TrainingData trainingData) {
        trainingDataList.add(trainingData);
    }

    /*
    * Get the best shot from training data.
    * */
    public TrainingData getShot() {
        TrainingData currentShot, bestShot;

        // Log.d("DEBUG", "I am shooting.");

        // no data to play with
        if (trainingDataList.size() == 0) {
            currentShot = getRandomShot();
            trainingDataList.add(currentShot);

            return currentShot;
        }

        bestShot = trainingDataList.get(0);

        for (TrainingData trainingData : trainingDataList) {
            if (trainingData.getDmg() > bestShot.getDmg() &&
                    (difficulty == 2 ||                         // hard
                    (difficulty == 0 && Math.random() > .4) ||  // normal
                    (difficulty == 0 && Math.random() > .7)))   // easy
                bestShot = trainingData;
        }

        bestShot = bestShot.copy();

        switch (difficulty) {
            case 2: // hard

                // get the closest shot
                if (bestShot.getDmg() == 0) {

                    bestShot = trainingDataList.get(0);

                    for (TrainingData trainingData : trainingDataList) {
                        if (trainingData.getEnemyDistance() < bestShot.getEnemyDistance())
                            bestShot = trainingData;
                    }

                    if (bestShot.getEnemyDistance() < minEnemyDistance) {
                        bestShot = getRandomShot();
                    }
                }

                if (bestShot.getDmg() > 17) {
                    bestShot.setPhi(bestShot.getPhi() + ((float) Math.random() * accuracyAngle - .3f * accuracyAngle) * .2f);
                    bestShot.setRad(bestShot.getRad() + ((float) Math.random() * accuracyRad - .3f * accuracyRad) * .2f);
                    break;
                }
            case 0: // easy
            case 1: // normal
                if (bestShot.getDmg() == 0)
                    bestShot = getRandomShot();

                bestShot.setPhi(bestShot.getPhi() + (float) Math.random() * accuracyAngle - .5f * accuracyAngle);
                bestShot.setRad(bestShot.getRad() + (float) Math.random() * accuracyRad - .5f * accuracyRad);

                break;
        }

        trainingDataList.add(bestShot);

        return bestShot;
    }

    private TrainingData getRandomShot() {

        // completely random shot
        if (difficulty == 0)
            return new TrainingData(
                    (float) (angle[0] + (angle[1] - angle[0]) * Math.random()),
                    (float) (force[0] + Math.random() * (force[1] - force[0])), 0);

        // stronger force
        return new TrainingData(
                (float) (angle[0] + (angle[1] - angle[0]) * Math.random()),
                (float) (force[0] + (Math.random() * .5 + .5) * (force[1] - force[0])), 0);
    }

    public void setLastShotDamage(int damage) {
        trainingDataList.get(trainingDataList.size() - 1).setDmg(damage);
    }
}
