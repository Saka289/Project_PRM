package com.example.projectprm;

import static com.example.projectprm.Music.formatDuration;
import static com.example.projectprm.Music.setSongPosition;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectprm.databinding.ActivityPlayerBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class PlayerActivity extends AppCompatActivity implements ServiceConnection, MediaPlayer.OnCompletionListener {
    public static ActivityPlayerBinding playerBinding;

    public static ArrayList<Music> musicListPA;
    public static int songPosition;

    public static boolean isPlaying = false;

    public static MusicService musicService = null;

    public static Boolean repeat = false;

    public Boolean min15 = false;
    public Boolean min30 = false;
    public Boolean min60 = false;

    public static String nowPlayingId = "";



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
        playerBinding.backBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

        playerBinding.seekBarPA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) musicService.mediaPlayer.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playerBinding.repeatBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!repeat) {
                    repeat = true;
                    playerBinding.repeatBtnPA.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.purple_500));
                } else {
                    repeat = false;
                    playerBinding.repeatBtnPA.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.cool_pink));
                }
            }
        });

        playerBinding.equalizerBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent EqIntent = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
                    EqIntent.setAction(AudioEffect.EXTRA_AUDIO_SESSION);
                    EqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getBaseContext().getPackageName());
                    EqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
                    startActivityForResult(EqIntent, 13);
                }catch (Exception ex) {
                    Toast.makeText(PlayerActivity.this, "Equalizer Feature not Supported", Toast.LENGTH_SHORT).show();
                }
            }
        });

        playerBinding.timerBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();

            }
        });

        playerBinding.timerBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean timer = min15 || min30 || min60;
                if (!timer) {
                    showBottomSheetDialog();
                } else {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(PlayerActivity.this);
                    builder.setTitle("Stop Timer")
                            .setMessage("Do you want to stop the timer?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    min15 = false;
                                    min30 = false;
                                    min60 = false;
                                    playerBinding.timerBtnPA.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.cool_pink));
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog customDialog = builder.create();
                    customDialog.show();
                }
            }
        });

        playerBinding.shareBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("audio/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA.get(songPosition).getPath()));
                startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"));
            }
        });
    }

    private void setLayout(){
        Glide.with(this)
                .load(musicListPA.get(songPosition).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(playerBinding.songImgPA);
        playerBinding.songNamePA.setText(musicListPA.get(songPosition).getTitle());
        if(repeat) playerBinding.repeatBtnPA.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.purple_500));
        if(min15 || min30 || min60) playerBinding.timerBtnPA.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.purple_500));
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
        playerBinding.tvSeekBarStart.setText(formatDuration(musicService.mediaPlayer.getCurrentPosition()));
        playerBinding.tvSeekBarEnd.setText(formatDuration(musicService.mediaPlayer.getDuration()));
        playerBinding.seekBarPA.setProgress(0);
        playerBinding.seekBarPA.setMax(musicService.mediaPlayer.getDuration());
        musicService.mediaPlayer.setOnCompletionListener(this);
    }

    private void initializeLayout(){
        songPosition = getIntent().getIntExtra("index",0);
        String songPosition = getIntent().getStringExtra("class");
        switch (songPosition) {
            case "NowPlaying":
                setLayout();
                playerBinding.tvSeekBarStart.setText(formatDuration(musicService.mediaPlayer.getCurrentPosition()));
                playerBinding.tvSeekBarEnd.setText(formatDuration(musicService.mediaPlayer.getDuration()));
                playerBinding.seekBarPA.setProgress(musicService.mediaPlayer.getCurrentPosition());
                playerBinding.seekBarPA.setMax(musicService.mediaPlayer.getDuration());
            case "MusicAdapter":
                Intent intent = new Intent(this, MusicService.class);
                bindService(intent,this,BIND_AUTO_CREATE);
                startService(intent);
                musicListPA = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                {
                    musicListPA.addAll(MainActivity.MusicListMA);
                }
                setLayout();

                break;

            case "MainActivity":
                intent = new Intent(this, MusicService.class);
                bindService(intent,this,BIND_AUTO_CREATE);
                startService(intent);
                musicListPA = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    musicListPA.addAll(MainActivity.MusicListMA);
                }
                Collections.shuffle(musicListPA);
                setLayout();

                break;
            case "MusicAdapterSearch":
                intent = new Intent(this, MusicService.class);
                bindService(intent,this,BIND_AUTO_CREATE);
                startService(intent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    initServiceAndPlaylist(MainActivity.musicListSearch,false, false);
                }
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
        musicService.seekBarSetup();

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        musicService = null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setSongPosition(true);
        createMediaPlayer();
        try {
            setLayout();
        } catch (Exception ex) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 13 || resultCode == RESULT_OK) {
            return;
        }
    }

    private void showBottomSheetDialog() {

        Dialog dialog = new BottomSheetDialog(PlayerActivity.this);

        dialog.setContentView(R.layout.bottom_sheet_dialog);
        dialog.show();
        dialog.findViewById(R.id.min_15).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Music will stop after 15 minutes", Toast.LENGTH_SHORT).show();
                playerBinding.timerBtnPA.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.purple_500));
                min15 = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(15 * 60000);
                            if (min15) {
                                Music.exitApplication();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.min_30).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Music will stop after 30 minutes", Toast.LENGTH_SHORT).show();
                playerBinding.timerBtnPA.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.purple_500));
                min15 = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(30 * 60000);
                            if (min30) {
                                Music.exitApplication();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.min_60).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Music will stop after 60 minutes", Toast.LENGTH_SHORT).show();
                playerBinding.timerBtnPA.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.purple_500));
                min60 = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(60 * 60000);
                            if (min60) {
                                Music.exitApplication();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                dialog.dismiss();
            }
        });
    }

    private void initServiceAndPlaylist(ArrayList<Music> playlist, boolean shuffle, boolean playNext) {
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, this, BIND_AUTO_CREATE);
        startService(intent);
        musicListPA = new ArrayList<>();
        musicListPA.addAll(playlist);
        if (shuffle) {
            Collections.shuffle(musicListPA);
        }
        setLayout();
//        if (!playNext) {
//            PlayNext.playNextList = new ArrayList<>();
//        }
    }

}