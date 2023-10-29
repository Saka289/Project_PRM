package com.example.projectprm;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.projectprm.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;

@RequiresApi(Build.VERSION_CODES.R)

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private ActionBarDrawerToggle toggle;

    private MusicAdapter musicAdapter;

    public static ArrayList<Music> MusicListMA;

    int[] currentGradient = {R.drawable.gradient_pink, R.drawable.gradient_blue, R.drawable.gradient_purple, R.drawable.gradient_green, R.drawable.gradient_black};

    int themeIndex = 0;
    int[] currentTheme = {R.style.coolPink, R.style.coolBlue, R.style.coolPurple, R.style.coolGreen, R.style.coolBlack};
    int[] currentThemeNav = {R.style.coolPinkNav, R.style.coolBlueNav, R.style.coolPurpleNav, R.style.coolGreenNav, R.style.coolBlackNav};
    public static ArrayList<Music> musicListSearch;

    public static boolean search = false;
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
        if (requestRuntimePermission()) {
            initializeLayout();
        }


        mainBinding.shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra("index", 0);
                intent.putExtra("class", "MainActivity");
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
        mainBinding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navFeedback) {
                    Toast.makeText(getApplicationContext(), "Feedback", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.navSettings) {
                    Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.navAbout) {
                    Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.navExit) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Exit")
                            .setMessage("DO you want to close this app")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                {
                                    Music.exitApplication();
                                }
                            })
                            .setNegativeButton("No", (dialog, which) -> {
                                dialog.dismiss();
                            });

                    Dialog customDialog = builder.create();

                    customDialog.show();
                    ((AlertDialog) customDialog).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                    ((AlertDialog) customDialog).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);



                // Add your exit confirmation logic here
                }
                return true;
            }
        });
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!PlayerActivity.isPlaying && PlayerActivity.musicService != null) {
            Music.exitApplication();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view_menu, menu);

        // For setting gradient
        LinearLayout linearLayoutNav = findViewById(R.id.linearLayoutNav);
        linearLayoutNav.setBackgroundResource(currentGradient[themeIndex]);

        MenuItem searchMenuItem = menu.findItem(R.id.searchView);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null) {
                    String userInput = newText.toLowerCase();
                    musicListSearch = new ArrayList<>();
                    for (Music song : MusicListMA) {
                        if (song.getTitle().toLowerCase().contains(userInput)) {
                            musicListSearch.add(song);
                        }
                    }
                    search = true;
                    musicAdapter.updateMusicList(musicListSearch);
                }
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}