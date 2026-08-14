package com.example.termproject.activities.source;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.termproject.R;
import com.example.termproject.activities.models.TermProject;
import com.example.termproject.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import static nl.dionsegijn.konfetti.core.Position.Relative;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Snackbar mSnackBar;

    private String currentPlayer = "Player 1";
    private TextView turnIndicator;

    private TermProject game = new TermProject();
    private boolean showTurnIndicator;

    private Button[][] buttons = new Button[6][7];
    private boolean confettiEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        turnIndicator = binding.content.turnIndicator;

        GridLayout gridLayout = binding.content.gridLayout;
        gridLayout.setColumnCount(7);
        gridLayout.setRowCount(6);
        setup_game_grid(gridLayout);

        // Hamburger on the LEFT
        setupMenu();

        binding.fab.setOnClickListener(view -> {
            restartGame();
        });
    }

    private void setupMenu() {
        binding.toolbar.setNavigationIcon(R.drawable.menu_icon);

        binding.toolbar.setNavigationIconTint(
                getColor(R.color.menu_icon_color)
        );

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

                // Set explicit size for the grid slots (e.g., 60dp)
                int sizeInPx = (int) (50 * getResources().getDisplayMetrics().density);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = sizeInPx;
                params.height = sizeInPx;
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);

                // Optional margins between buttons
                int margin = (int) (2 * getResources().getDisplayMetrics().density);
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

                        // Check if this player has won
                        if (game.isGameOver()) {

                            String winner = (activePlayer == 1)
                                    ? "Player 1"
                                    : "Player 2";

                            // Check confetti preference
                            SharedPreferences preferences =
                                    PreferenceManager
                                            .getDefaultSharedPreferences(
                                                    MainActivity.this
                                            );

                            boolean confettiEnabled =
                                    preferences.getBoolean(
                                            getString(
                                                    R.string.confetti_key
                                            ),
                                            true
                                    );


                            // Show confetti if enabled
                            if (confettiEnabled) {
                                showConfetti();
                            }


                            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                                    .setTitle("Game Over!")
                                    .setMessage(winner + " wins!")
                                    .setPositiveButton("OK", null)
                                    .setCancelable(false)
                                    .show();
                        }
                    }
                });


                gridLayout.addView(circleButton);
            }
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
                .setMessage("Connect Four is a fun two player game.\nTake turns dropping one token into the board.\nThe first player to get four in a row wins!")
                .setPositiveButton("OK", null)
                .show();
    }


    private void dismissSnackBarIfShown() {
        if (mSnackBar != null && mSnackBar.isShown()) {
            mSnackBar.dismiss();
        }
    }
    private void showConfetti() {

        EmitterConfig emitterConfig =
                new Emitter(
                        3,
                        TimeUnit.SECONDS
                ).perSecond(30);

        Party party =
                new PartyFactory(emitterConfig)

                        // Shoot upward
                        .angle(Angle.TOP)

                        // Spread confetti across the screen
                        .spread(Spread.WIDE)

                        // Confetti speed
                        .setSpeedBetween(
                                10f,
                                30f
                        )

                        // Confetti colours
                        .colors(
                                Arrays.asList(
                                        0xFFFF0000, // Red
                                        0xFFFFFF00, // Yellow
                                        0xFF0000FF, // Blue
                                        0xFF00FF00, // Green
                                        0xFFFF00FF  // Magenta
                                )
                        )// Confetti shapes
                        .shapes(
                                Arrays.asList(
                                        Shape.Square.INSTANCE,
                                        Shape.Circle.INSTANCE
                                )
                        )

                        // Start from the top-middle
                        .position(
                                new Relative(
                                        0.5,
                                        0.0
                                )
                        )

                        .build();

        binding.konfettiView.start(party);
    }



    }