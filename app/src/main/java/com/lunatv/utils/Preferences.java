package com.lunatv.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class Preferences {
    private static final String PREF_NAME = "LunaTVPrefs";
    private static final String KEY_SERVER_URL = "server_url";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_AUTH_COOKIE = "auth_cookie";
    private static final String KEY_SKIP_INTRO = "skip_intro";
    private static final String KEY_SKIP_OUTRO = "skip_outro";
    private static final String KEY_INTRO_TIME = "intro_time";
    private static final String KEY_OUTRO_TIME = "outro_time";
    private static final String KEY_SEARCH_HISTORY = "search_history";

    private static SharedPreferences prefs;

    public static void init(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // 服务器地址
    public static void setServerUrl(String url) {
        prefs.edit().putString(KEY_SERVER_URL, url).apply();
    }

    public static String getServerUrl() {
        return prefs.getString(KEY_SERVER_URL, "");
    }

    // 用户名
    public static void setUsername(String username) {
        prefs.edit().putString(KEY_USERNAME, username).apply();
    }

    public static String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }

    // 认证 Cookie
    public static void setAuthCookie(String cookie) {
        prefs.edit().putString(KEY_AUTH_COOKIE, cookie).apply();
    }

    public static String getAuthCookie() {
        return prefs.getString(KEY_AUTH_COOKIE, "");
    }

    // 是否跳过片头
    public static void setSkipIntro(boolean skip) {
        prefs.edit().putBoolean(KEY_SKIP_INTRO, skip).apply();
    }

    public static boolean isSkipIntro() {
        return prefs.getBoolean(KEY_SKIP_INTRO, true);
    }

    // 是否跳过片尾
    public static void setSkipOutro(boolean skip) {
        prefs.edit().putBoolean(KEY_SKIP_OUTRO, skip).apply();
    }

    public static boolean isSkipOutro() {
        return prefs.getBoolean(KEY_SKIP_OUTRO, true);
    }

    // 片头跳过时间（秒）
    public static void setIntroTime(int seconds) {
        prefs.edit().putInt(KEY_INTRO_TIME, seconds).apply();
    }

    public static int getIntroTime() {
        return prefs.getInt(KEY_INTRO_TIME, 90); // 默认 90 秒
    }

    // 片尾跳过时间（秒）
    public static void setOutroTime(int seconds) {
        prefs.edit().putInt(KEY_OUTRO_TIME, seconds).apply();
    }

    public static int getOutroTime() {
        return prefs.getInt(KEY_OUTRO_TIME, 120); // 默认 120 秒
    }

    // 搜索历史
    public static void addSearchHistory(String keyword) {
        Set<String> history = getSearchHistory();
        history.remove(keyword); // 移除已存在的
        history.add(keyword);
        
        // 限制历史记录数量
        if (history.size() > 20) {
            String[] array = history.toArray(new String[0]);
            history.clear();
            for (int i = array.length - 20; i < array.length; i++) {
                history.add(array[i]);
            }
        }
        
        prefs.edit().putStringSet(KEY_SEARCH_HISTORY, history).apply();
    }

    public static Set<String> getSearchHistory() {
        return new HashSet<>(prefs.getStringSet(KEY_SEARCH_HISTORY, new HashSet<String>()));
    }

    public static void clearSearchHistory() {
        prefs.edit().remove(KEY_SEARCH_HISTORY).apply();
    }

    // 清除所有数据
    public static void clearAll() {
        prefs.edit().clear().apply();
    }
}