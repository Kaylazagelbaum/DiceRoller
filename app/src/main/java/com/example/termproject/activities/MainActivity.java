package com.example.termproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.PopupMenu;

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

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        gridLayout.setColumnCount(7);
        gridLayout.setRowCount(6);

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
                circleButton.setOnClickListener(v -> {
                    // Handle button click at (r, c)
                });

                gridLayout.addView(circleButton);
            }
        }

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
}



