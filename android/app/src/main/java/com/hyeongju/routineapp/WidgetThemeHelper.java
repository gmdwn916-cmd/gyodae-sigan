package com.hyeongju.routineapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.widget.RemoteViews;

// 다섯 위젯이 공통으로 쓰는 "지금 다크 모드로 그릴지" 판단(2026-07-17 추가).
// 앱 안 설정(설정 탭 "테마")에서 라이트/다크를 명시적으로 골랐으면 그 값을
// 최우선으로 따르고(휴대폰 시스템 설정과 달라도 무관), "시스템"을 골랐으면
// 예전처럼 기기 시스템 설정(Configuration.uiMode)을 그대로 따름 — JS의
// applyTheme()이 테마를 적용할 때마다 WidgetBridgePlugin.setThemeOverride()를
// 통해 이 값을 갱신해둔다.
// **원인이었던 버그**: 앱 안에서 "다크"로 바꿔도 위젯은 항상 시스템 설정만
// 보고 있어서, 휴대폰 자체는 라이트인 채로 앱만 다크로 바꾸면 위젯이 안
// 따라오는 것처럼 보였음 — 위젯 배경은 이 판단을 이미 쓰고 있었지만(각 Provider의
// updateOne), 그 판단 자체가 시스템 설정만 봤던 것이 원인.
final class WidgetThemeHelper {
    private WidgetThemeHelper() {}

    static final String PREFS_NAME = "widget_bridge";
    static final String KEY_THEME_OVERRIDE = "widget_theme_override"; // "light" | "dark" | ""(시스템 설정 따름)

    static boolean isDarkMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String override = prefs.getString(KEY_THEME_OVERRIDE, "");
        if ("dark".equals(override)) return true;
        if ("light".equals(override)) return false;
        return (context.getResources().getConfiguration().uiMode
            & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    // widget_text_primary는 라이트/다크에 따라 값이 다름(#1C1C1E / #FFFFFF) —
    // 리소스 자동 해석(@color, values-night)은 항상 실제 기기 시스템 설정만
    // 보고 결정되므로, 앱 안 설정을 따르게 하려면 isDarkMode() 판단으로 직접
    // 골라야 함. widget_text_secondary는 라이트/다크 값이 똑같아서(둘 다
    // #8E8E93) 이 문제 자체가 없어 그대로 ContextCompat.getColor()를 계속 씀.
    static int primaryTextColor(Context context) {
        return isDarkMode(context) ? 0xFFFFFFFF : 0xFF1C1C1E;
    }

    // 앱 안 설정(설정 탭 "테마")에서 라이트/다크를 명시적으로 골랐는지 —
    // 안 골랐으면("시스템") 아래 두 함수가 색을 우리가 미리 정해서 심는 대신
    // 리소스 참조(@color/@drawable)로 넘겨서, 홈 화면 런처가 그리는 그 순간의
    // 시스템 다크/라이트로 알아서 해석되게 함.
    private static boolean hasOverride(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String override = prefs.getString(KEY_THEME_OVERRIDE, "");
        return "dark".equals(override) || "light".equals(override);
    }

    // 앱을 안 켠 채로 휴대폰 시스템 다크/라이트만 바뀌면 위젯이 예전 밝기 그대로
    // 남아있던 문제(2026-09-06 수정, 사용자 신고 "다크모드 변경이 늦다") —
    // 원인: 배경색·글자색을 둘 다 push하는 순간의 판단으로 확정해서 심어두는데,
    // 위젯은 앱이 다시 열릴 때까지 다시 push될 계기가 없음(매니페스트에 걸어둔
    // CONFIGURATION_CHANGED 수신은 안드로이드가 매니페스트 등록 리시버에게는
    // 주지 않는 종류라 실제로는 한 번도 동작한 적이 없었음).
    // 고침: 앱 안 테마가 "시스템"이고 안드로이드 12(API 31) 이상이면, 배경과
    // 글자색을 값이 아니라 리소스로 넘겨서 런처가 매번 그 순간의 테마로 직접
    // 해석하게 함 — 앱을 안 열어도 시스템 테마를 바로 따라감.
    // **배경과 글자색은 반드시 항상 같은 방식으로 골라야 함**(한쪽만 리소스로
    // 넘기면 흰 배경에 흰 글자 같은 사고가 남 — 예전에 실제로 겪은 버그) —
    // 그래서 두 함수의 조건이 완전히 동일하고, 안드로이드 12 미만이나 앱에서
    // 테마를 직접 고른 경우엔 둘 다 예전처럼 우리가 판단해 심음.
    private static boolean useAdaptiveResources(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hasOverride(context);
    }

    static void applyBackground(Context context, RemoteViews views, int viewId) {
        if (useAdaptiveResources(context)) {
            // widget_background.xml은 @color/widget_bg(values/ + values-night/)를
            // 참조하므로 런처가 그리는 시점의 테마로 자동 해석됨.
            views.setInt(viewId, "setBackgroundResource", R.drawable.widget_background);
            return;
        }
        views.setInt(viewId, "setBackgroundResource",
            isDarkMode(context) ? R.drawable.widget_background_dark : R.drawable.widget_background_light);
    }

    // 오늘 할일 위젯 상단 근무 배지의 둥근 밑그림 — 색이 배경색(widget_bg)과
    // 같아야 근무색을 옅게 얹은 느낌이 자연스러워서, 배경과 항상 같은 기준으로
    // 고름(위 applyBackground 주석 참고).
    static int badgeBaseRes(Context context) {
        if (useAdaptiveResources(context)) return R.drawable.widget_badge_base;
        return isDarkMode(context) ? R.drawable.widget_badge_base_dark : R.drawable.widget_badge_base_light;
    }

    static void applyPrimaryText(Context context, RemoteViews views, int viewId) {
        if (useAdaptiveResources(context)) {
            views.setColorStateList(viewId, "setTextColor", R.color.widget_text_primary);
            return;
        }
        views.setTextColor(viewId, primaryTextColor(context));
    }
}
