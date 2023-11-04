package com.example.projectprm;

import static com.example.projectprm.Music.formatDuration;
import static com.example.projectprm.Music.getImgArt;
import static com.example.projectprm.PlayerActivity.musicListPA;
import static com.example.projectprm.PlayerActivity.musicService;
import static com.example.projectprm.PlayerActivity.playerBinding;
import static com.example.projectprm.PlayerActivity.songPosition;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class MusicService extends Service implements AudioManager.OnAudioFocusChangeListener {

    public MyBinder myBinder = new MyBinder();
    public MediaPlayer mediaPlayer = null;
    public MediaSessionCompat mediaSession;
    public AudioManager audioManager;
    Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    @Override
    public IBinder onBind(Intent intent) {
        mediaSession = new MediaSessionCompat(getBaseContext(), "My Music");
        return myBinder;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange <= 0) {
            PlayerActivity.playerBinding.playPauseBtnPA.setIconResource(R.drawable.play_icon);
            NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.play_icon);
            showNotification(R.drawable.play_icon);
            PlayerActivity.isPlaying = false;
            musicService.mediaPlayer.pause();
        } else {
            PlayerActivity.playerBinding.playPauseBtnPA.setIconResource(R.drawable.pause_icon);
            NowPlaying.binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon);
            showNotification(R.drawable.pause_icon);
            PlayerActivity.isPlaying = true;
            mediaPlayer.start();
        }
    }

    public class MyBinder extends Binder {
        public MusicService currentService() {
            return MusicService.this;
        }
    }

    public void showNotification(int playPauseBtn) {
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            intent = new Intent(getBaseContext(), MainActivity.class);
        }
        intent.putExtra("index", PlayerActivity.songPosition);
        intent.putExtra("class", "NowPlaying");

        PendingIntent contextIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        int flag;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flag = PendingIntent.FLAG_IMMUTABLE;
        } else {
            flag = PendingIntent.FLAG_UPDATE_CURRENT;
        }

        Intent prevIntent = new Intent(getBaseContext(), NotificationReceiver.class);
        prevIntent.setAction(ApplicationClass.PREVIOUS);

        Intent playIntent = new Intent(getBaseContext(), NotificationReceiver.class);
        playIntent.setAction(ApplicationClass.PLAY);

        Intent nextIntent = new Intent(getBaseContext(), NotificationReceiver.class);
        nextIntent.setAction(ApplicationClass.NEXT);

        Intent exitIntent = new Intent(getBaseContext(), NotificationReceiver.class);
        exitIntent.setAction(ApplicationClass.EXIT);

// Create the PendingIntents
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, prevIntent, flag);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, playIntent, flag);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, nextIntent, flag);
        PendingIntent exitPendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, exitIntent, flag);

        byte[] imgArt = getImgArt(musicListPA.get(songPosition).getPath());
        Bitmap image;
        if (imgArt != null) {
            image = BitmapFactory.decodeByteArray(imgArt, 0, imgArt.length);
        } else {
            image = BitmapFactory.decodeResource(getResources(), R.drawable.music_player_icon_slash_screen);
        }


        NotificationCompat.Builder notification = new NotificationCompat.Builder(getBaseContext(), (String) ApplicationClass.CHANNEL_ID);
        notification.setContentIntent(contextIntent);
        notification.setContentTitle(musicListPA.get(songPosition).getTitle());
        notification.setContentText(musicListPA.get(songPosition).getArtist());
        notification.setSmallIcon(R.drawable.playlist_icon);
        notification.setLargeIcon(image);
        notification.setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.getSessionToken()));
        notification.setPriority(NotificationCompat.PRIORITY_HIGH);
        notification.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        notification.setOnlyAlertOnce(true);
        notification.addAction(R.drawable.previous_icon, "Previous", prevPendingIntent);
        notification.addAction(playPauseBtn, "Play", playPendingIntent);
        notification.addAction(R.drawable.next_icon, "Next", nextPendingIntent);
        notification.addAction(R.drawable.exit_icon, "Exit", exitPendingIntent);
        notification.build();

        startForeground(13, notification.build());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            float playbackSpeed;
            if (PlayerActivity.isPlaying) {
                playbackSpeed = 1F;
            } else {
                playbackSpeed = 0F;
            }
            MediaMetadataCompat metadata = new MediaMetadataCompat.Builder()
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer.getDuration())
                    .build();
            PlaybackStateCompat playbackState = new PlaybackStateCompat.Builder()
                    .setState(PlaybackStateCompat.STATE_PLAYING, mediaPlayer.getCurrentPosition(), playbackSpeed)
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .build();
            mediaSession.setMetadata(metadata);
            mediaSession.setPlaybackState(playbackState);
        }

    }

    public void createMediaPlayer() {
        if (musicService.mediaPlayer == null) {
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

        playerBinding.playPauseBtnPA.setIconResource(R.drawable.pause_icon);
        musicService.showNotification(R.drawable.pause_icon);
        playerBinding.tvSeekBarStart.setText(formatDuration(mediaPlayer.getCurrentPosition()));
        playerBinding.tvSeekBarEnd.setText(formatDuration(mediaPlayer.getDuration()));
        playerBinding.seekBarPA.setProgress(0);
        playerBinding.seekBarPA.setMax(mediaPlayer.getDuration());
    }

    public void seekBarSetup() {
        runnable = new Runnable() {
            @Override
            public void run() {
                playerBinding.tvSeekBarStart.setText(formatDuration(mediaPlayer.getCurrentPosition()));
                playerBinding.seekBarPA.setProgress(mediaPlayer.getCurrentPosition());
                new Handler(Looper.getMainLooper()).postDelayed(runnable, 200);
            }
        };
        new Handler(Looper.getMainLooper()).postDelayed(runnable, 0);
    }


}
