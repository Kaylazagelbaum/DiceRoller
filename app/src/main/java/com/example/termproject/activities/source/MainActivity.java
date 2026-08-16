package com.example.termproject.activities.source;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.termproject.R;
import androidx.lifecycle.ViewModelProvider;
import com.example.termproject.activities.models.TermProject;
import com.example.termproject.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

//for confetti effect
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import android.graphics.Color;
import nl.dionsegijn.konfetti.core.Position;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Snackbar mSnackBar;

    private String currentPlayer = "Player 1";
    private TextView turnIndicator;

    private TermProject game;
    private boolean showTurnIndicator;
    private boolean showConfetti;

    private Button[][] buttons = new Button[6][7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        game = new ViewModelProvider(this).get(TermProject.class);

        turnIndicator = binding.content.turnIndicator;

        GridLayout gridLayout = binding.content.gridLayout;
        gridLayout.setColumnCount(7);
        gridLayout.setRowCount(6);
        setup_game_grid(gridLayout);

        syncBoardFromModel();

        // Hamburger on the LEFT
        setupMenu();

        binding.fab.setOnClickListener(view -> {
            restartGame();
        });

    }

    private void syncBoardFromModel() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                int player = game.getCell(row, col);
                if (player == 1) {
                    buttons[row][col].setBackgroundResource(R.drawable.red_token);
                } else if (player == 2) {
                    buttons[row][col].setBackgroundResource(R.drawable.yellow_token);
                } else {
                    buttons[row][col].setBackgroundResource(R.drawable.token_circle);
                }
            }
        }
        updateTurnIndicator();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setupMenu() {
        binding.toolbar.setNavigationIcon(R.drawable.menu_icon);
        binding.toolbar.setNavigationOnClickListener(view -> {
            PopupMenu popupMenu =
                    new PopupMenu(MainActivity.this, binding.toolbar);
            popupMenu.getMenuInflater()
                    .inflate(R.menu.menu_main, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {

                int id = item.getItemId();
                if (id == R.id.action_settings) {
                    showSettings();
                    return true;
                } else if (id == R.id.action_about) {
                    showAbout();
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    private void restartGame() {
        // Reset board logic
        game.reset();
        // Reset UI
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                buttons[row][col].setBackgroundResource(R.drawable.token_circle);
            }
        }
        updateTurnIndicator();
        Snackbar.make(binding.getRoot(), "Game Restarted", Snackbar.LENGTH_SHORT).show();
    }

    private void updateTurnIndicator() {
        if (showTurnIndicator) {
            String text = (game.getCurrentPlayer() == 1) ? "Player 1's Turn" : "Player 2's Turn";
            turnIndicator.setText(text);
        } else {
            turnIndicator.setText("Connect Four Game");
        }
    }

    private void setup_game_grid(GridLayout gridLayout) {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Button circleButton = new Button(this);

                // Set circular background
                circleButton.setBackgroundResource(R.drawable.token_circle);

                // Set explicit size for the grid slots from dimens.xml
                int sizeInPx = getResources().getDimensionPixelSize(R.dimen.token_size);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = sizeInPx;
                params.height = sizeInPx;
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);

                int margin = getResources().getDimensionPixelSize(R.dimen.grid_margin);
                params.setMargins(margin, margin, margin, margin);

                circleButton.setLayoutParams(params);
                buttons[row][col] = circleButton;

                // Optional click listener
                final int r = row;
                final int c = col;

                circleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (game.isGameOver()) {
                            return;
                        }
                        int activePlayer = game.getCurrentPlayer();
                        int rowToPlace = game.placeToken(c);

                        // If column is full, return
                        if (rowToPlace == -1) {
                            return;
                        }

                        // Update the UI
                        Button targetButton = buttons[rowToPlace][c];
                        if (activePlayer == 1) {
                            targetButton.setBackgroundResource(R.drawable.red_token);
                        } else {
                            targetButton.setBackgroundResource(R.drawable.yellow_token);
                        }
                        updateTurnIndicator();
                        check_for_winner(activePlayer);
                    }
                });


                gridLayout.addView(circleButton);
            }
        }
    }

    private void check_for_winner(int activePlayer) {
        // Check if this player has won
        if (game.isGameOver()) {
            String winner = (activePlayer == 1) ? "Player 1" : "Player 2";
            if (showConfetti) {
                KonfettiView konfettiView = findViewById(R.id.konfettiView);
                explodeConfetti(konfettiView);
            }

            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Game Over!")
                    .setMessage(winner + " wins!")
                    .setPositiveButton("OK", null)
                    .setCancelable(false)
                    .show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (turnIndicator == null) {
            return;
        }
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        showTurnIndicator =
                preferences.getBoolean("show_turn_indicator", true);
        showConfetti =
                preferences.getBoolean(getString(R.string.confetti_key), true);
        updateTurnIndicator();
        turnIndicator.setVisibility(View.VISIBLE);
    }

    private void showSettings() {
        dismissSnackBarIfShown();

        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void showAbout() {
        dismissSnackBarIfShown();

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("About Connect Four")
                .setMessage("Connect Four is a fun two player game.\nTake to turns dropping one token into the board.\nThe first player to get four in a row wins!")
                .setPositiveButton("OK", null)
                .show();
    }


    private void dismissSnackBarIfShown() {
        if (mSnackBar != null && mSnackBar.isShown()) {
            mSnackBar.dismiss();
        }
    }
    private void explodeConfetti(KonfettiView konfettiView) {
        EmitterConfig emitterConfig = new Emitter(100L, TimeUnit.MILLISECONDS).max(100);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .spread(360)
                        .colors(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW))
                        .setSpeedBetween(0f, 30f)
                        .position(new Position.Relative(0.5, 0.3))
                        .build()
        );
    }

}



