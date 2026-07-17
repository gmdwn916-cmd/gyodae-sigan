package com.hyeongju.routineapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

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
}
