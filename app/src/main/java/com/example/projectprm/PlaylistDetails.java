package com.example.projectprm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import com.google.gson.Gson;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectprm.databinding.ActivityPlaylistDetailsBinding;
import com.google.gson.GsonBuilder;

public class PlaylistDetails extends AppCompatActivity {
    private ActivityPlaylistDetailsBinding binding;
    private MusicAdapter adapter;
    public static int currentPlaylistPos = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistDetailsBinding.inflate(getLayoutInflater());
        setTheme(R.style.coolPink);
        setContentView(binding.getRoot());
        currentPlaylistPos = getIntent().getIntExtra("index", -1);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                PlaylistActivity.musicPlaylist.ref.get(currentPlaylistPos).playlist.addAll(MainActivity.MusicListMA);
            }

        } catch (Exception e) {
        }
        binding.playlistDetailsRV.setItemViewCacheSize(10);
        binding.playlistDetailsRV.setHasFixedSize(true);
        binding.playlistDetailsRV.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MusicAdapter(this, PlaylistActivity.musicPlaylist.ref.get(currentPlaylistPos).playlist, true);
        binding.playlistDetailsRV.setAdapter(adapter);

        binding.backBtnPD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        binding.playlistNamePD.setText(PlaylistActivity.musicPlaylist.ref.get(currentPlaylistPos).name);
        binding.moreInfoPD.setText("Total " + adapter.getItemCount() + " Songs.\n\n" +
                "Created On:\n" + PlaylistActivity.musicPlaylist.ref.get(currentPlaylistPos).createdOn + "\n\n" +
                "  -- " + PlaylistActivity.musicPlaylist.ref.get(currentPlaylistPos).createdBy);

        if (adapter.getItemCount() > 0) {
            Glide.with(this)
                    .load(PlaylistActivity.musicPlaylist.ref.get(currentPlaylistPos).playlist.get(0).getArtUri())
                    .apply(new RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
                    .into(binding.playlistImgPD);
            binding.shuffleBtnPD.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();

        // for storing favorites data using shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit();
        String jsonStringPlaylist = new GsonBuilder().create().toJson(PlaylistActivity.musicPlaylist);
        editor.putString("MusicPlaylist", jsonStringPlaylist);
        editor.apply();
    }
}