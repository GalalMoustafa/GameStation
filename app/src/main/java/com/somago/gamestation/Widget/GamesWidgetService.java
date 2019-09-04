package com.somago.gamestation.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class GamesWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new WidgetFactory(intent, getApplicationContext()));
    }
}
