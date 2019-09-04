package com.somago.gamestation.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.somago.gamestation.MainActivity;
import com.somago.gamestation.Models.Game;
import com.somago.gamestation.R;

import java.util.ArrayList;

public class GamesWidget extends AppWidgetProvider {

    static ArrayList<Game> gamesList = new ArrayList<>();
    static int index = 0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.games_grid_widget);
        views.setTextViewText(R.id.games_widget_title, context.getResources().getString(R.string.app_name));
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("index",index);
        Intent service = new Intent(context,GamesWidgetService.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, i,PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.games_widget_title, pendingIntent);
        views.setRemoteAdapter(R.id.games_widget_grid, service);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void sendRefreshBroadcast(Context context, ArrayList<Game> games, int i) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        gamesList = games;
        index = i;
        intent.setComponent(new ComponentName(context, GamesWidget.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName componentName = new ComponentName(context, GamesWidget.class);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.games_grid_widget);
            views.setTextViewText(R.id.games_widget_title, context.getResources().getString(R.string.app_name));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetManager.getAppWidgetIds(componentName), R.id.games_widget_grid);
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }
}
