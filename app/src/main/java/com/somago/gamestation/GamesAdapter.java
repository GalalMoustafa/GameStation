package com.somago.gamestation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.somago.gamestation.Models.Game;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GamesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    List<Game> games;
    Activity context;

    public GamesAdapter(List<Game> games, Activity context) {
        this.games = games;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .game_item, parent, false);
        return new GameHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GameHolder gameHolder = (GameHolder) holder;
        if(!games.get(position).getImage().isEmpty()){
            Picasso.get().load(games.get(position).getImage()).placeholder(R.drawable.placeholder_games_cover).into(gameHolder.gameImage);
        }
        gameHolder.gameName.setText(games.get(position).getName());
        gameHolder.gamePrice.setText(games.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    private class GameHolder extends RecyclerView.ViewHolder{

        ImageView gameImage;
        TextView gameName;
        TextView gamePrice;

        GameHolder(View view){
            super(view);
            gameImage = view.findViewById(R.id.game_image_card);
            gameName = view.findViewById(R.id.game_title_card);
            gamePrice = view.findViewById(R.id.game_price_card);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(context.getResources().getString(R.string.game_bundle_name), games.get(getAdapterPosition()));
                    intent.putExtras(bundle);
                    ((MainActivity)context).startActivity(intent);
                }
            });
        }
    }
}
