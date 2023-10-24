package com.example.projectprm;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprm.databinding.ActivityPlaylistBinding;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {

    private ActivityPlaylistBinding playlistBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.coolPink);
        playlistBinding = ActivityPlaylistBinding.inflate(getLayoutInflater());
        setContentView(playlistBinding.getRoot());

    }
}