package com.example.projectprm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.example.projectprm.databinding.ActivitySelectionBinding;

import java.util.ArrayList;

public class SelectionActivity extends AppCompatActivity {
    private ActivitySelectionBinding binding;
    private MusicAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectionBinding.inflate(getLayoutInflater());
        setTheme(R.style.coolPink);
        setContentView(binding.getRoot());
        binding.selectionRV.setItemViewCacheSize(10);
        binding.selectionRV.setHasFixedSize(true);
        binding.selectionRV.setLayoutManager(new LinearLayoutManager(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            adapter = new MusicAdapter(this, MainActivity.MusicListMA, false, true);
        }
        binding.selectionRV.setAdapter(adapter);
        binding.backBtnSA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.searchViewSA.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    MainActivity.musicListSearch = new ArrayList<>();
                }
                if (newText != null) {
                    String userInput = newText.toLowerCase();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        for (Music song : MainActivity.MusicListMA) {
                            if (song.getTitle().toLowerCase().contains(userInput)) {
                                MainActivity.musicListSearch.add(song);
                            }
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        MainActivity.search = true;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        adapter.updateMusicList(MainActivity.musicListSearch);
                    }
                }
                return true;
            }
        });
    }
}