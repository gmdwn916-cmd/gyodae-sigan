package com.hyeongju.routineapp;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

// 위젯에서 할 일을 추가했을 때, 앱을 열지 않아도 위젯 화면에 바로 보이게
// 하는 도우미(2026-10-04 추가, 사용자 요청 — "위젯에서 아무리 추가해도 앱을
// 열기 전엔 위젯에 안 보여서 답답하다").
//
// 원래 구조: 위젯 입력은 임시 우편함(pending_inbox_items/pending_dated_items)에만
// 쌓이고, 위젯이 실제로 그리는 표시용 캐시(inbox_widget_data/today_widget_data)는
// JS 앱이 켜질 때만 새로 만들어졌음 — 그래서 앱을 열기 전엔 위젯에 안 보였음.
//
// 고친 방식: 우편함에 넣는 것과 동시에 표시용 캐시에도 그 항목을 "임시로" 끼워
// 넣고 해당 위젯을 즉시 다시 그림(낙관적 갱신 — TodayWidgetProvider.handleToggle이
// 체크할 때 쓰는 방식과 같음). 진짜 데이터 반영(정렬·시간대 자동 배치·카테고리
// 아이콘 등)은 여전히 앱이 열릴 때 JS가 하고, 그때 JS가 캐시 전체를 새로 덮어써서
// 임시 항목은 자연스럽게 정식 항목으로 바뀜.
//
// 오늘 할 일은 id를 여기서 미리 만들어 우편함에도 같이 넣음 — JS
// syncWidgetDatedItems()가 이 id를 그대로 써서 일정을 만들기 때문에, 앱을 열기
// 전에 위젯에서 이 임시 항목을 체크해도(=today_widget_pending_toggles에 이 id로
// 쌓임) 앱이 열릴 때 올바른 일정에 완료 처리가 붙음(syncWidgetDatedItems가
// syncWidgetTodayToggles보다 먼저 실행되는 순서에 의존).
final class WidgetLiveUpdate {
    private WidgetLiveUpdate() {}

    // JS genId()와 같은 모양(접두어 + 36진수 시각 + 무작위 4글자).
    static String genId(String prefix) {
        String rand = Long.toString((long) (Math.random() * 1679616L), 36);
        while (rand.length() < 4) rand = "0" + rand;
        return prefix + Long.toString(System.currentTimeMillis(), 36) + rand;
    }

    // 미배치 위젯(InboxWidgetProvider) 캐시에 항목 추가 + 즉시 다시 그리기.
    static void addInboxItem(Context context, String text) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(
                InboxWidgetProvider.PREFS_NAME, Context.MODE_PRIVATE);
            String raw = prefs.getString(InboxWidgetProvider.KEY_INBOX_DATA, null);
            JSONObject obj;
            try { obj = raw == null ? new JSONObject() : new JSONObject(raw); }
            catch (Exception e) { obj = new JSONObject(); }
            JSONArray items = obj.optJSONArray("items");
            if (items == null) items = new JSONArray();
            JSONObject it = new JSONObject();
            it.put("id", genId("i"));
            it.put("text", text);
            items.put(it); // JS도 state.inbox 끝에 push하므로 같은 위치
            obj.put("items", items);
            obj.put("count", items.length());
            prefs.edit().putString(InboxWidgetProvider.KEY_INBOX_DATA, obj.toString()).apply();
        } catch (Exception e) {
            // 무시 — 앱을 열면 어차피 JS가 정상 데이터로 다시 채움
        }
        try { InboxWidgetProvider.refreshAll(context); } catch (Exception e) {}
    }

    // 오늘 할일 위젯(TodayWidgetProvider) 캐시에 항목 추가 + 즉시 다시 그리기.
    // 위젯이 보여주고 있는 날짜(today_widget_data.date)와 같은 날짜일 때만 넣음
    // (스케줄 위젯 팝업에서 다른 날짜에 추가한 건 오늘 위젯과 무관).
    static void addTodayItem(Context context, String id, String text, String date) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(
                TodayWidgetProvider.PREFS_NAME, Context.MODE_PRIVATE);
            String raw = prefs.getString(TodayWidgetProvider.KEY_TODAY_DATA, null);
            if (raw == null) return;
            JSONObject obj = new JSONObject(raw);
            if (date == null || !date.equals(obj.optString("date", ""))) return;
            JSONArray items = obj.optJSONArray("items");
            if (items == null) items = new JSONArray();
            JSONObject it = new JSONObject();
            it.put("id", id);
            it.put("important", false);
            it.put("text", text);
            it.put("done", false);
            it.put("icon", "");
            it.put("type", "once");
            // 완료된 항목(완료 표시 켜둔 경우)은 항상 맨 아래라서, 그 바로 앞에 끼움.
            JSONArray next = new JSONArray();
            boolean inserted = false;
            for (int i = 0; i < items.length(); i++) {
                JSONObject cur = items.optJSONObject(i);
                if (!inserted && cur != null && cur.optBoolean("done", false)) {
                    next.put(it);
                    inserted = true;
                }
                if (cur != null) next.put(cur);
            }
            if (!inserted) next.put(it);
            obj.put("items", next);
            prefs.edit().putString(TodayWidgetProvider.KEY_TODAY_DATA, obj.toString()).apply();
        } catch (Exception e) {
            return;
        }
        try { TodayWidgetProvider.refreshAll(context); } catch (Exception e) {}
    }

    // 스케줄 위젯(ScheduleWidgetProvider) 캐시의 그 날짜 칸에 항목 추가 + 즉시
    // 다시 그리기(2026-10-05 추가, 사용자 신고 — "스케줄 위젯으로 할 일을 추가하면
    // 앱에는 보이는데 스케줄 위젯에는 안 보인다"). JS buildSchedulePayload()와 같은
    // 모양({text:'• '+내용, done, important})으로 allTodos에 넣고, 칸에 그리는
    // todos는 JS와 똑같이 allTodos 앞 3개로 다시 맞춤. 완료 항목(완료 표시를
    // 켜둔 경우)은 항상 맨 아래라서 그 바로 앞에 끼움. 위젯이 보고 있던 주
    // 위치(KEY_WEEK_START_INDEX)는 건드리지 않음.
    static void addScheduleItem(Context context, String text, String date) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(
                ScheduleWidgetProvider.PREFS_NAME, Context.MODE_PRIVATE);
            String raw = prefs.getString(ScheduleWidgetProvider.KEY_SCHEDULE_DATA, null);
            if (raw == null || date == null) return;
            JSONObject obj = new JSONObject(raw);
            JSONArray weeks = obj.optJSONArray("weeks");
            if (weeks == null) return;
            boolean found = false;
            for (int w = 0; w < weeks.length() && !found; w++) {
                JSONObject week = weeks.optJSONObject(w);
                JSONArray days = week == null ? null : week.optJSONArray("days");
                if (days == null) continue;
                for (int d = 0; d < days.length(); d++) {
                    JSONObject day = days.optJSONObject(d);
                    if (day == null || !date.equals(day.optString("date", ""))) continue;
                    JSONArray all = day.optJSONArray("allTodos");
                    if (all == null) all = new JSONArray();
                    JSONObject it = new JSONObject();
                    it.put("text", "\u2022 " + text);
                    it.put("done", false);
                    it.put("important", false);
                    JSONArray next = new JSONArray();
                    boolean inserted = false;
                    for (int i = 0; i < all.length(); i++) {
                        JSONObject cur = all.optJSONObject(i);
                        if (!inserted && cur != null && cur.optBoolean("done", false)) {
                            next.put(it);
                            inserted = true;
                        }
                        if (cur != null) next.put(cur);
                    }
                    if (!inserted) next.put(it);
                    JSONArray todos = new JSONArray();
                    for (int i = 0; i < next.length() && i < 3; i++) todos.put(next.get(i));
                    day.put("allTodos", next);
                    day.put("todos", todos);
                    found = true;
                    break;
                }
            }
            if (!found) return; // 위젯이 들고 있는 범위 밖 날짜 — 앱을 열면 반영됨
            prefs.edit().putString(ScheduleWidgetProvider.KEY_SCHEDULE_DATA, obj.toString()).apply();
        } catch (Exception e) {
            return;
        }
        try { ScheduleWidgetProvider.refreshAll(context); } catch (Exception e) {}
    }
}
