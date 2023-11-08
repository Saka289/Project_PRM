package com.example.projectprm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import com.example.projectprm.databinding.ActivitySettingsBinding;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.internal.GsonBuildConfig;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

   // private SwitchCompat switchMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex]);
        }

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Settings");



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            switch (MainActivity.themeIndex) {
                case 0:
                    binding.coolPinkTheme.setBackgroundColor(Color.YELLOW);
                    break;
                case 1:
                    binding.coolBlueTheme.setBackgroundColor(Color.YELLOW);
                    break;
                case 2:
                    binding.coolPurpleTheme.setBackgroundColor(Color.YELLOW);
                    break;
                case 3:
                    binding.coolGreenTheme.setBackgroundColor(Color.YELLOW);
                    break;
                case 4:
                    binding.coolBlackTheme.setBackgroundColor(Color.YELLOW);
                    break;
            }
        }


        binding.coolPinkTheme.setOnClickListener(v -> saveTheme(0));
        binding.coolBlueTheme.setOnClickListener(v -> saveTheme(1));
        binding.coolPurpleTheme.setOnClickListener(v -> saveTheme(2));
        binding.coolGreenTheme.setOnClickListener(v -> saveTheme(3));
        binding.coolBlackTheme.setOnClickListener(v -> saveTheme(4));
        binding.versionName.setText(setVersionDetails());


        binding.sortBtn.setOnClickListener(v -> {
            String[] menuList = {"Recently Added", "Song Title", "File Size"};
            final int[] currentSort = {0};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                currentSort[0] = MainActivity.sortOrder;
            }
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Sorting")
                    .setPositiveButton("OK", (dialog, which) -> {
                        SharedPreferences.Editor editor = getSharedPreferences("SORTING", MODE_PRIVATE).edit();
                        editor.putInt("sortOrder", currentSort[0]);
                        editor.apply();
                    })
                    .setSingleChoiceItems(menuList, currentSort[0], (dialog, which) -> {
                        currentSort[0] = which;
                    });

            AlertDialog customDialog = builder.create();
            customDialog.show();
            setDialogBtnBackground(this, customDialog);
        });


    }


    private void saveTheme(int index) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (MainActivity.themeIndex != index) {
                SharedPreferences.Editor editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit();
                editor.putInt("themeIndex", index);
                editor.apply();

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle("Apply Theme")
                        .setMessage("Do you want to apply theme?")
                        .setPositiveButton("Yes", (dialog, which) -> Music.exitApplication())
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss());


                AlertDialog customDialog = builder.create();
                customDialog.show();
                setDialogBtnBackground(this, customDialog);
            }
        }
    }

    private void setDialogBtnBackground(Context context, AlertDialog dialog) {
        // Setting button text color
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
        );
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
        );

        // Setting button background color
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
        );
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(
                MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
        );
    }




    private String setVersionDetails() {
        return "Version Name: " + GsonBuildConfig.VERSION;
    }


}