package com.example.projectprm;

import android.os.Build;
import static com.example.projectprm.Music.checkPlaylist;

import android.content.Intent;
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
            setTheme(MainActivity.currentTheme[MainActivity.themeIndex]);
        }
        favouriteBinding = ActivityFavouriteBinding.inflate(getLayoutInflater());
        setContentView(favouriteBinding.getRoot());
        favouriteSongs = checkPlaylist(favouriteSongs);
        favouriteBinding.favouriteRV.setHasFixedSize(true);
        favouriteBinding.favouriteRV.setItemViewCacheSize(13);
        favouriteBinding.favouriteRV.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new FavouriteAdapter(this, favouriteSongs);
        favouriteBinding.favouriteRV.setAdapter(adapter);
        if (favouriteSongs.size() < 1) {
            favouriteBinding.shuffleBtnFA.setVisibility(View.INVISIBLE);
        }
        favouriteBinding.shuffleBtnFA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("index", 0);
                intent.putExtra("class", "FavouriteShuffle");
                startActivity(intent);
            }
        });
        favouriteBinding.backBtnFA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}