package com.hyeongju.routineapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class QuickAddWidgetProvider extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_quick_add);
            Intent intent = new Intent(context, QuickAddActivity.class);
            // NEW_TASK + MULTIPLE_TASK: QuickAddActivity의 taskAffinity=""와 같이 써야
            // 앱이 이미 켜져 있어도(백그라운드/최근앱) 그 작업(task)을 앞으로 안 끌어오고,
            // 입력창만 별도의 새 작업으로 뜸(안 그러면 위젯 누를 때 앱까지 같이 열림).
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            views.setOnClickPendingIntent(R.id.widget_add_button, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
