package com.example.projectprm;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprm.databinding.ActivityFavouriteBinding;

public class FavouriteActivity extends AppCompatActivity {

    private ActivityFavouriteBinding favouriteBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ProjectPRM);
        favouriteBinding = ActivityFavouriteBinding.inflate(getLayoutInflater());
        setContentView(favouriteBinding.getRoot());
    }
}