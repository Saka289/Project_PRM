package com.example.projectprm;

import static com.example.projectprm.Music.exitApplication;
import static com.example.projectprm.Music.setSongPosition;
import static com.example.projectprm.PlayerActivity.musicListPA;
import static com.example.projectprm.PlayerActivity.musicService;
import static com.example.projectprm.PlayerActivity.playerBinding;
import static com.example.projectprm.PlayerActivity.songPosition;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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
                exitApplication();
                break;
        }
    }

    private void playMusic(){
        PlayerActivity.isPlaying = true;
        musicService.mediaPlayer.start();
        musicService.showNotification(R.drawable.pause_icon);
        playerBinding.playPauseBtnPA.setIconResource(R.drawable.pause_icon);
        /*NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon);*/
    }

    private void pauseMusic(){
        PlayerActivity.isPlaying = false;
        musicService.mediaPlayer.pause();
        musicService.showNotification(R.drawable.play_icon);
        playerBinding.playPauseBtnPA.setIconResource(R.drawable.play_icon);
        /*NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon);*/
    }

    private void prevNextSong(boolean increment, Context context) {
        setSongPosition(increment);
        musicService.createMediaPlayer();
        Glide.with(context)
                .load(musicListPA.get(songPosition).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(playerBinding.songImgPA);
        playerBinding.songNamePA.setText(musicListPA.get(songPosition).getTitle());
        /*Glide.with(context)
                .load(PlayerActivity.musicListPA.get(PlayerActivity.songPosition).getArtUri())
                .apply(new RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(NowPlaying.binding.songImgNP);
        NowPlaying.binding.songNameNP.setText(PlayerActivity.musicListPA.get(PlayerActivity.songPosition).getTitle());*/

        playMusic();
    }


}