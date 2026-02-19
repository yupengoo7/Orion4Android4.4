package com.lunatv.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import android.content.Intent;

import com.lunatv.R;
import com.lunatv.utils.Preferences;

public class SettingsActivity extends Activity {
    private ImageButton btnBack;
    private CheckBox cbSkipIntro;
    private CheckBox cbSkipOutro;
    private SeekBar seekIntroTime;
    private SeekBar seekOutroTime;
    private TextView tvIntroTime;
    private TextView tvOutroTime;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initViews();
        loadSettings();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        cbSkipIntro = findViewById(R.id.cb_skip_intro);
        cbSkipOutro = findViewById(R.id.cb_skip_outro);
        seekIntroTime = findViewById(R.id.seek_intro_time);
        seekOutroTime = findViewById(R.id.seek_outro_time);
        tvIntroTime = findViewById(R.id.tv_intro_time);
        tvOutroTime = findViewById(R.id.tv_outro_time);
        btnLogout = findViewById(R.id.btn_logout);
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // 片头跳过开关
        cbSkipIntro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Preferences.setSkipIntro(isChecked);
            seekIntroTime.setEnabled(isChecked);
        });
        
        // 片尾跳过开关
        cbSkipOutro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Preferences.setSkipOutro(isChecked);
            seekOutroTime.setEnabled(isChecked);
        });
        
        // 片头时间选择
        seekIntroTime.setMax(300); // 最大 5 分钟
        seekIntroTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Preferences.setIntroTime(progress);
                    tvIntroTime.setText(progress + " 秒");
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // 片尾时间选择
        seekOutroTime.setMax(300); // 最大 5 分钟
        seekOutroTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Preferences.setOutroTime(progress);
                    tvOutroTime.setText(progress + " 秒");
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // 退出登录
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutConfirmDialog();
            }
        });
    }

    private void loadSettings() {
        // 片头设置
        boolean skipIntro = Preferences.isSkipIntro();
        cbSkipIntro.setChecked(skipIntro);
        seekIntroTime.setEnabled(skipIntro);
        int introTime = Preferences.getIntroTime();
        seekIntroTime.setProgress(introTime);
        tvIntroTime.setText(introTime + " 秒");
        
        // 片尾设置
        boolean skipOutro = Preferences.isSkipOutro();
        cbSkipOutro.setChecked(skipOutro);
        seekOutroTime.setEnabled(skipOutro);
        int outroTime = Preferences.getOutroTime();
        seekOutroTime.setProgress(outroTime);
        tvOutroTime.setText(outroTime + " 秒");
    }

    private void showLogoutConfirmDialog() {
        new AlertDialog.Builder(this)
            .setTitle("退出登录")
            .setMessage("确定要退出登录吗？")
            .setPositiveButton("确定", (dialog, which) -> {
                // 清除所有数据
                Preferences.clearAll();
                
                // 返回登录页
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                
                Toast.makeText(this, "已退出登录", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("取消", null)
            .show();
    }
}