package com.lunatv;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.lunatv.api.LunaTVApi;
import com.lunatv.utils.Preferences;

public class LunaTVApp extends Application {
    private static final String TAG = "LunaTVApp";
    private static LunaTVApp instance;
    private LunaTVApi apiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        // 初始化 Preferences
        Preferences.init(this);
        
        // 在 Android 4.4 上启用 TLS 1.2
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            enableTLS12();
        }
        
        // 初始化 API 客户端
        initApiClient();
    }

    public static LunaTVApp getInstance() {
        return instance;
    }

    private void enableTLS12() {
        try {
            ProviderInstaller.installIfNeeded(this);
            Log.d(TAG, "TLS 1.2 enabled successfully");
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "Google Play Services repairable exception", e);
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "Google Play Services not available", e);
        }
    }

    private void initApiClient() {
        String baseUrl = Preferences.getServerUrl();
        if (baseUrl != null && !baseUrl.isEmpty()) {
            apiClient = new LunaTVApi(baseUrl);
        }
    }

    public LunaTVApi getApiClient() {
        return apiClient;
    }

    public void setApiClient(String baseUrl) {
        apiClient = new LunaTVApi(baseUrl);
    }
}