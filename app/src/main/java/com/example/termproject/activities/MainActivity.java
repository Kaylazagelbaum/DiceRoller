package com.example.termproject.activities;

import android.content.Intent;
import android.os.Bundle;
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



