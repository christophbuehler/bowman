package com.norizon.bowman;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import java.util.List;
import java.util.ArrayList;

import android.graphics.Path;
import android.view.Display;

/**
 * Created by cbuehler on 23.03.2015.
 * The in-game view of bowman.
 */
public class GameView extends View {
    private Paint paint = new Paint();

    private V dragStartPoint = new V(0, 0);
    private V dragEndPoint = new V(0, 0);

    private Camera mainCamera = new Camera();

    private Player currentPlayer;

    private Arrow shootingArrow;

    private Ai ai;

    private Path shapeOne, shapeTwo;

    private int playerYPosition = 170;

    private Point displaySize;

    // min and max player distance
    private int[] playerTwoDistance = new int[] { 700, 1700 };

    // min and max y position for arrows to stuck
    private int[] stuckY = new int[] { 210, 260 };

    // min and max ai think delay
    private int[] aiThinkTime = new int[] { 500, 1500 };

    // min and max force for ai arrows
    private int[] aiForce = new int[] { 40, 90 };

    // min and max angle for ai arrows
    private float[] aiAngle = new float[] { 1.9f, 2.6f };

    private List<Arrow> arrows = new ArrayList<Arrow>();

    boolean isDragging = false;

    private int difficulty;

    private Player playerOne, playerTwo;

    // playing against a computer / a real player
    private boolean againstComputer;

    private MainActivity context;

    /**
     * Start new game
     */
    public GameView(MainActivity context, int difficulty, boolean againstComputer) {
        super(context);

        Display display = context.getWindowManager().getDefaultDisplay();

        displaySize = new Point();
        display.getSize(displaySize);

        this.context = context;

        setBackgroundColor(-1);

        if (againstComputer)
            ai = new Ai(playerOne, playerTwo, difficulty, aiAngle, aiForce);

        this.againstComputer = againstComputer;

        this.difficulty = difficulty;

        playerOne = new Player(new V(playerYPosition, playerYPosition), false, this);
        playerTwo = new Player(new V(playerYPosition + playerTwoDistance[0] + (float) (Math.random() * (playerTwoDistance[1] - playerTwoDistance[0])), playerYPosition), true, this);

        shapeOne = getShape((int) playerOne.getPosition().getX());
        shapeTwo = getShape((int) playerTwo.getPosition().getX());

        currentPlayer = Math.random() > .5 ? playerOne : playerTwo;
        switchPlayer();

        paint.setStyle(Paint.Style.STROKE);
    }

    private void switchPlayer() {
        if (currentPlayer == playerOne) {
            currentPlayer = playerTwo;
            mainCamera.setSlopePosition(playerTwo.getPosition());

            if (!this.againstComputer) return;

            computerTurn();
            return;
        }

        currentPlayer = playerOne;
        mainCamera.setSlopePosition(playerOne.getPosition());
    }

    public void update(long time) {
        Player victim = playerOne == currentPlayer ? playerTwo : playerOne;
        V tmpPos;

        // update arrows
        for (Arrow arrow : arrows) {
            arrow.update();
        }

        // currently shooting
        if (shootingArrow != null) {

            // arrow hit ground
            if (shootingArrow.getPosition().getY() > stuckY[0] + (stuckY[1] - stuckY[0]) * Math.random()) {

                shootingArrow.stuck();
                shootingArrow = null;

                // update ai training data
                ai.setLastShotDamage(0);

                switchPlayer();
                mainCamera.setSlopePosition(currentPlayer.getPosition());
            } else {
                mainCamera.setSlopePosition(shootingArrow.getPosition());

                if (shootingArrow.getLastPosition() != null) {

                    tmpPos = shootingArrow.getTip(shootingArrow.getLastPosition());

                    // loop through all the positions, the arrow had
                    do {
                        tmpPos = tmpPos.add(shootingArrow.getPosition().subtract(tmpPos).getDirection());

                        if (!victim.checkArrowHit(tmpPos, true)) continue;

                        // player got hit

                        shootingArrow.setPosition(shootingArrow.getCenter(tmpPos));

                        shootingArrow.stuck();
                        shootingArrow = null;

                        if (victim.hasLost()) {
                            endGame(victim);
                        }

                        // update ai training data
                        ai.setLastShotDamage(victim.getLastDamage());

                        switchPlayer();
                        mainCamera.setSlopePosition(currentPlayer.getPosition());

                        break;
                    } while (shootingArrow.getPosition().distance(tmpPos) > 1);
                }
            }
        }

        mainCamera.update();

        playerOne.update(time);
        playerTwo.update(time);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mainCamera.draw(canvas);

        paint.setARGB(255, 180, 180, 180);

        // draw background
        canvas.drawLine(16, stuckY[0], 4000, stuckY[0], paint);

        // draw shape around players
        drawShape(canvas, paint, shapeOne);
        drawShape(canvas, paint, shapeTwo);

        // draw players
        playerOne.draw(canvas, paint);
        playerTwo.draw(canvas, paint);

        // draw arrows
        for (Arrow arrow : arrows) {
            arrow.draw(canvas, paint);
        }

        if (isDragging) {
            paint.setARGB(255, 140, 140, 140);

            paint.setStrokeWidth(1);

            canvas.drawLine((float) dragStartPoint.getX(), (float) dragStartPoint.getY(), (float) dragEndPoint.getX(), (float) dragEndPoint.getY(), paint);

            canvas.drawCircle(dragStartPoint.getX(), dragStartPoint.getY(), 1, paint);
            canvas.drawCircle(dragEndPoint.getX(), dragEndPoint.getY(), 1, paint);

            // draw angle
            drawAngle(canvas, paint, GeneralMethods.round(GeneralMethods.radToDeg(currentPlayer.getPhi()), 2));
        }

        invalidate();
    }

    private Path getShape(int xPos) {
        Path path = new Path();
        path.moveTo(xPos - 40, stuckY[0]);
        path.lineTo(xPos - 104, stuckY[0] + 20);
        path.lineTo(xPos - 40, stuckY[0] + 60);
        path.lineTo(xPos + 40, stuckY[0] + 60);
        path.lineTo(xPos + 104, stuckY[0] + 20);
        path.lineTo(xPos + 40, stuckY[0]);

        return path;
    }

    private void drawShape(Canvas canvas, Paint paint, Path path) {
        path.close();

        paint.setARGB(255, 200, 220, 240);

        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        canvas.drawPath(path, paint);
    }

    private void drawAngle(Canvas canvas, Paint paint, float phi) {
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(phi + "°", (float) (displaySize.x / 2), (float) 60, paint);
    }

    private void computerTurn() {
        TrainingData trainingData = ai.getShot();

        final double rad = trainingData.getRad();
        final float phi = trainingData.getPhi();

        final GameView gameView = this;

        playerTwo.setPhi(phi);
        playerTwo.aim();

        final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            private V velocity;

            @Override
            public void run()
            {
                velocity = new V(
                        (float) (rad * Math.cos(phi + Math.PI / 2)),
                        (float) (rad * Math.sin(phi + Math.PI / 2)));

                playerTwo.shoot();

                shootingArrow = new Arrow(new V(playerTwo.getPosition().getX(), playerTwo.getPosition().getY()), new V(-.5f, 5), velocity, gameView);

                arrows.add(shootingArrow);
            }
        }, (long) (aiThinkTime[0] + (aiThinkTime[1] - aiThinkTime[0]) * Math.random()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // computer turn no interaction allowed
        if (againstComputer && currentPlayer == playerTwo) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                // influence velocity of flying arrow by tapping
                if (shootingArrow != null) {

                    // tap on lower half of screen
                    if (event.getY() > displaySize.y / 2)
                        shootingArrow.setVelocity(shootingArrow.getVelocity().add(new V(0, 10)));
                    else
                        shootingArrow.setVelocity(shootingArrow.getVelocity().add(new V(0, -10)));

                    return true;
                }

                currentPlayer.aim();

                dragStartPoint.setX(event.getX());
                dragStartPoint.setY(event.getY());

                dragEndPoint.setX(event.getX());
                dragEndPoint.setY(event.getY());

                isDragging = true;
                break;
            case MotionEvent.ACTION_MOVE:

                if (shootingArrow != null) return true;

                dragEndPoint.setX(event.getX());
                dragEndPoint.setY(event.getY());

                currentPlayer.setPhi((float) Math.atan2(
                        dragStartPoint.getX() - dragEndPoint.getX(),
                        dragEndPoint.getY() - dragStartPoint.getY()));
                break;
            case MotionEvent.ACTION_UP:
                if (shootingArrow != null) return true;

                if (dragStartPoint.distance(dragEndPoint) < 10) break;

                // play shoot animation
                currentPlayer.shoot();

                shootingArrow = new Arrow(new V(currentPlayer.getPosition().getX(), currentPlayer.getPosition().getY()), new V(-.5f, 5), new V(
                        (float) ((dragStartPoint.getX() - dragEndPoint.getX()) / 2),
                        (float) ((dragStartPoint.getY() - dragEndPoint.getY())) / 2), this);

                ai.learnData(new TrainingData(
                        GeneralMethods.addToAngle(
                                (float) Math.PI,
                                -shootingArrow.getPhi()), shootingArrow.getRad(), 0));

                arrows.add(shootingArrow);

                isDragging = false;
        }

        invalidate();
        return true;
    }

    public void endGame(Player loser) {
        context.endGame(loser == playerTwo);
    }
}