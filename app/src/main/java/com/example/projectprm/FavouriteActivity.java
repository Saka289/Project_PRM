package com.example.projectprm;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprm.databinding.ActivityFavouriteBinding;

public class FavouriteActivity extends AppCompatActivity {

    private ActivityFavouriteBinding favouriteBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.coolPink);
        favouriteBinding = ActivityFavouriteBinding.inflate(getLayoutInflater());
        setContentView(favouriteBinding.getRoot());
        favouriteBinding.backBtnFA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}