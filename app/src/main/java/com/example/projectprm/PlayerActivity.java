package com.example.projectprm;

import static com.example.projectprm.Music.setSongPosition;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectprm.databinding.ActivityPlayerBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class PlayerActivity extends AppCompatActivity implements ServiceConnection {
    public static ActivityPlayerBinding playerBinding;

    public static ArrayList<Music> musicListPA;
    public static int songPosition;

    public static boolean isPlaying = false;

    public static MusicService musicService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.coolPink);
        playerBinding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(playerBinding.getRoot());
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        startService(intent);
        initializeLayout();
        playerBinding.playPauseBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    pauseMusic();
                }else{
                    playMusic();
                }

            }
        });
        playerBinding.previousBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevNextSong(false);
            }
        });
        playerBinding.nextBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevNextSong(true);
            }
        });
    }

    private void setLayout(){
        Glide.with(this)
                .load(musicListPA.get(songPosition).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(playerBinding.songImgPA);
        playerBinding.songNamePA.setText(musicListPA.get(songPosition).getTitle());
    }

    private void createMediaPlayer(){
        if(musicService.mediaPlayer == null) {
            musicService.mediaPlayer = new MediaPlayer();
        }
        musicService.mediaPlayer.reset();

        try {
            musicService.mediaPlayer.setDataSource(musicListPA.get(Integer.parseInt(String.valueOf(songPosition))).getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            musicService.mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        musicService.mediaPlayer.start();
        isPlaying = true;
        playerBinding.playPauseBtnPA.setIconResource(R.drawable.pause_icon);
        musicService.showNotification(R.drawable.pause_icon);
    }

    private void initializeLayout(){
        songPosition = getIntent().getIntExtra("index",0);
        String songPosition = getIntent().getStringExtra("class");
        switch (songPosition) {
            case "MusicAdapter":
                musicListPA = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                {
                    musicListPA.addAll(MainActivity.MusicListMA);
                }
                setLayout();

                break;

            case "MainActivity":
                musicListPA = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    musicListPA.addAll(MainActivity.MusicListMA);
                }
                Collections.shuffle(musicListPA);
                setLayout();

                break;
        }
    }
    private void playMusic(){

        playerBinding.playPauseBtnPA.setIconResource(R.drawable.pause_icon);
        musicService.showNotification(R.drawable.pause_icon);
        isPlaying = true;
        musicService.mediaPlayer.start();
    }
    private void pauseMusic(){
        playerBinding.playPauseBtnPA.setIconResource(R.drawable.play_icon);
        musicService.showNotification(R.drawable.play_icon);
        isPlaying = false;
        musicService.mediaPlayer.pause();
    }
    private void prevNextSong(boolean increment){
        if(increment){
            setSongPosition(true);
        }else{
            setSongPosition(false);
        }
        setLayout();
        createMediaPlayer();
    }



    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.currentService();
        createMediaPlayer();

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }
}