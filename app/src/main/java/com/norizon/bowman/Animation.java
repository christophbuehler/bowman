package com.norizon.bowman;

/**
 * Created by Christoph on 03/26/2015.
 */
public class Animation {
    private int[] indexes;
    private int state = 0;
    private Runnable onComplete;

    public void setState(int state) {
        this.state = state;
    }

    public int getNextIndex() {

        state++;

        // animation ended
        if (state == indexes.length - 1) {
            onComplete.run();
            return indexes[indexes.length - 1];
        }

        state = state % indexes.length;

        return indexes[state];
    }

    public Animation(int[] indexes, Runnable onComplete) {
        this.indexes = indexes;
        this.onComplete = onComplete;
    }
}
