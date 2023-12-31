package com.example.projectprm;

import static com.example.projectprm.Music.exitApplication;
import static com.example.projectprm.Music.favouriteChecker;
import static com.example.projectprm.Music.formatDuration;
import static com.example.projectprm.Music.getImgArt;
import static com.example.projectprm.Music.setSongPosition;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
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

    public static Boolean isFavourite = false;
    public static int fIndex=-1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            setTheme(MainActivity.currentTheme[MainActivity.themeIndex]);
        }

        playerBinding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(playerBinding.getRoot());

        Intent intent = new Intent(this, MusicService.class);

        if (intent.getData() != null && "content".equals(intent.getData().getScheme())) {
            // Xử lý Intent khi chứa dữ liệu và có scheme "content"
            songPosition = 0;
            Intent intentService = new Intent(this, MusicService.class);
            bindService(intentService, this, BIND_AUTO_CREATE);
            startService(intentService);
            musicListPA = new ArrayList<>();
            musicListPA.add(getMusicDetails(intent.getData()));
            /*Glide.with(this)
                    .load(getImgArt(musicListPA.get(songPosition).getPath()))
                    .apply(new RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
                    .into(playerBinding.songImgPA);*/
            playerBinding.songNamePA.setText(musicListPA.get(songPosition).getTitle());

            byte[] imgArt = getImgArt(musicListPA.get(songPosition).getPath());
            Bitmap image;
            if (imgArt != null) {
                image = BitmapFactory.decodeByteArray(imgArt, 0, imgArt.length);
            } else {
                image = BitmapFactory.decodeResource(musicService.getResources(), R.drawable.music_player_icon_slash_screen);
            }
            playerBinding.songImgPA.setImageBitmap(image);

        } else {
            // Xử lý Intent khi không chứa dữ liệu hoặc không có scheme "content"
            initializeLayout();
        }


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
                    EqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService.mediaPlayer.getAudioSessionId());
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
        playerBinding.favouriteBtnPA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fIndex = favouriteChecker(musicListPA.get(songPosition).getId());
                if (isFavourite) {
                    isFavourite = false;
                    playerBinding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon);
                    FavouriteActivity.favouriteSongs.remove(fIndex);
                } else {
                    isFavourite = true;
                    playerBinding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon);
                    FavouriteActivity.favouriteSongs.add(musicListPA.get(songPosition));
                }

            }
        });
    }

    private Music getMusicDetails(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DURATION};
            cursor = this.getContentResolver().query(contentUri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int dataColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
                int durationColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
                String path = cursor.getString(dataColumnIndex);
                long duration = cursor.getLong(durationColumnIndex);
                return new Music("Unknown", path, "Unknown", "Unknown", duration, "Unknown", path);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        // Trả về một giá trị mặc định hoặc xử lý thích hợp nếu không có dữ liệu hợp lệ.
        return new Music("Unknown", "Unknown", "Unknown", "Unknown", 0L, "Unknown", "Unknown");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicListPA.get(songPosition).getId().equals("Unknown") && !isPlaying) {
            exitApplication();
        }
    }



    private void setLayout(){
        fIndex = favouriteChecker(musicListPA.get(songPosition).getId());
        /*Glide.with(this)
                .load(musicListPA.get(songPosition).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(playerBinding.songImgPA);*/
        byte[] imgArt = getImgArt(musicListPA.get(songPosition).getPath());
        Bitmap image;
        if (imgArt != null) {
            image = BitmapFactory.decodeByteArray(imgArt, 0, imgArt.length);
        } else {
            image = BitmapFactory.decodeResource(musicService.getResources(), R.drawable.music_player_icon_slash_screen);
        }
        playerBinding.songImgPA.setImageBitmap(image);
        playerBinding.songNamePA.setText(musicListPA.get(songPosition).getTitle());
        if(repeat) playerBinding.repeatBtnPA.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.purple_500));
        if(min15 || min30 || min60) playerBinding.timerBtnPA.setColorFilter(ContextCompat.getColor(PlayerActivity.this, R.color.purple_500));
        if (isFavourite) {
            playerBinding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon);
        } else {
            playerBinding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon);
        }
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
            case "PlaylistDetailsAdapter" :
                intent = new Intent(this, MusicService.class);
                bindService(intent,this,BIND_AUTO_CREATE);
                startService(intent);
                musicListPA = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                {
                    musicListPA.addAll(PlaylistActivity.musicPlaylist.ref.get(PlaylistDetails.currentPlaylistPos).playlist);
                }
                setLayout();
                break;
            case "PlaylistDetailsShuffle" :
                intent = new Intent(this, MusicService.class);
                bindService(intent,this,BIND_AUTO_CREATE);
                startService(intent);
                musicListPA = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                {
                    musicListPA.addAll(PlaylistActivity.musicPlaylist.ref.get(PlaylistDetails.currentPlaylistPos).playlist);
                }
                Collections.shuffle(musicListPA);
                setLayout();
                break;
            case "FavouriteShuffle" :
                intent = new Intent(this, MusicService.class);
                bindService(intent,this,BIND_AUTO_CREATE);
                startService(intent);
                musicListPA = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                {
                    musicListPA.addAll(FavouriteActivity.favouriteSongs);
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
        musicService.seekBarSetup();
//        musicService.audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        musicService.audioManager.requestAudioFocus(musicService, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

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
                                exitApplication();
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
                                exitApplication();
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
                                exitApplication();
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