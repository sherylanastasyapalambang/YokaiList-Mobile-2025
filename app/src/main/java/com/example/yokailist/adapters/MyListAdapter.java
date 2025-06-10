package com.example.yokailist.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yokailist.R;
import com.example.yokailist.activities.anime.AddToMyListActivity;
import com.example.yokailist.activities.anime.AnimeDetailActivity;
import com.example.yokailist.data.db.AnimeStatusHelper;
import com.example.yokailist.data.db.DatabaseContract;
import com.example.yokailist.data.models.AnimeStatus;
import com.example.yokailist.data.response.animedetail.Anime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.MyListViewHolder> {

    private final List<AnimeStatus> animeStatusList;
    private Context context;
    String progressString;
    public MyListAdapter(Context context, List<AnimeStatus> animeStatusList) {
        this.context = context;
        this.animeStatusList = animeStatusList;
    }

    @NonNull
    @Override
    public MyListAdapter.MyListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_my_list, parent, false);
        return new MyListAdapter.MyListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyListAdapter.MyListViewHolder holder, int position) {
        AnimeStatus animeStatus = animeStatusList.get(position);

        int id = animeStatus.getId();
        int animeId = animeStatus.getAnimeId();
        int status = animeStatus.getStatus();
        String imageUrl = animeStatus.getImageUrl();
        String title = animeStatus.getTitle();
        String type = animeStatus.getType();
        String season = animeStatus.getSeason();
        int progress = animeStatus.getProgress();
        int episodes = animeStatus.getEpisodes();

        if (imageUrl != null) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .into(holder.iv_cover);
        } else {
            holder.iv_cover.setImageResource(R.drawable.img_noimgpotrait);
        }

        String typeAndSeason = type + ", " + season;
        progressString = progress + " / " + episodes + " ep";

        holder.tv_title.setText(title);
        holder.tv_typeAndSeason.setText(typeAndSeason);
        holder.tv_progress.setText(progressString);

        holder.pb_episodes.setMax(episodes);
        holder.pb_episodes.setProgress(progress);

        if (status == 1) {
            holder.pb_episodes.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_dk_green));
        } else if (status == 2) {
            holder.pb_episodes.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_dk_blue));
        } else if (status == 3) {
            holder.pb_episodes.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_dk_yellow));
        } else if (status == 4) {
            holder.pb_episodes.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_dk_red));
        } else if (status == 5) {
            holder.pb_episodes.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_dk_grey));
        } else {
            holder.pb_episodes.setProgressDrawable(ContextCompat.getDrawable(context, R.drawable.progress_bar_dk_grey));
        }

        holder.ib_addEps.setOnClickListener(v -> {
            int currentProgress = animeStatus.getProgress();
            int maxEpisodes = animeStatus.getEpisodes();

            if (currentProgress < maxEpisodes) {
                int newProgress = currentProgress + 1;

                animeStatus.setProgress(newProgress);

                AnimeStatusHelper helper = AnimeStatusHelper.getInstance(context);
                helper.open();

                ContentValues values = new ContentValues();
                values.put(DatabaseContract.AnimeStatusColumns.PROGRESS, newProgress);
                values.put(DatabaseContract.AnimeStatusColumns.UPDATED_DATE,
                        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                .format(new Date()));


                if (animeStatus.getStatus() == 5 && currentProgress == 0) {
                    values.put(DatabaseContract.AnimeStatusColumns.STATUS, 1);
                    animeStatus.setStatus(1);

                    holder.pb_episodes.setProgressDrawable(
                            ContextCompat.getDrawable(context, R.drawable.progress_bar_dk_green)
                    );
                }

                long result = helper.update(String.valueOf(animeStatus.getId()), values);
                helper.close();

                if (result > 0) {
                    progressString = newProgress + " / " + maxEpisodes + " ep";
                    holder.tv_progress.setText(progressString);
                    holder.pb_episodes.setProgress(newProgress);

                    if (newProgress == maxEpisodes && animeStatus.getStatus() == 1) {
                        Toast.makeText(context,
                                "All episodes watched! You can mark as completed.",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to update progress", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context,
                        "Already at maximum episodes (" + maxEpisodes + ")",
                        Toast.LENGTH_SHORT).show();
            }
        });

        holder.ib_edit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddToMyListActivity.class);
            intent.putExtra("ANIME_ID", animeId);
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AnimeDetailActivity.class);
            intent.putExtra("ANIME_ID", animeId);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return animeStatusList.size();
    }

    public class MyListViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_typeAndSeason, tv_progress;
        ImageView iv_cover;
        ProgressBar pb_episodes;
        ImageButton ib_edit, ib_addEps;
        public MyListViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_cover = itemView.findViewById(R.id.iv_cover);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_typeAndSeason = itemView.findViewById(R.id.tv_typeAndSeason);
            tv_progress = itemView.findViewById(R.id.tv_progress);
            pb_episodes = itemView.findViewById(R.id.pb_episodes);
            ib_edit = itemView.findViewById(R.id.ib_edit);
            ib_addEps = itemView.findViewById(R.id.ib_addEps);
        }
    }
}
