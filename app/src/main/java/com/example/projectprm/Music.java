package com.example.projectprm;

import static com.example.projectprm.PlayerActivity.musicListPA;
import static com.example.projectprm.PlayerActivity.songPosition;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.SortedList;

import com.google.android.material.color.MaterialColors;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Music {
    private String id;
    private String title;
    private String album;
    private String artist;
    private Long duration;
    private String path;
    private String artUri;

    public Music() {
    }

    public Music(String id, String title, String album, String artist, Long duration, String path, String artUri) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
        this.path = path;
        this.artUri = artUri;
    }

    public static class Playlist {
        String name;
        ArrayList<Music> playlist;
        String createdBy;
        String createdOn;

    }


     public static class MusicPlaylist {
         List<Playlist> ref = new ArrayList<>();
    }

    public String getArtUri() {
        return artUri;
    }

    public void setArtUri(String artUri) {
        this.artUri = artUri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static String formatDuration(long duration) {
        long minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS);
        long seconds = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) - minutes * TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static byte[] getImgArt(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        byte[] art = retriever.getEmbeddedPicture();
        return art;
    }

    public static void setSongPosition(boolean increment){
        if(!PlayerActivity.repeat) {
            if(increment){
                if(musicListPA.size() - 1 == songPosition){
                    songPosition = 0;
                }else{
                    ++songPosition;
                }
            }else{
                if(0 == songPosition){
                    songPosition = musicListPA.size() - 1;
                }else{
                    --songPosition;
                }
            }
        }

    }

    public static void exitApplication(){
        if(PlayerActivity.musicService != null) {
//            PlayerActivity.musicService.audioManager.abandonAudioFocus(PlayerActivity.musicService);
            PlayerActivity.musicService.stopForeground(true);
            PlayerActivity.musicService.mediaPlayer.release();
            PlayerActivity.musicService = null;
            System.exit(1);
        }
    }

    public static int favouriteChecker(String id) {
        PlayerActivity.isFavourite= false;
        for (int index = 0; index < FavouriteActivity.favouriteSongs.size(); index++) {
            Music music = FavouriteActivity.favouriteSongs.get(index);
            if (id.equals(music.getId())) {
                PlayerActivity.isFavourite = true;
                return index;
            }
        }
        return -1;
    }




    public static ArrayList<Music> checkPlaylist(ArrayList<Music> playlist) {
        Iterator<Music> iterator = playlist.iterator();
        while (iterator.hasNext()) {
            Music music = iterator.next();
            File file = new File(music.getPath());
            if (!file.exists()) {
                iterator.remove();
            }
        }
        return playlist;
    }


    public void setDialogBtnBackground(Context context, AlertDialog dialog) {
        // Setting button text color
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
        );
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
        );


        // Setting button background color
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
        );
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(
                MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
        );
    }
}
