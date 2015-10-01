package com.norizon.bowman;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.os.Handler;

import com.example.cbuehler.weartest.R;

public class MainActivity extends Activity {
    private GameView gameView;
    private int updateTimeout = 60;

    // default difficulty is set to easy
    private int difficulty = 0;

    Button newGameButton, difficultyBtn, gameEndedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                showMainMenu();
            }
        });

        final Handler h = new Handler();
        h.postDelayed(new Runnable()
        {
            private long time = 0;

            @Override
            public void run()
            {
                time += updateTimeout;

                update(time);
                h.postDelayed(this, updateTimeout);
            }
        }, updateTimeout);
    }

    private void update(long time) {
        if (gameView == null) return;

        gameView.update(time);
    }

    private void activityLoaded() {

    }

    private void setDifficulty(int difficulty) {
        this.difficulty = difficulty;

        switch (difficulty) {
            case 0: // easy
                difficultyBtn.setText(R.string.difficulty_easy_text);
                difficultyBtn.setBackgroundColor(getResources().getColor(R.color.difficulty_easy_color));
            break;
            case 1: // normal
                difficultyBtn.setText(R.string.difficulty_normal_text);
                difficultyBtn.setBackgroundColor(getResources().getColor(R.color.difficulty_normal_color));
            break;
            case 2: // hard
                difficultyBtn.setText(R.string.difficulty_hard_text);
                difficultyBtn.setBackgroundColor(getResources().getColor(R.color.difficulty_hard_color));
        }
    }

    private void showMainMenu() {
        newGameButton = (Button) findViewById(R.id.new_game_btn);
        difficultyBtn = (Button) findViewById(R.id.difficulty_btn);

        // start new game
        newGameButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewGame();
            }
        });

        // change difficulty
        difficultyBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setDifficulty((difficulty + 1) % 3);
            }
        });
    }

    private void loadNewGame() {
        gameView = new GameView(this, difficulty, true);
        setContentView(gameView);
    }

    public void endGame(boolean playerWon) {
        setContentView(R.layout.game_ended_activity);

        gameEndedBtn = (Button) findViewById(R.id.ok_btn);

        if (playerWon) {
            gameEndedBtn.setText(R.string.game_won_text);
            gameEndedBtn.setBackgroundColor(getResources().getColor(R.color.game_won_color));
        }

        // start new game
        gameEndedBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_main);

                final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

                stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
                    @Override
                    public void onLayoutInflated(WatchViewStub stub) {
                        showMainMenu();
                        setDifficulty(difficulty);
                    }
                });
            }
        });
    }
}
