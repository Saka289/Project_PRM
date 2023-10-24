package com.example.projectprm;

import static com.example.projectprm.Music.setSongPosition;
import static com.example.projectprm.PlayerActivity.musicListPA;
import static com.example.projectprm.PlayerActivity.musicService;
import static com.example.projectprm.PlayerActivity.playerBinding;
import static com.example.projectprm.PlayerActivity.songPosition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ApplicationClass.PREVIOUS:
                prevNextSong(false,context);
                break;
            case ApplicationClass.PLAY:
                if(PlayerActivity.isPlaying)
                {
                    pauseMusic();
                }else{
                    playMusic();
                }
                break;
            case ApplicationClass.NEXT:
                prevNextSong(true,context);
                break;
            case ApplicationClass.EXIT:
                musicService.stopForeground(true);
                musicService = null;
                System.exit(1);
                break;
        }
    }

    private void playMusic(){
        PlayerActivity.isPlaying = true;
        musicService.mediaPlayer.start();
        musicService.showNotification(R.drawable.pause_icon);
        playerBinding.playPauseBtnPA.setIconResource(R.drawable.pause_icon);

    }

    private void pauseMusic(){
        PlayerActivity.isPlaying = false;
        musicService.mediaPlayer.pause();
        musicService.showNotification(R.drawable.play_icon);
        playerBinding.playPauseBtnPA.setIconResource(R.drawable.play_icon);

    }

    private void prevNextSong(boolean increment, Context context) {
        setSongPosition(increment);
        musicService.createMediaPlayer();
        Glide.with(context)
                .load(musicListPA.get(songPosition).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(playerBinding.songImgPA);
        playerBinding.songNamePA.setText(musicListPA.get(songPosition).getTitle());
        playMusic();
    }


}