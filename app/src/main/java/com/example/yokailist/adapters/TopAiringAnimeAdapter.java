package com.example.yokailist.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yokailist.R;
import com.example.yokailist.activities.anime.AnimeDetailActivity;
import com.example.yokailist.data.response.animedetail.Anime;

import java.util.List;

public class TopAiringAnimeAdapter extends RecyclerView.Adapter<TopAiringAnimeAdapter.TopAiringAnimeViewHolder> {
    private final List<Anime> animeList;
    private Context context;
    public TopAiringAnimeAdapter(Context context, List<Anime> animeList) {
        this.context = context;
        this.animeList = animeList;
    }

    @NonNull
    @Override
    public TopAiringAnimeAdapter.TopAiringAnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_anime_horizontal, parent, false);
        return new TopAiringAnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopAiringAnimeAdapter.TopAiringAnimeViewHolder holder, int position) {
        Anime anime = animeList.get(position);

        Glide.with(holder.itemView.getContext())
                .load(anime.getImages().getWebp().getImageUrl())
                .into(holder.iv_cover);

        holder.tv_title.setText(anime.getTitle());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AnimeDetailActivity.class);
            intent.putExtra("ANIME_ID", anime.getId());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return animeList.size();
    }

    public class TopAiringAnimeViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_cover;
        TextView tv_title;

        public TopAiringAnimeViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_cover = itemView.findViewById(R.id.iv_cover);
            tv_title = itemView.findViewById(R.id.tv_title);
        }
    }
}
