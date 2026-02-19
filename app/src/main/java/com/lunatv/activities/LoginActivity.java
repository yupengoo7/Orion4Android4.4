package com.lunatv.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lunatv.LunaTVApp;
import com.lunatv.R;
import com.lunatv.api.LunaTVApi;
import com.lunatv.utils.Preferences;

public class LoginActivity extends Activity {
    private EditText etServerUrl;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        loadSavedCredentials();
    }

    private void initViews() {
        etServerUrl = findViewById(R.id.et_server_url);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void loadSavedCredentials() {
        String savedUrl = Preferences.getServerUrl();
        String savedUsername = Preferences.getUsername();
        
        if (savedUrl != null && !savedUrl.isEmpty()) {
            etServerUrl.setText(savedUrl);
        }
        if (savedUsername != null && !savedUsername.isEmpty()) {
            etUsername.setText(savedUsername);
        }
        
        // 如果有保存的凭证，尝试自动登录
        if (!savedUrl.isEmpty() && !savedUsername.isEmpty()) {
            etPassword.requestFocus();
        }
    }

    private void attemptLogin() {
        String serverUrl = etServerUrl.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 验证输入
        if (serverUrl.isEmpty()) {
            etServerUrl.setError(getString(R.string.error_empty_url));
            etServerUrl.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError(getString(R.string.error_empty_password));
            etPassword.requestFocus();
            return;
        }

        showLoading(true);

        // 保存配置
        Preferences.setServerUrl(serverUrl);
        Preferences.setUsername(username);
        
        // 初始化 API 客户端
        LunaTVApp.getInstance().setApiClient(serverUrl);

        // 登录
        LunaTVApi api = LunaTVApp.getInstance().getApiClient();
        api.login(username, password, new LunaTVApi.ApiCallback<LunaTVApi.LoginResponse>() {
            @Override
            public void onSuccess(LunaTVApi.LoginResponse result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                        Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        
                        // 跳转到主页面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }

            @Override
            public void onError(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading(false);
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        etServerUrl.setEnabled(!show);
        etUsername.setEnabled(!show);
        etPassword.setEnabled(!show);
    }
}