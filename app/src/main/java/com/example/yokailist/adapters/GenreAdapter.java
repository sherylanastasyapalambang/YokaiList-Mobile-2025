package com.example.yokailist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yokailist.R;
import com.example.yokailist.data.response.genre.Genre;

import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder>{
    private final List<Genre> genreList;
    private final List<Genre> selectedGenres = new ArrayList<>();
    private OnGenresUpdateListener listener;
    private Context context;

    public interface OnGenresUpdateListener {
        void onGenresUpdated(String genreIds);
    }

    public GenreAdapter(Context context, List<Genre> genreList, OnGenresUpdateListener listener) {
        this.context = context;
        this.genreList = genreList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GenreAdapter.GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_genre, parent, false);
        return new GenreAdapter.GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreAdapter.GenreViewHolder holder, int position) {
        Genre genre = genreList.get(position);

        holder.btn_genre.setText(genre.getName());

        boolean isSelected = selectedGenres.contains(genre);
        holder.btn_genre.setBackground(ContextCompat.getDrawable(
                holder.itemView.getContext(),
                isSelected ? R.drawable.shape_dk_genre_true : R.drawable.shape_dk_genre
        ));

        holder.btn_genre.setOnClickListener(v -> {
            if (selectedGenres.contains(genre)) {
                selectedGenres.remove(genre);
            } else {
                selectedGenres.add(genre);
            }
            notifyItemChanged(position);

            String genreIds = getSelectedGenreIds();
            listener.onGenresUpdated(genreIds);
        });
    }

    public String getSelectedGenreIds() {
        if (selectedGenres.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (Genre genre : selectedGenres) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(genre.getId());
        }
        return sb.toString();
    }

    public void clearSelections() {
        selectedGenres.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    public class GenreViewHolder extends RecyclerView.ViewHolder {
        Button btn_genre;
        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            btn_genre = itemView.findViewById(R.id.btn_genre);
        }
    }

    public void selectGenre(Genre genre) {
        if (!selectedGenres.contains(genre)) {
            selectedGenres.add(genre);
            notifyDataSetChanged();
        }
    }

    public void refreshSelectionState() {
        notifyDataSetChanged();
    }
}