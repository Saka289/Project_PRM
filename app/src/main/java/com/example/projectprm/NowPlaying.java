package com.example.projectprm;

import static com.example.projectprm.Music.setSongPosition;
import static com.example.projectprm.PlayerActivity.musicListPA;
import static com.example.projectprm.PlayerActivity.musicService;
import static com.example.projectprm.PlayerActivity.playerBinding;
import static com.example.projectprm.PlayerActivity.songPosition;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.projectprm.databinding.FragmentNowPlayingBinding;
import com.bumptech.glide.request.RequestOptions;


public class NowPlaying extends Fragment {

    public static FragmentNowPlayingBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireContext().getTheme().applyStyle(MainActivity.currentTheme[MainActivity.themeIndex], true);
        }

        View view = inflater.inflate(R.layout.fragment_now_playing, container, false);
        binding = FragmentNowPlayingBinding.bind(view);
        binding.getRoot().setVisibility(View.INVISIBLE);
        binding.playPauseBtnNP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PlayerActivity.isPlaying) pauseMusic();
                else playMusic();

            }
        });
        binding.nextBtnNP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSongPosition(true);
                musicService.createMediaPlayer();
                playerBinding.songNamePA.setText(musicListPA.get(songPosition).getTitle());
                Glide.with(NowPlaying.this)
                        .load(musicListPA.get(songPosition).getArtUri())
                        .apply(new RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
                        .into(NowPlaying.binding.songImgNP);
                NowPlaying.binding.songNameNP.setText(musicListPA.get(songPosition).getTitle());
                musicService.showNotification(R.drawable.pause_icon);
                playMusic();
            }
        });
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), PlayerActivity.class);
                intent.putExtra("index", songPosition);
                intent.putExtra("class", "NowPlaying");
                ContextCompat.startActivity(requireContext(), intent, null);
            }
        });
        return inflater.inflate(R.layout.fragment_now_playing, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PlayerActivity.musicService != null) {
            binding.getRoot().setVisibility(View.VISIBLE);
            binding.songNameNP.setSelected(true);
            Glide.with(requireContext())
                    .load(PlayerActivity.musicListPA.get(PlayerActivity.songPosition).getArtUri())
                    .apply(new RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
                    .into(binding.songImgNP);
            binding.songNameNP.setText(PlayerActivity.musicListPA.get(PlayerActivity.songPosition).getTitle());
            if (PlayerActivity.isPlaying) {
                binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon);
            } else {
                binding.playPauseBtnNP.setIconResource(R.drawable.play_icon);
            }
        }
    }

    private void playMusic() {
        PlayerActivity.isPlaying = true;
        PlayerActivity.musicService.mediaPlayer.start();
        binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon);
        PlayerActivity.musicService.showNotification(R.drawable.pause_icon);
    }

    private void pauseMusic() {
        PlayerActivity.isPlaying = false;
        PlayerActivity.musicService.mediaPlayer.pause();
        binding.playPauseBtnNP.setIconResource(R.drawable.play_icon);
        PlayerActivity.musicService.showNotification(R.drawable.play_icon);
    }
}