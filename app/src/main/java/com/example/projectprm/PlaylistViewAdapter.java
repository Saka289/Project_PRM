package com.example.projectprm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectprm.databinding.FavouriteViewBinding;
import com.example.projectprm.databinding.PlaylistViewBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class PlaylistViewAdapter extends RecyclerView.Adapter<PlaylistViewAdapter.MyHolder> {

    private final Context context;
    private List<Music.Playlist> playlistList;

    public PlaylistViewAdapter(Context context, List<Music.Playlist> playlistList) {
        this.context = context;
        this.playlistList = playlistList;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        private View root;
        private ImageButton delete;
        private TextView name;
        private ImageView image;


        public MyHolder(PlaylistViewBinding binding) {
            super(binding.getRoot());
            root = binding.getRoot();
            image = binding.playlistImg;
            name = binding.playlistName;
            delete = binding.playlistDeleteBtn;
        }

        public TextView getName() {
            return name;
        }


        public ImageView getImage() {
            return image;
        }


    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PlaylistViewBinding binding = PlaylistViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHolder(binding);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.name.setText(playlistList.get(position).name);
        holder.name.setSelected(true);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                builder.setTitle(playlistList.get(position).name)
                        .setMessage("Do you want to delete playlist?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            PlaylistActivity.musicPlaylist.ref.remove(position);
                            refreshPlaylist();
                            dialog.dismiss();
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            dialog.dismiss();
                        });

                AlertDialog customDialog = builder.create();
                customDialog.show();

            }
        });
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlaylistDetails.class);
                intent.putExtra("index", position);
                ContextCompat.startActivity(context, intent, null);
            }
        });

        if (PlaylistActivity.musicPlaylist.ref.get(holder.getAdapterPosition()).playlist.size() > 0) {
            Glide.with(context)
                    .load(PlaylistActivity.musicPlaylist.ref.get(position).playlist.get(0).getArtUri())
                    .apply(RequestOptions.placeholderOf(R.drawable.music_player_icon_slash_screen).centerCrop())
                    .into(holder.image);
        }
    }



    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    private void sendIntent( String ref, int position) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("index", position);
        intent.putExtra("class", ref);
        ContextCompat.startActivity(context, intent, null);
    }
    public void refreshPlaylist() {
        playlistList = new ArrayList<>(PlaylistActivity.musicPlaylist.ref);
        notifyDataSetChanged();
    }
}