package com.example.projectprm;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectprm.databinding.ActivityMainBinding;

import java.io.File;
import java.util.ArrayList;

@RequiresApi(Build.VERSION_CODES.R)

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private ActionBarDrawerToggle toggle;

    private MusicAdapter musicAdapter;

    public static ArrayList<Music> MusicListMA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.coolPinkNav);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        toggle = new ActionBarDrawerToggle(this, mainBinding.getRoot(), R.string.open, R.string.close);
        mainBinding.getRoot().addDrawerListener(toggle);
        toggle.syncState();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if(requestRuntimePermission()){initializeLayout();}


        mainBinding.shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra("index", 0);
                intent.putExtra("class","MainActivity");
                startActivity(intent);
            }
        });
        mainBinding.favouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FavouriteActivity.class));
            }
        });
        mainBinding.playlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PlaylistActivity.class));
            }
        });

//        mainBinding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.navSettings:
//                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
//                        return true;
//                    case R.id.navAbout:
//                        startActivity(new Intent(MainActivity.this, AboutActivity.class));
//                        return true;
//                    case R.id.navExit:
//                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
//                        builder.setTitle("Exit")
//                                .setMessage("Do you want to close app?")
//                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        exitApplication();
//                                    }
//                                })
//                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                        AlertDialog customDialog = builder.create();
//                        customDialog.show();
//                        setDialogBtnBackground(MainActivity.this, customDialog);
//                        return true;
//                    default:
//                        return false;
//                }
//            }
//        });
    }

    private boolean requestRuntimePermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                initializeLayout();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeLayout(){
        MusicListMA = getAllAudio();

        mainBinding.musicRV.setHasFixedSize(true);
        mainBinding.musicRV.setItemViewCacheSize(13);
        mainBinding.musicRV.setLayoutManager(new LinearLayoutManager(this));
        musicAdapter = new MusicAdapter(this, MusicListMA);
        mainBinding.musicRV.setAdapter(musicAdapter);
        mainBinding.totalSongs.setText("Total Songs : " + musicAdapter.getItemCount());
    }


    private ArrayList<Music> getAllAudio(){
        ArrayList<Music> tempList = new ArrayList<>();
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                MediaStore.Audio.Media.DATE_ADDED + " DESC",
                null
        );
        if(cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String titleC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                    String idC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                    String albumC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                    String artistC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    String pathC = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                    Long durationC = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    String albumIdC =  String.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
                    Uri uri = Uri.parse("content://media/external/audio/albumart");
                    String artUri = Uri.withAppendedPath(uri, albumIdC).toString();
                    Music music = new Music();
                    music.setId(idC);
                    music.setTitle(titleC);
                    music.setAlbum(albumC);
                    music.setArtist(artistC);
                    music.setPath(pathC);
                    music.setDuration(durationC);
                    music.setArtUri(artUri);
                    File file = new File(music.getPath());
                    if (file.exists()) {
                        tempList.add(music);
                    }
                } while (cursor.moveToNext());
                    cursor.close();
            }
        }
        return tempList;
    }
}