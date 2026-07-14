package com.hyeongju.routineapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// 오늘 위젯(위젯 4)의 스크롤 목록을 실제로 채우는 부분. 안드로이드 위젯 안에
// 스크롤되는 목록을 넣으려면 이 방식(RemoteViewsService + RemoteViewsFactory)이
// 필요함 — 위젯 2·3처럼 고정된 칸 몇 개를 미리 그려두는 방식으로는 "항목 개수가
// 매번 달라지고 스크롤도 되는 목록"을 만들 수 없어서 이 위젯에서 처음 씀.
public class TodayWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new TodayRemoteViewsFactory(getApplicationContext());
    }

    private static class TodayRemoteViewsFactory implements RemoteViewsFactory {
        private final Context context;
        private List<JSONObject> items = new ArrayList<>();

        TodayRemoteViewsFactory(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate() {
        }

        // 목록 줄 하나하나를 만들기 전에 딱 한 번 불림 — 무거운 일(데이터 읽기)은
        // 여기서 미리 끝내두고, getViewAt()은 이미 읽어둔 목록에서 꺼내 쓰기만 함.
        @Override
        public void onDataSetChanged() {
            List<JSONObject> next = new ArrayList<>();
            SharedPreferences prefs = context.getSharedPreferences(
                TodayWidgetProvider.PREFS_NAME, Context.MODE_PRIVATE);
            String raw = prefs.getString(TodayWidgetProvider.KEY_TODAY_DATA, null);
            if (raw != null) {
                try {
                    JSONArray arr = new JSONObject(raw).optJSONArray("items");
                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject it = arr.optJSONObject(i);
                            // 완료된 항목은 목록에서 아예 안 보이게 함 — payload는
                            // 원래 미완료 항목만 담고 있지만, 혹시 남아있는 옛 데이터를
                            // 대비한 안전장치로 한 번 더 걸러줌.
                            if (it != null && !it.optBoolean("done", false)) next.add(it);
                        }
                    }
                } catch (Exception e) {
                    // 데이터가 깨져 있으면 빈 목록으로
                }
            }
            items = next;
        }

        @Override
        public void onDestroy() {
            items.clear();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.widget_today_item);
            JSONObject it = items.get(position);
            String id = it.optString("id", "");
            String text = it.optString("text", "");
            String icon = it.optString("icon", "");
            String type = it.optString("type", "once");
            boolean done = it.optBoolean("done", false);

            int textId = idFor("item_text");
            row.setTextViewText(textId, (icon.isEmpty() ? "" : icon + " ") + text);
            row.setInt(textId, "setPaintFlags",
                done ? (Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG) : Paint.ANTI_ALIAS_FLAG);
            row.setTextColor(textId, ContextCompat.getColor(context,
                done ? R.color.widget_text_secondary : R.color.widget_text_primary));
            row.setImageViewResource(idFor("item_check"),
                done ? R.drawable.widget_check_on : R.drawable.widget_check_off);

            // 이 줄이 탭됐을 때 어떤 항목의 무엇을 바꿔야 하는지 알려주는 꼬리표.
            // "done을 뒤집어라"가 아니라 "눌렀을 때 이 상태(!done)가 될 것이다"를
            // 미리 담아 보냄 — TodayWidgetProvider.handleToggle()이 이 값을 그대로
            // 최종 상태로 저장하므로 중복 처리돼도 결과가 달라지지 않음.
            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(TodayWidgetProvider.EXTRA_ITEM_ID, id);
            fillInIntent.putExtra(TodayWidgetProvider.EXTRA_ITEM_TYPE, type);
            fillInIntent.putExtra(TodayWidgetProvider.EXTRA_NEW_DONE, !done);
            row.setOnClickFillInIntent(idFor("row_root"), fillInIntent);

            return row;
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
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        private int idFor(String name) {
            return context.getResources().getIdentifier(name, "id", context.getPackageName());
        }
    }
}
