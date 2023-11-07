package com.example.projectprm;

import static androidx.core.util.TimeUtils.formatDuration;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbEndpoint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectprm.databinding.MusicViewBinding;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyHolder> {

    private Context context;
    private ArrayList<Music> musicList;
    private boolean playlistDetails;
    private boolean selectionActivity;

    public MusicAdapter(Context context, ArrayList<Music> musicList, boolean playlistDetails, boolean selectionActivity) {
        this.context = context;
        this.musicList = musicList;
        this.playlistDetails = playlistDetails;
        this.selectionActivity = selectionActivity;

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MusicViewBinding binding = MusicViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.title.setText(musicList.get(position).getTitle());
        holder.album.setText(musicList.get(position).getAlbum());
        holder.duration.setText(Music.formatDuration(musicList.get(position).getDuration()));

        Glide.with(context)
                .load(musicList.get(position).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(holder.image);


        if (playlistDetails) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendIntent("PlaylistDetailsAdapter", position);
                }
            });
        } else if (selectionActivity) {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (addSong(musicList.get(position))) {
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.cool_pink));
                    } else {
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    }
                }
            });
        } else {
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (MainActivity.search) {
                            sendIntent("MusicAdapterSearch", position);
                        } else if (musicList.get(position).getId() == PlayerActivity.nowPlayingId) {
                            sendIntent("NowPlaying", PlayerActivity.songPosition);
                        } else {
                            sendIntent("MusicAdapter", position);
                        }
                    }
                }
            });
        }

    }


    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView album;
        private ImageView image;
        private TextView duration;
        private View root;

        public MyHolder(MusicViewBinding binding) {
            super(binding.getRoot());

            title = binding.songNameMV;
            album = binding.songAlbumMV;
            image = binding.imageMV;
            duration = binding.songDuration;
            root = binding.getRoot();
        }

        public TextView getTitle() {
            return title;
        }

        public TextView getAlbum() {
            return album;
        }

        public ImageView getImage() {
            return image;
        }

        public TextView getDuration() {
            return duration;
        }

    }

    public void updateMusicList(ArrayList<Music> searchList) {
        musicList = new ArrayList<>();
        musicList.addAll(searchList);
        notifyDataSetChanged();
    }

    private void sendIntent(String ref, int position) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("index", position);
        intent.putExtra("class", ref);
        ContextCompat.startActivity(context, intent, null);
    }

    private boolean addSong(Music song) {
        List<Music> playlist = PlaylistActivity.musicPlaylist.ref.get(PlaylistDetails.currentPlaylistPos).playlist;
        for (int index = 0; index < playlist.size(); index++) {
            if (song.getId() == playlist.get(index).getId()) {
                playlist.remove(index);
                return false;
            }
        }
        playlist.add(song);
        return true;
    }

    public void refreshPlaylist() {
        musicList = new ArrayList<>();
        musicList = PlaylistActivity.musicPlaylist.ref.get(PlaylistDetails.currentPlaylistPos).playlist;
        notifyDataSetChanged();
    }
}

