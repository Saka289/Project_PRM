package com.example.projectprm;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.projectprm.databinding.ActivityPlaylistBinding;
import com.example.projectprm.databinding.AddPlaylistDialogBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PlaylistActivity extends AppCompatActivity {

    private ActivityPlaylistBinding playlistBinding;
    private PlaylistViewAdapter adapter;

    public static Music.MusicPlaylist musicPlaylist = new Music.MusicPlaylist();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.coolPink);
        playlistBinding = ActivityPlaylistBinding.inflate(getLayoutInflater());
        setContentView(playlistBinding.getRoot());
//        ArrayList<String> list=new ArrayList<String>();
//        list.add("String 1");
//        list.add("String 1");
//        list.add("String 1");
//        list.add("String 1");
//        list.add("String 1");
        playlistBinding.playlistRV.setHasFixedSize(true);
        playlistBinding.playlistRV.setItemViewCacheSize(13);
        playlistBinding.playlistRV.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new PlaylistViewAdapter(this, musicPlaylist.ref);

        playlistBinding.playlistRV.setAdapter(adapter);

        playlistBinding.backBtnFA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        playlistBinding.addPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAlertDialog();
            }
        });

    }

    private void customAlertDialog() {
        View customDialog = LayoutInflater.from(this).inflate(R.layout.add_playlist_dialog, playlistBinding.getRoot(), false);
        AddPlaylistDialogBinding binder = AddPlaylistDialogBinding.bind(customDialog);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        AlertDialog dialog = builder.setView(customDialog)
                .setTitle("Playlist Details")
                .setPositiveButton("ADD", (d, which) -> {
                    CharSequence playlistName = binder.playlistName.getText();
                    CharSequence createdBy = binder.yourName.getText();
                    if (playlistName != null && createdBy != null) {
                        if (playlistName.length() > 0 && createdBy.length() > 0) {
                            addPlaylist(playlistName.toString(), createdBy.toString());
                        }
                    }
                    d.dismiss();
                })
                .create();
        dialog.show();

    }

    private void addPlaylist(String name, String createdBy) {
        boolean playlistExists = false;
        for (Music.Playlist playlist : musicPlaylist.ref) {
            if (name.equals(playlist.name)) {
                playlistExists = true;
                break;
            }
        }
        if (playlistExists) {
            Toast.makeText(this, "Playlist Exist!!", Toast.LENGTH_SHORT).show();
        } else {
            Music.Playlist tempPlaylist = new Music.Playlist();
            tempPlaylist.name = name;
            tempPlaylist.playlist = new ArrayList<>();
            tempPlaylist.createdBy = createdBy;
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            tempPlaylist.createdOn = sdf.format(calendar.getTime());
            musicPlaylist.ref.add(tempPlaylist);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}