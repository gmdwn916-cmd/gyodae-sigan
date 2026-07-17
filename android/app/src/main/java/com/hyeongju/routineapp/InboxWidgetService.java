package com.hyeongju.routineapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

// 미배치 위젯(위젯 5)의 스크롤 목록을 실제로 채우는 부분 — 위젯 4
// (TodayWidgetService)와 같은 방식(RemoteViewsService + RemoteViewsFactory),
// 다만 체크 처리가 없어서 훨씬 단순함(텍스트만 표시, 줄마다 빈 fillInIntent만
// 붙여서 탭하면 InboxWidgetProvider가 만든 템플릿(앱 열기)이 그대로 실행됨).
public class InboxWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new InboxRemoteViewsFactory(getApplicationContext());
    }

    private static class InboxRemoteViewsFactory implements RemoteViewsFactory {
        private final Context context;
        private List<String> texts = new ArrayList<>();

        InboxRemoteViewsFactory(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate() {
        }

        @Override
        public void onDataSetChanged() {
            List<String> next = new ArrayList<>();
            SharedPreferences prefs = context.getSharedPreferences(
                InboxWidgetProvider.PREFS_NAME, Context.MODE_PRIVATE);
            String raw = prefs.getString(InboxWidgetProvider.KEY_INBOX_DATA, null);
            if (raw != null) {
                try {
                    JSONArray arr = new JSONObject(raw).optJSONArray("items");
                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject it = arr.optJSONObject(i);
                            if (it != null) next.add(it.optString("text", ""));
                        }
                    }
                } catch (Exception e) {
                    // 데이터가 깨져 있으면 빈 목록으로
                }
            }
            texts = next;
        }

        @Override
        public void onDestroy() {
            texts.clear();
        }

        @Override
        public int getCount() {
            return texts.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews row = new RemoteViews(context.getPackageName(), R.layout.widget_inbox_item);
            row.setTextViewText(idFor("inbox_item_text"), texts.get(position));
            // 이 줄의 XML 기본 textColor(@color/widget_text_primary)는 실제 기기
            // 시스템 다크모드만 보고 자동으로 해석되므로, 앱 안 테마 설정을
            // 따르게 하려면 여기서 직접 색을 심어야 함(2026-07-17 추가 — 다른
            // 위젯들은 이미 다 이렇게 하고 있었는데 이 줄만 빠져 있었음).
            row.setTextColor(idFor("inbox_item_text"), WidgetThemeHelper.primaryTextColor(context));
            // 모든 줄이 항상 같은 동작(앱 열기)이라 특별한 값을 안 실은 빈
            // fillInIntent만 붙임 — 그래도 이게 있어야 탭에 반응함(RemoteViews
            // 컬렉션 위젯의 제약).
            row.setOnClickFillInIntent(idFor("inbox_item_row"), new Intent());
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
