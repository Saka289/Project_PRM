package com.example.projectprm;

import static com.example.projectprm.Music.getImgArt;
import static com.example.projectprm.PlayerActivity.musicListPA;
import static com.example.projectprm.PlayerActivity.musicService;
import static com.example.projectprm.PlayerActivity.songPosition;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectprm.databinding.FavouriteViewBinding;
import com.example.projectprm.databinding.MusicViewBinding;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.MyHolder> {

    private Context context;
    private ArrayList<Music> musicList;

    public FavouriteAdapter(Context context, ArrayList<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private ImageView image;

        public MyHolder(FavouriteViewBinding binding) {
            super(binding.getRoot());

            name = binding.songNameFV;
            image = binding.songImgFV;
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
        FavouriteViewBinding binding = FavouriteViewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.name.setText(musicList.get(position).getTitle());
        /*Glide.with(context)
                .load(musicList.get(position).getArtUri())
                .apply(RequestOptions.placeholderOf(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(holder.image);*/

        byte[] imgArt = getImgArt(musicList.get(position).getPath());
        Bitmap image;
        if (imgArt != null) {
            image = BitmapFactory.decodeByteArray(imgArt, 0, imgArt.length);
        } else {
            image = BitmapFactory.decodeResource(musicService.getResources(), R.drawable.music_player_icon_slash_screen);
        }

        holder.image.setImageBitmap(image);
    }



    @Override
    public int getItemCount() {
        return musicList.size();
    }

    private void sendIntent( String ref, int position) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra("index", position);
        intent.putExtra("class", ref);
        ContextCompat.startActivity(context, intent, null);
    }

}