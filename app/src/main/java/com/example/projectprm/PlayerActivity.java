package com.example.projectprm;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprm.databinding.ActivityPlayerBinding;

public class PlayerActivity extends AppCompatActivity {
    private ActivityPlayerBinding playerBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ProjectPRM);
        playerBinding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(playerBinding.getRoot());
    }
}