package com.example.yokailist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yokailist.R;
import com.example.yokailist.data.response.character.Character;
import com.example.yokailist.data.response.character.DataItem;

import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.CharacterViewHolder> {
    private Context context;
    private List<DataItem> characterList;

    public CharacterAdapter(Context context, List<DataItem> characterList) {
        this.context = context;
        this.characterList = characterList;
    }

    @NonNull
    @Override
    public CharacterAdapter.CharacterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_character, parent, false);
        return new CharacterAdapter.CharacterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterAdapter.CharacterViewHolder holder, int position) {
        DataItem dataItem = characterList.get(position);
        Character character = dataItem.getCharacter();

        if (character != null) {
            holder.tv_name.setText(character.getName());

            if (character.getImages() != null && character.getImages().getWebp() != null) {
                String imageUrl = character.getImages().getWebp().getImageUrl();
                Glide.with(context)
                        .load(imageUrl)
                        .into(holder.iv_image);
            }
        }
    }

    @Override
    public int getItemCount() {
        return characterList != null ? characterList.size() : 0;
    }

    public class CharacterViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_image;
        TextView tv_name;

        public CharacterViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_image = itemView.findViewById(R.id.iv_image);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}