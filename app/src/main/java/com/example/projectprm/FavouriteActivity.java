package com.example.projectprm;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.projectprm.databinding.ActivityFavouriteBinding;

import java.util.ArrayList;

public class FavouriteActivity extends AppCompatActivity {

    private ActivityFavouriteBinding favouriteBinding;
    private FavouriteAdapter adapter;
    public static ArrayList<Music> favouriteSongs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex]);
        }
        favouriteBinding = ActivityFavouriteBinding.inflate(getLayoutInflater());
        setContentView(favouriteBinding.getRoot());
        favouriteBinding.backBtnFA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        favouriteBinding.favouriteRV.setHasFixedSize(true);
        favouriteBinding.favouriteRV.setItemViewCacheSize(13);

        favouriteBinding.favouriteRV.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new FavouriteAdapter(this,favouriteSongs );
        favouriteBinding.favouriteRV.setAdapter(adapter);
    }
}