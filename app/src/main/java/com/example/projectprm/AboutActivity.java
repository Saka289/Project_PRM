package com.example.projectprm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import com.example.projectprm.databinding.ActivityAboutBinding;

public class AboutActivity extends AppCompatActivity {
    private ActivityAboutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex]);
        }
        binding = ActivityAboutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("About");
        binding.aboutText.setText(aboutText());

    }

    private String aboutText() {
        return "This music app was developed by Nam, Linh, Giang, Nhân" +
                "\n\nIf you want to provide feedback, we would love to hear from you.";
    }
}