package com.hyeongju.routineapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

// 날짜가 바뀌면(자정·시간 변경·재부팅) 앱을 안 열어도 위젯을 새 날짜로 넘기는
// 도우미(2026-11-17 추가, 사용자 신고 — "00시가 넘어가도, 위젯을 조작해도
// 위젯 날짜가 그대로다").
//
// 원래 구조: 위젯 내용은 전부 JS가 계산해서 넣어준 결과라, 앱이 안 열리면
// 날짜가 바뀌어도 어제 내용이 그대로 남았음(CLAUDE.md "한계(공통)" 참고).
//
// 고친 방식(근무·할 일 계산은 여전히 JS만 함 — 네이티브는 고르기만):
// - 오늘 위젯·오늘 팀 근무 위젯: JS가 오늘 것과 함께 앞으로 7일치를
//   "upcoming"에 미리 실어 보냄 → 날짜가 바뀌면 그날 것을 꺼내 오늘 자리로 옮김.
// - 스케줄 위젯: 이미 16주치 날짜가 있으므로 "오늘" 표시(isToday)만 새 날짜로
//   다시 매기고, 보이는 주를 오늘이 든 주로 되돌림.
// - 달력 위젯: 오늘 표시는 원래 네이티브가 현재 날짜로 그림 → 달이 바뀌었으면
//   보이는 달만 오늘이 든 달로 되돌림.
// 실행 시점: ① 자정 직후 AlarmManager(정확 알람 권한 없이 쓰는 방식이라 몇 분
// 늦을 수 있음) ② 시간·시간대 변경, 재부팅, 앱 업데이트 ③ 위젯이 다시 그려질
// 때마다(위젯을 누르는 등) ensureToday()로 "마지막으로 맞춘 날짜"와 비교.
// 앱을 7일 넘게 안 열면 upcoming이 바닥나서 오늘 위젯은 그 뒤로 안 바뀜(앱을
// 한 번 열면 다시 채워짐).
final class WidgetDayRollover {
    private WidgetDayRollover() {}

    static final String ACTION_ROLLOVER = "com.hyeongju.routineapp.WIDGET_DAY_ROLLOVER";
    private static final String KEY_LAST_DATE = "widget_rollover_last_date";

    static String todayStr() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
    }

    // 위젯이 그려질 때마다 부름 — 날짜가 바뀌었을 때만 실제로 넘김.
    // 날짜를 넘겼으면 true.
    static boolean ensureToday(Context context) {
        boolean changed = false;
        try {
            SharedPreferences prefs = context.getSharedPreferences(TodayWidgetProvider.PREFS_NAME, Context.MODE_PRIVATE);
            String today = todayStr();
            if (!today.equals(prefs.getString(KEY_LAST_DATE, ""))) {
                applyData(context, today);
                changed = true;
                // 지금 그려지는 위젯 하나만이 아니라 나머지 위젯(오늘 위젯의 목록
                // 포함)도 새 날짜로 다시 그리도록, 지금 그리기가 끝난 뒤에 한 번 더
                // 전체 새로 그리기를 예약함(그리는 도중에 바로 부르면 재진입이 됨).
                final Context app = context.getApplicationContext();
                new android.os.Handler(android.os.Looper.getMainLooper()).post(new Runnable() {
                    @Override public void run() { rollover(app); }
                });
            }
            scheduleNext(context);
        } catch (Exception e) {
            // 무시 — 위젯 그리기 자체는 막지 않음
        }
        return changed;
    }

    // 리시버(자정 알람 등)에서 부름 — 데이터를 넘기고 위젯 전부 다시 그림.
    static void rollover(Context context) {
        try { applyData(context, todayStr()); } catch (Exception e) {}
        try { TodayWidgetProvider.refreshAll(context); } catch (Exception e) {}
        try { TeamTodayWidgetProvider.refreshAll(context); } catch (Exception e) {}
        try { ScheduleWidgetProvider.refreshAll(context); } catch (Exception e) {}
        try { MonthCalendarWidgetProvider.refreshAll(context); } catch (Exception e) {}
        scheduleNext(context);
    }

    private static void applyData(Context context, String today) {
        SharedPreferences prefs = context.getSharedPreferences(TodayWidgetProvider.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        // 반복 실행 방지 표시를 먼저 남김(아래에서 위젯을 다시 그려도 재진입 안 함)
        ed.putString(KEY_LAST_DATE, today);

        String rolledToday = rollPayload(prefs.getString(TodayWidgetProvider.KEY_TODAY_DATA, null), today);
        if (rolledToday != null) ed.putString(TodayWidgetProvider.KEY_TODAY_DATA, rolledToday);

        String rolledTeam = rollPayload(prefs.getString(TeamTodayWidgetProvider.KEY_TEAM_TODAY_DATA, null), today);
        if (rolledTeam != null) ed.putString(TeamTodayWidgetProvider.KEY_TEAM_TODAY_DATA, rolledTeam);

        rollSchedule(prefs, ed, today);
        rollMonth(prefs, ed, today);
        ed.apply();
    }

    // {date, ..., upcoming:{date: payload}} — 오늘이 아니면 upcoming에서 오늘 것을
    // 꺼내 그 자리에 놓고, 지난 날짜는 upcoming에서 버림. 바꿀 게 없으면 null.
    private static String rollPayload(String raw, String today) {
        if (raw == null) return null;
        try {
            JSONObject obj = new JSONObject(raw);
            if (today.equals(obj.optString("date", ""))) return null;
            JSONObject upcoming = obj.optJSONObject("upcoming");
            if (upcoming == null) return null;
            JSONObject next = upcoming.optJSONObject(today);
            if (next == null) return null;
            JSONObject rest = new JSONObject();
            Iterator<String> keys = upcoming.keys();
            while (keys.hasNext()) {
                String k = keys.next();
                if (k.compareTo(today) > 0) rest.put(k, upcoming.get(k));
            }
            next.put("upcoming", rest);
            return next.toString();
        } catch (Exception e) {
            return null;
        }
    }

    private static void rollSchedule(SharedPreferences prefs, SharedPreferences.Editor ed, String today) {
        String raw = prefs.getString(ScheduleWidgetProvider.KEY_SCHEDULE_DATA, null);
        if (raw == null) return;
        try {
            JSONObject obj = new JSONObject(raw);
            JSONArray weeks = obj.optJSONArray("weeks");
            if (weeks == null) return;
            int todayWeek = -1;
            for (int w = 0; w < weeks.length(); w++) {
                JSONObject week = weeks.optJSONObject(w);
                JSONArray days = week == null ? null : week.optJSONArray("days");
                if (days == null) continue;
                for (int d = 0; d < days.length(); d++) {
                    JSONObject day = days.optJSONObject(d);
                    if (day == null) continue;
                    boolean isToday = today.equals(day.optString("date", ""));
                    day.put("isToday", isToday);
                    if (isToday) todayWeek = w;
                }
            }
            ed.putString(ScheduleWidgetProvider.KEY_SCHEDULE_DATA, obj.toString());
            if (todayWeek >= 0) ed.putInt(ScheduleWidgetProvider.KEY_WEEK_START_INDEX, todayWeek);
        } catch (Exception e) {
            // 무시
        }
    }

    private static void rollMonth(SharedPreferences prefs, SharedPreferences.Editor ed, String today) {
        String raw = prefs.getString(MonthCalendarWidgetProvider.KEY_MONTH_DATA, null);
        if (raw == null) return;
        try {
            JSONArray months = new JSONObject(raw).optJSONArray("months");
            if (months == null) return;
            for (int m = 0; m < months.length(); m++) {
                JSONObject month = months.optJSONObject(m);
                JSONArray days = month == null ? null : month.optJSONArray("days");
                if (days == null) continue;
                for (int d = 0; d < days.length(); d++) {
                    JSONObject day = days.optJSONObject(d);
                    if (day != null && !day.optBoolean("otherMonth", false)
                            && today.equals(day.optString("date", ""))) {
                        ed.putInt(MonthCalendarWidgetProvider.KEY_DISPLAY_INDEX, m);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            // 무시
        }
    }

    // 다음 자정 직후에 rollover가 돌도록 예약(같은 PendingIntent라 여러 번 불러도
    // 하나만 남음). 정확 알람 권한이 없어도 되는 setWindow — 안드로이드가 최대
    // 10분 정도 늦출 수 있음(화면이 꺼져 있으면 켜질 때 처리돼도 무방).
    static void scheduleNext(Context context) {
        try {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (am == null) return;
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_YEAR, 1);
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 5);
            c.set(Calendar.MILLISECOND, 0);
            Intent intent = new Intent(context, WidgetDayRolloverReceiver.class);
            intent.setAction(ACTION_ROLLOVER);
            PendingIntent pi = PendingIntent.getBroadcast(context, 7101, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            am.setWindow(AlarmManager.RTC, c.getTimeInMillis(), 60 * 1000L, pi);
        } catch (Exception e) {
            // 무시 — 다음 위젯 그리기 때 ensureToday가 다시 맞춤
        }
    }
}
