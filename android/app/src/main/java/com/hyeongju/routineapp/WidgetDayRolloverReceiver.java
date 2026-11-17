package com.hyeongju.routineapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// 자정 알람, 시간·시간대 변경, 재부팅, 앱 업데이트 때 위젯을 새 날짜로 넘김
// (WidgetDayRollover 참고, 2026-11-17 추가).
public class WidgetDayRolloverReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WidgetDayRollover.rollover(context);
    }
}
