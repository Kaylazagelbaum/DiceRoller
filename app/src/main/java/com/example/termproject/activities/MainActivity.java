package com.example.termproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.termproject.R;
import com.example.termproject.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Snackbar mSnackBar;

    private String currentPlayer = "Player 1";
    private TextView turnIndicator;

    boolean isPlayer1Turn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars =
                    insets.getInsets(WindowInsetsCompat.Type.systemBars());

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );

            return insets;
        });

        setSupportActionBar(binding.toolbar);

        turnIndicator = binding.content.turnIndicator;

        GridLayout gridLayout = binding.content.gridLayout;
        gridLayout.setColumnCount(7);
        gridLayout.setRowCount(6);

        setup_game_grid(gridLayout);

        // Hamburger on the LEFT
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

        binding.fab.setOnClickListener(view -> {
            mSnackBar = Snackbar.make(
                    view,
                    "Replace with your own action",
                    Snackbar.LENGTH_LONG
            );

            mSnackBar.setAction("Action", null);
            mSnackBar.show();
        });
    }

    private void setup_game_grid(GridLayout gridLayout) {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                Button circleButton = new Button(this);

                // Set circular background
                circleButton.setBackgroundResource(R.drawable.token_circle);

                // Set explicit size for the grid slots (e.g., 60dp)
                int sizeInPx = (int) (60 * getResources().getDisplayMetrics().density);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = sizeInPx;
                params.height = sizeInPx;
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);

                // Optional margins between buttons
                int margin = (int) (4 * getResources().getDisplayMetrics().density);
                params.setMargins(margin, margin, margin, margin);

                circleButton.setLayoutParams(params);

                // Optional click listener
                final int r = row;
                final int c = col;
                circleButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isPlayer1Turn) {
                            circleButton.setBackgroundResource(R.drawable.red_token);
                            turnIndicator.setText("Player 2's Turn");
                        } else {
                            circleButton.setBackgroundResource(R.drawable.yellow_token);
                            turnIndicator.setText("Player 1's Turn");
                        }
                        isPlayer1Turn = !isPlayer1Turn;
                    }});


                gridLayout.addView(circleButton);
            }
        }
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
                .setMessage("We have to write something here")
                .setPositiveButton("OK", null)
                .show();
    }


    private void dismissSnackBarIfShown() {
        if (mSnackBar != null && mSnackBar.isShown()) {
            mSnackBar.dismiss();
        }
    }
    
    private boolean checkWin(int[][] board, int row, int col, int player) {
        return checkHorizontal(board, row, player) ||
                checkVertical(board, col, player) ||
                checkDiagonal1(board, row, col, player) ||
                checkDiagonal2(board, row, col, player);

    }

    private boolean checkDiagonal2(int[][] board, int row, int col, int player) {
        int count = 0;
        int r = row;
        int c = col;

        while (r < 5 && c > 0) {
            r++;
            c--;
        }
        while (r >= 0 && c < 7) {
            if (board[r][c] == player) {
                count++;
                if (count == 4)
                    return true;
            } else {
                count = 0;
            }
            r--;
            c++;
        }
        return false;
    }

    private boolean checkDiagonal1(int[][] board, int row, int col, int player) {
        int count = 0;
        int r = row;
        int c = col;

        while (r > 0 && c > 0) {
            r--;
            c--;
        }
        while (r < 6 && c < 7) {
            if (board[r][c] == player) {
                count++;
                if (count == 4) {
                    return true;
                }
            } else {
                count = 0;
            }
            r++;
            c++;
        }
        return false;
    }

    private boolean checkVertical(int[][] board, int col, int player) {
        int count = 0;
        for (int row = 0; row < 6; row++) {
            if (board[row][col] == player) {
                count++;
                if (count == 4) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }
    

    private boolean checkHorizontal(int[][] board, int row, int player) {
        int count = 0;
        for (int col = 0; col < 7; col++) {
            if (board[row][col] == player) {
                count++;
                if (count == 4) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }
}



