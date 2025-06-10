package com.example.yokailist.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;
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
import com.example.yokailist.data.db.AnimeStatusHelper;
import com.example.yokailist.data.db.MappingHelper;
import com.example.yokailist.data.models.AnimeStatus;
import com.example.yokailist.data.response.AnimeDetailResponse;
import com.example.yokailist.data.response.animedetail.Anime;
import com.example.yokailist.data.response.animedetail.GenresItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SeasonalAnimeAdapter extends RecyclerView.Adapter<SeasonalAnimeAdapter.SeasonalAnimeViewHolder> {
    private final List<Anime> animeList;
    private final Map<Integer, AnimeStatus> animeStatusMap;
    private final Context context;
    private boolean isEdit;

    public SeasonalAnimeAdapter(Context context, List<Anime> animeList, Map<Integer, AnimeStatus> animeStatusMap) {
        this.context = context;
        this.animeList = animeList;
        this.animeStatusMap = animeStatusMap;
    }


    @NonNull
    @Override
    public SeasonalAnimeAdapter.SeasonalAnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_anime_grid, parent, false);
        return new SeasonalAnimeAdapter.SeasonalAnimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeasonalAnimeAdapter.SeasonalAnimeViewHolder holder, int position) {
        Anime anime = animeList.get(position);

        int animeId = anime.getId();

        AnimeStatus animeStatus = animeStatusMap.get(anime.getId());

        if (animeStatus != null) {
            isEdit = true;
        } else {
            isEdit = false;
        }

        if (isEdit) {
            holder.ib_myList.setImageResource(R.drawable.ic_dk_edit_white);
            if (animeStatus.getStatus() == 1) {
                holder.ib_myList.setBackgroundResource(R.drawable.shape_status_green);
            } else if (animeStatus.getStatus() == 2) {
                holder.ib_myList.setBackgroundResource(R.drawable.shape_status_blue);
            } else if (animeStatus.getStatus() == 3) {
                holder.ib_myList.setBackgroundResource(R.drawable.shape_status_yellow);
            } else if (animeStatus.getStatus() == 4) {
                holder.ib_myList.setBackgroundResource(R.drawable.shape_status_red);
            }  else if (animeStatus.getStatus() == 5) {
                holder.ib_myList.setBackgroundResource(R.drawable.shape_status_grey);
            }
        } else {
            holder.ib_myList.setImageResource(R.drawable.ic_dk_addlist);
            holder.ib_myList.setBackgroundResource(R.drawable.shape_darker_gray);
        }

        if (anime.getImages() == null || anime.getImages().getWebp() == null) {
            holder.iv_cover.setImageResource(R.drawable.img_noimgpotrait);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(anime.getImages().getWebp().getImageUrl())
                    .into(holder.iv_cover);
        }

        String members = NumberFormat.getNumberInstance(Locale.US).format(anime.getMembers());
        String score;

        if (anime.getScore() != 0.0 ) {
            score = String.valueOf(anime.getScore());
        } else {
            score = "N/A";
        }

        String title = anime.getTitle();
        if (title.length() > 25) {
            title = title.substring(0, 25) + "...";
        }
        holder.tv_title.setText(title);

        holder.tv_title.setText(title);
        holder.tv_genres.setText(formatGenres(anime.getGenres()));
        holder.tv_score.setText(score);
        holder.tv_members.setText(members);

        holder.ib_myList.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddToMyListActivity.class);
            intent.putExtra("ANIME_ID", animeId);
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

    public class SeasonalAnimeViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title, tv_genres, tv_score, tv_members;
        ImageView iv_cover;
        ImageButton ib_myList;

        public SeasonalAnimeViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_genres = itemView.findViewById(R.id.tv_genres);
            tv_score = itemView.findViewById(R.id.tv_score);
            tv_members = itemView.findViewById(R.id.tv_members);
            iv_cover = itemView.findViewById(R.id.iv_cover);
            ib_myList = itemView.findViewById(R.id.ib_myList);
        }
    }

    private String formatGenres(List<GenresItem> genres) {
        if (genres == null || genres.isEmpty()) return "-";

        List<String> genreNames = new ArrayList<>();
        for (GenresItem genre : genres) {
            genreNames.add(genre.getName());
        }
        return TextUtils.join(", ", genreNames);
    }


}
