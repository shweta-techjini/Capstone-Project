package taggedit.com.teggedit.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import taggedit.com.teggedit.R;
import taggedit.com.teggedit.activity.HomeActivity;
import taggedit.com.teggedit.utility.MyLogger;

/**
 * Created by Shweta on 1/24/17.
 */

public class TagWidgetProvide extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        MyLogger.d(this, "onUpdate Tagwidget provider");
        // Could be multiple widgets
        for (int appWidgetId : appWidgetIds) {
            MyLogger.d(this, "onUpdate Tagwidget provider");
            Intent intent = new Intent(context, HomeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            // set the widget services
            Intent widgetService = new Intent(context, TagWidgetService.class);
            widgetService.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(R.id.widget_listview, widgetService);
            views.setEmptyView(R.id.widget_listview, R.id.empty_view);

            // set the item click for each item in the list
            Intent homeActivity = new Intent(context, HomeActivity.class);
            PendingIntent searchPhotoFromTag = PendingIntent.getActivity(context, 0, homeActivity, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_listview, searchPhotoFromTag);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MyLogger.d(this, "onReceive Tagwidget provider");
        super.onReceive(context, intent);
    }
}