package com.example.yokailist.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yokailist.R;
import com.example.yokailist.activities.anime.AddToMyListActivity;
import com.example.yokailist.activities.anime.AnimeDetailActivity;
import com.example.yokailist.data.response.animedetail.Anime;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AnimeAdapter extends RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder> {
    private final List<Anime> animeList;
    private Context context;

    public AnimeAdapter(Context context, List<Anime> animeList) {
        this.context = context;
        this.animeList = animeList;
    }

    @NonNull
    @Override
    public AnimeAdapter.AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_anime_vertical, parent, false);
        return new AnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeAdapter.AnimeViewHolder holder, int position) {
        Anime anime = animeList.get(position);

        String score;
        String episodes;
        String season;
        String members;

        if (anime.getImages().getWebp().getImageUrl() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(anime.getImages().getWebp().getImageUrl())
                    .into(holder.iv_cover);
        } else {
            holder.iv_cover.setImageResource(R.drawable.img_noimgpotrait);
        }

        if (anime.getScore() != 0.0 ) {
            score = String.valueOf(anime.getScore());
        } else {
            score = "N/A";
        }

        if (anime.getEpisodes() != 0 ) {
            episodes = String.valueOf(anime.getEpisodes()) + " ep";
        } else {
            episodes = " - ";
        }

        if (anime.getSeason() != null  && anime.getYear() != 0) {
            season = anime.getSeason() + " " + anime.getYear();
        } else if (anime.getType() != null && !anime.getType().equals("TV")) {
            season = " - ";
            holder.tv_season.setVisibility(View.GONE);
        } else {
            season = anime.getAired().getDate();
        }

        if (anime.getMembers() != 0 ) {
            members = NumberFormat.getNumberInstance(Locale.US).format(anime.getMembers());
        } else {
            members = " - ";
        }

        holder.tv_title.setText(anime.getTitle());
        holder.tv_score.setText(String.valueOf(score));
        holder.tv_episodes.setText(String.valueOf(episodes));
        holder.tv_season.setText(season);
        holder.tv_members.setText(members);

        holder.ib_myList.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddToMyListActivity.class);
            intent.putExtra("ANIME_ID", anime.getId());
            context.startActivity(intent);
        });

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

    public class AnimeViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_cover;
        ImageButton ib_myList;
        TextView tv_title, tv_score, tv_episodes, tv_season, tv_members;

        public AnimeViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_cover = itemView.findViewById(R.id.iv_cover);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_score = itemView.findViewById(R.id.tv_score);
            tv_episodes = itemView.findViewById(R.id.tv_episodes);
            tv_season = itemView.findViewById(R.id.tv_season);
            tv_members = itemView.findViewById(R.id.tv_members);
            ib_myList = itemView.findViewById(R.id.ib_myList);
        }
    }
}
