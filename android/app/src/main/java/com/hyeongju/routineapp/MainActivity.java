package com.hyeongju.routineapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebSettings;

import com.getcapacitor.BridgeActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends BridgeActivity {
    // 위젯(2/3/4/5)을 탭했을 때 "어느 화면으로 바로 들어가야 하는지"를 담아서
    // MainActivity를 여는 인텐트에 실어 보낸 값 — WidgetBridgePlugin의
    // getPendingNavTarget()/clearPendingNavTarget()이 이 키로 SharedPreferences에서
    // 읽고 지움. 웹뷰가 로딩 중일 수도 있는 onCreate 시점에 바로 JS를 실행하는
    // 대신(타이밍이 불안정함), 위젯 1의 "임시 우편함"과 같은 방식으로 SharedPreferences에
    // 저장해두고 JS쪽(syncWidgetNavTarget)이 앱 시작/포그라운드 복귀 시 읽어가게 함.
    static final String EXTRA_WIDGET_NAV = "widget_nav";
    static final String EXTRA_WIDGET_NAV_MONTH = "widget_nav_month";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(WidgetBridgePlugin.class);
        super.onCreate(savedInstanceState);
        // 개발 중 계속 새 APK를 설치해서 테스트하는데, 안드로이드는 APK를 새로
        // 설치해도 앱 데이터/캐시는 그대로 유지해서 웹뷰가 예전 index.html을
        // 계속 캐시해서 보여주는 문제가 있었음(위젯 데이터 형식이 계속 옛날
        // 방식으로 오던 원인이 이것). 캐시를 아예 안 쓰게 해서 항상 방금 설치한
        // 최신 코드로 실행되게 함.
        this.bridge.getWebView().getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        savePendingNavTarget(getIntent());
    }

    // launchMode="singleTask"라 앱이 이미 떠 있을 때 위젯을 또 누르면 onCreate가
    // 아니라 여기로 옴 — 그래서 여기서도 똑같이 저장해줘야 함.
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        savePendingNavTarget(intent);
    }

    private void savePendingNavTarget(Intent intent) {
        if (intent == null) return;
        String target = intent.getStringExtra(EXTRA_WIDGET_NAV);
        if (target == null) return;

        try {
            JSONObject obj = new JSONObject();
            obj.put("target", target);
            String month = intent.getStringExtra(EXTRA_WIDGET_NAV_MONTH);
            if (month != null) obj.put("month", month);

            SharedPreferences prefs = getSharedPreferences(WidgetBridgePlugin.PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(WidgetBridgePlugin.KEY_PENDING_NAV_TARGET, obj.toString()).apply();
        } catch (JSONException e) {
            // 무시 — 이번 한 번은 원래 화면으로 열리고 다음 위젯 탭부터 정상 동작
        }

        // 같은 인텐트를 나중에 또 처리하지 않도록 extra를 비워둠(예: 화면 회전으로
        // Activity가 재생성될 때 getIntent()가 같은 값을 또 들고 있는 것 방지).
        intent.removeExtra(EXTRA_WIDGET_NAV);
        intent.removeExtra(EXTRA_WIDGET_NAV_MONTH);
    }
}
