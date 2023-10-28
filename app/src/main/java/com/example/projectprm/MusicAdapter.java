package com.example.projectprm;
import static androidx.core.util.TimeUtils.formatDuration;

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

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyHolder> {

    private Context context;
    private ArrayList<Music> musicList;

    public MusicAdapter(Context context, ArrayList<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MusicViewBinding binding = MusicViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        holder.title.setText(musicList.get(position).getTitle());
        holder.album.setText(musicList.get(position).getAlbum());
        holder.duration.setText(Music.formatDuration(musicList.get(position).getDuration()));

        Glide.with(context)
                .load(musicList.get(position).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(holder.image);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.search) {
                    sendIntent("MusicAdapterSearch", position);
                } else {
                    sendIntent("MusicAdapter", position);
                }
            }
        });


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
            return   duration;
        }

    }

    public void updateMusicList(ArrayList<Music> searchList) {
        musicList = new ArrayList<>();
        musicList.addAll(searchList);
        notifyDataSetChanged();
    }

    private void sendIntent( String ref, int position) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("index", position);
        intent.putExtra("class", ref);
        ContextCompat.startActivity(context, intent, null);
    }
}

