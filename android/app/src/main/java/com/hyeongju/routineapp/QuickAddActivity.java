package com.hyeongju.routineapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class QuickAddActivity extends Activity {
    public static final String PREFS_NAME = "widget_bridge";
    public static final String KEY_PENDING_ITEMS = "pending_inbox_items";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_add);

        final EditText input = findViewById(R.id.quick_add_input);
        Button confirmBtn = findViewById(R.id.quick_add_confirm);

        input.requestFocus();
        // windowSoftInputMode="stateAlwaysVisible"(매니페스트)만으로는 기종에 따라
        // 키보드가 안 뜨는 경우가 있어서, 뷰가 실제로 붙은 뒤 명시적으로 한 번 더 요청.
        input.post(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
        });

        confirmBtn.setOnClickListener(v -> save(input));
        input.setOnEditorActionListener((v, actionId, event) -> {
            boolean isEnterDown = event != null
                && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                && event.getAction() == KeyEvent.ACTION_DOWN;
            if (actionId == EditorInfo.IME_ACTION_DONE || isEnterDown) {
                save(input);
                return true;
            }
            return false;
        });
    }

    private void save(EditText input) {
        String text = input.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            finish();
            return;
        }
        addPendingItem(text);
        Toast.makeText(this, "미배치에 추가됐어요", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void addPendingItem(String text) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String raw = prefs.getString(KEY_PENDING_ITEMS, "[]");
        JSONArray arr;
        try {
            arr = new JSONArray(raw);
        } catch (JSONException e) {
            arr = new JSONArray();
        }
        arr.put(text);
        prefs.edit().putString(KEY_PENDING_ITEMS, arr.toString()).apply();
    }
}
