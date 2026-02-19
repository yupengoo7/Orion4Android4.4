package com.lunatv.activities;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.lunatv.LunaTVApp;
import com.lunatv.R;
import com.lunatv.api.LunaTVApi;
import com.lunatv.models.PlayRecord;
import com.lunatv.models.Video;
import com.lunatv.utils.Preferences;

import java.util.ArrayList;
import java.util.List;

public class PlayerActivity extends Activity {
    private static final String TAG = "PlayerActivity";
    public static final String EXTRA_VIDEO = "extra_video";
    public static final String EXTRA_EPISODE_INDEX = "extra_episode_index";
    public static final String EXTRA_EPISODES = "extra_episodes";
    
    private static final int CONTROLS_TIMEOUT = 5000; // 控制栏自动隐藏时间
    
    private VideoView videoView;
    private ProgressBar progressLoading;
    private TextView tvError;
    private LinearLayout controlsContainer;
    private SeekBar seekBar;
    private ImageButton btnPlayPause;
    private TextView tvCurrentTime, tvTotalTime, tvTitle;
    private Button btnSkipIntro, btnSkipOutro;
    
    private Video video;
    private List<String> episodes;
    private int currentEpisodeIndex;
    private int currentPosition = 0;
    private int totalDuration = 0;
    private boolean isPlaying = false;
    private boolean isControlsVisible = false;
    private Handler handler = new Handler();
    
    // 片头片尾跳过
    private int introEndTime;
    private int outroStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        
        video = (Video) getIntent().getSerializableExtra(EXTRA_VIDEO);
        currentEpisodeIndex = getIntent().getIntExtra(EXTRA_EPISODE_INDEX, 0);
        episodes = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_EPISODES);
        
        if (video == null || episodes == null || episodes.isEmpty()) {
            Toast.makeText(this, "播放数据错误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // 获取片头片尾跳过设置
        introEndTime = Preferences.isSkipIntro() ? Preferences.getIntroTime() : 0;
        outroStartTime = Preferences.isSkipOutro() ? Preferences.getOutroTime() : 0;
        
        initViews();
        setupVideoView();
        loadLastPosition();
        playEpisode(currentEpisodeIndex);
    }

    private void initViews() {
        videoView = findViewById(R.id.video_view);
        progressLoading = findViewById(R.id.progress_loading);
        tvError = findViewById(R.id.tv_error);
        controlsContainer = findViewById(R.id.controls_container);
        seekBar = findViewById(R.id.seek_bar);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        tvTitle = findViewById(R.id.tv_title);
        btnSkipIntro = findViewById(R.id.btn_skip_intro);
        btnSkipOutro = findViewById(R.id.btn_skip_outro);
        
        // 播放/暂停按钮
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });
        
        // 进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    int newPosition = (int) ((progress / 100.0) * totalDuration);
                    videoView.seekTo(newPosition);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // 跳过片头按钮
        btnSkipIntro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (introEndTime > 0) {
                    videoView.seekTo(introEndTime * 1000);
                    btnSkipIntro.setVisibility(View.GONE);
                }
            }
        });
        
        // 跳过片尾按钮
        btnSkipOutro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outroStartTime > 0 && totalDuration > 0) {
                    videoView.seekTo(totalDuration - (outroStartTime * 1000));
                    btnSkipOutro.setVisibility(View.GONE);
                }
            }
        });
        
        // 点击视频区域显示/隐藏控制栏
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleControls();
            }
        });
    }

    private void setupVideoView() {
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                totalDuration = videoView.getDuration();
                tvTotalTime.setText(formatTime(totalDuration));
                
                // 恢复上次播放位置
                if (currentPosition > 0) {
                    videoView.seekTo(currentPosition);
                }
                
                // 如果是片头跳过时间范围内，显示跳过按钮
                if (currentPosition < introEndTime * 1000 && introEndTime > 0) {
                    btnSkipIntro.setVisibility(View.VISIBLE);
                }
                
                videoView.start();
                isPlaying = true;
                btnPlayPause.setImageResource(R.drawable.ic_pause);
                progressLoading.setVisibility(View.GONE);
                
                // 开始更新进度
                startProgressUpdate();
            }
        });
        
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                savePlayRecord();
                // 播放下一集
                if (currentEpisodeIndex < episodes.size() - 1) {
                    currentEpisodeIndex++;
                    currentPosition = 0;
                    playEpisode(currentEpisodeIndex);
                } else {
                    finish();
                }
            }
        });
        
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "Video error: what=" + what + ", extra=" + extra);
                progressLoading.setVisibility(View.GONE);
                tvError.setVisibility(View.VISIBLE);
                tvError.setText("播放出错，请重试");
                return true;
            }
        });
    }

    private void playEpisode(int index) {
        if (index < 0 || index >= episodes.size()) {
            return;
        }
        
        currentEpisodeIndex = index;
        String url = episodes.get(index);
        
        progressLoading.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);
        
        videoView.setVideoURI(Uri.parse(url));
        tvTitle.setText(video.getTitle() + " - 第" + (index + 1) + "集");
        
        showControls();
    }

    private void loadLastPosition() {
        // 从服务器获取上次播放位置
        LunaTVApi api = LunaTVApp.getInstance().getApiClient();
        String key = video.getSource() + "+" + video.getId();
        api.getPlayRecords(new LunaTVApi.ApiCallback<java.util.Map<String, PlayRecord>>() {
            @Override
            public void onSuccess(java.util.Map<String, PlayRecord> result) {
                if (result != null && result.containsKey(key)) {
                    PlayRecord record = result.get(key);
                    if (record.getIndex() == currentEpisodeIndex) {
                        currentPosition = record.getPlayTime() * 1000;
                    }
                }
            }

            @Override
            public void onError(String message) {
                // 忽略错误
            }
        });
    }

    private void savePlayRecord() {
        currentPosition = videoView.getCurrentPosition();
        
        PlayRecord record = new PlayRecord();
        record.setTitle(video.getTitle());
        record.setCover(video.getPoster());
        record.setSource(video.getSource());
        record.setSourceName(video.getSourceName());
        record.setVideoId(video.getId());
        record.setIndex(currentEpisodeIndex);
        record.setTotalEpisodes(episodes.size());
        record.setPlayTime(currentPosition / 1000);
        record.setTotalTime(totalDuration / 1000);
        record.setSaveTime(System.currentTimeMillis());
        record.setYear(video.getYear());
        record.setSearchTitle(video.getTitle());
        
        LunaTVApi api = LunaTVApp.getInstance().getApiClient();
        String key = video.getSource() + "+" + video.getId();
        api.savePlayRecord(key, record, new LunaTVApi.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.d(TAG, "Play record saved");
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Failed to save play record: " + message);
            }
        });
    }

    private void togglePlayPause() {
        if (isPlaying) {
            videoView.pause();
            btnPlayPause.setImageResource(R.drawable.ic_play);
        } else {
            videoView.start();
            btnPlayPause.setImageResource(R.drawable.ic_pause);
        }
        isPlaying = !isPlaying;
        resetControlsTimer();
    }

    private void toggleControls() {
        if (isControlsVisible) {
            hideControls();
        } else {
            showControls();
        }
    }

    private void showControls() {
        controlsContainer.setVisibility(View.VISIBLE);
        isControlsVisible = true;
        resetControlsTimer();
    }

    private void hideControls() {
        controlsContainer.setVisibility(View.GONE);
        isControlsVisible = false;
    }

    private void resetControlsTimer() {
        handler.removeCallbacks(hideControlsRunnable);
        handler.postDelayed(hideControlsRunnable, CONTROLS_TIMEOUT);
    }

    private Runnable hideControlsRunnable = new Runnable() {
        @Override
        public void run() {
            hideControls();
        }
    };

    private void startProgressUpdate() {
        handler.post(updateProgressRunnable);
    }

    private Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (videoView != null && isPlaying) {
                int current = videoView.getCurrentPosition();
                currentPosition = current;
                
                // 更新时间显示
                tvCurrentTime.setText(formatTime(current));
                
                // 更新进度条
                if (totalDuration > 0) {
                    int progress = (int) ((current * 100.0) / totalDuration);
                    seekBar.setProgress(progress);
                }
                
                // 检查是否需要显示片头跳过按钮
                if (introEndTime > 0 && current < introEndTime * 1000) {
                    btnSkipIntro.setVisibility(View.VISIBLE);
                } else {
                    btnSkipIntro.setVisibility(View.GONE);
                }
                
                // 检查是否需要显示片尾跳过按钮
                if (outroStartTime > 0 && totalDuration > 0 && 
                    current > totalDuration - (outroStartTime * 1000)) {
                    btnSkipOutro.setVisibility(View.VISIBLE);
                } else {
                    btnSkipOutro.setVisibility(View.GONE);
                }
                
                handler.postDelayed(this, 1000);
            }
        }
    };

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                togglePlayPause();
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                // 快退 10 秒
                if (videoView != null) {
                    int newPos = videoView.getCurrentPosition() - 10000;
                    if (newPos < 0) newPos = 0;
                    videoView.seekTo(newPos);
                    showControls();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                // 快进 10 秒
                if (videoView != null) {
                    int newPos = videoView.getCurrentPosition() + 10000;
                    if (newPos > totalDuration) newPos = totalDuration;
                    videoView.seekTo(newPos);
                    showControls();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_MENU:
                toggleControls();
                return true;
            case KeyEvent.KEYCODE_BACK:
                if (isControlsVisible) {
                    hideControls();
                    return true;
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        savePlayRecord();
        if (videoView != null && isPlaying) {
            videoView.pause();
        }
        handler.removeCallbacks(updateProgressRunnable);
        handler.removeCallbacks(hideControlsRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null && !isPlaying) {
            videoView.start();
            isPlaying = true;
            startProgressUpdate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        savePlayRecord();
        handler.removeCallbacksAndMessages(null);
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}