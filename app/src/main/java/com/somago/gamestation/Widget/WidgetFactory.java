package com.somago.gamestation.Widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.somago.gamestation.Models.Game;
import com.somago.gamestation.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class WidgetFactory implements RemoteViewsService.RemoteViewsFactory{

    ArrayList<Game> games;
    Context context;
    int _id;

    public WidgetFactory(Intent intent, Context context) {
        _id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 1);
        this.context = context;
        games = new ArrayList<>();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        games = GamesWidget.gamesList;
        Log.d("WidgetGamesSize" , games.size() + " ");
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        views.setTextViewText(R.id.game_title_card, games.get(i).getName());
        views.setTextViewText(R.id.game_price_card, games.get(i).getPrice());
        views.setImageViewResource(R.id.game_image_card, R.drawable.placeholder_games_cover);
        views.setImageViewUri(R.id.game_image_card, Uri.parse(games.get(i).getImage()));
//        Picasso.get().load(games.get(i).getImage()).placeholder(R.drawable.placeholder_games_cover).into();
//        views.setImageViewUri(R.id.game_image_card, Uri.parse(new Game().getImage()));
        try {
            Bitmap bitmap = Picasso.get().load(games.get(i).getImage()).get();
            views.setImageViewBitmap(R.id.game_image_card,bitmap);
            //views.setImageViewResource(R.id.game_image_card, R.drawable.placeholder_games_cover);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
