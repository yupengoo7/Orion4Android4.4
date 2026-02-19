package com.lunatv.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ItemBridgeAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.VerticalGridView;
import android.util.Log;
import android.widget.Toast;

import com.lunatv.LunaTVApp;
import com.lunatv.R;
import com.lunatv.adapters.CardPresenter;
import com.lunatv.api.LunaTVApi;
import com.lunatv.models.Favorite;
import com.lunatv.models.PlayRecord;
import com.lunatv.models.Video;
import com.lunatv.utils.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    
    private VerticalGridView verticalGrid;
    private ArrayObjectAdapter rowsAdapter;
    private CardPresenter cardPresenter;
    private ItemBridgeAdapter bridgeAdapter;
    
    private List<Video> recommendedVideos = new ArrayList<>();
    private List<PlayRecord> recentRecords = new ArrayList<>();
    private List<Favorite> favorites = new ArrayList<>();
    
    private boolean isActivityActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 检查是否已登录
        if (!checkLogin()) {
            return;
        }
        
        setContentView(R.layout.activity_main);
        isActivityActive = true;
        
        try {
            initViews();
            setupAdapter();
            loadData();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            showToast("初始化失败: " + e.getMessage());
        }
    }
    
    private boolean checkLogin() {
        String savedUrl = Preferences.getServerUrl();
        String savedCookie = Preferences.getAuthCookie();
        
        if (savedUrl.isEmpty() || savedCookie.isEmpty()) {
            // 未登录，返回登录页面
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
        
        // 确保 API 客户端已初始化
        if (LunaTVApp.getInstance().getApiClient() == null) {
            LunaTVApp.getInstance().setApiClient(savedUrl);
        }
        
        return true;
    }

    private void initViews() {
        verticalGrid = findViewById(R.id.vertical_grid);
    }

    private void setupAdapter() {
        rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM, false));
        cardPresenter = new CardPresenter();
        
        bridgeAdapter = new ItemBridgeAdapter(rowsAdapter);
        verticalGrid.setAdapter(bridgeAdapter);
    }

    private void loadData() {
        loadRecommended();
        loadRecentRecords();
        loadFavorites();
    }

    private void loadRecommended() {
        LunaTVApi api = LunaTVApp.getInstance().getApiClient();
        if (api == null) {
            Log.e(TAG, "API client is null");
            return;
        }
        
        api.search("2024", new LunaTVApi.ApiCallback<List<Video>>() {
            @Override
            public void onSuccess(List<Video> result) {
                if (!isActivityActive) return;
                
                try {
                    if (result != null && !result.isEmpty()) {
                        // 创建副本，避免 subList 视图问题
                        int count = Math.min(10, result.size());
                        recommendedVideos = new ArrayList<>(result.subList(0, count));
                        updateUI();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing recommended videos", e);
                }
            }

            @Override
            public void onError(String message) {
                if (!isActivityActive) return;
                Log.e(TAG, "Failed to load recommended: " + message);
            }
        });
    }

    private void loadRecentRecords() {
        LunaTVApi api = LunaTVApp.getInstance().getApiClient();
        if (api == null) {
            Log.e(TAG, "API client is null");
            return;
        }
        
        api.getPlayRecords(new LunaTVApi.ApiCallback<Map<String, PlayRecord>>() {
            @Override
            public void onSuccess(Map<String, PlayRecord> result) {
                if (!isActivityActive) return;
                
                try {
                    if (result != null && !result.isEmpty()) {
                        recentRecords = new ArrayList<>(result.values());
                        updateUI();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing recent records", e);
                }
            }

            @Override
            public void onError(String message) {
                if (!isActivityActive) return;
                Log.e(TAG, "Failed to load recent records: " + message);
            }
        });
    }

    private void loadFavorites() {
        LunaTVApi api = LunaTVApp.getInstance().getApiClient();
        if (api == null) {
            Log.e(TAG, "API client is null");
            return;
        }
        
        api.getFavorites(new LunaTVApi.ApiCallback<Map<String, Favorite>>() {
            @Override
            public void onSuccess(Map<String, Favorite> result) {
                if (!isActivityActive) return;
                
                try {
                    if (result != null && !result.isEmpty()) {
                        favorites = new ArrayList<>(result.values());
                        updateUI();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error processing favorites", e);
                }
            }

            @Override
            public void onError(String message) {
                if (!isActivityActive) return;
                Log.e(TAG, "Failed to load favorites: " + message);
            }
        });
    }

    private void updateUI() {
        if (!isActivityActive || rowsAdapter == null) return;
        
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isActivityActive || rowsAdapter == null) return;
                
                try {
                    rowsAdapter.clear();

                    // 添加推荐行
                    if (recommendedVideos != null && !recommendedVideos.isEmpty()) {
                        HeaderItem header = new HeaderItem(0, getString(R.string.header_recommended));
                        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                        for (Video video : recommendedVideos) {
                            if (video != null) {
                                listRowAdapter.add(video);
                            }
                        }
                        if (listRowAdapter.size() > 0) {
                            rowsAdapter.add(new ListRow(header, listRowAdapter));
                        }
                    }

                    // 添加最近观看行
                    if (recentRecords != null && !recentRecords.isEmpty()) {
                        HeaderItem header = new HeaderItem(1, getString(R.string.header_recent));
                        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                        for (PlayRecord record : recentRecords) {
                            if (record != null) {
                                Video video = recordToVideo(record);
                                listRowAdapter.add(video);
                            }
                        }
                        if (listRowAdapter.size() > 0) {
                            rowsAdapter.add(new ListRow(header, listRowAdapter));
                        }
                    }

                    // 添加收藏行
                    if (favorites != null && !favorites.isEmpty()) {
                        HeaderItem header = new HeaderItem(2, getString(R.string.header_favorites));
                        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                        for (Favorite favorite : favorites) {
                            if (favorite != null) {
                                Video video = favoriteToVideo(favorite);
                                listRowAdapter.add(video);
                            }
                        }
                        if (listRowAdapter.size() > 0) {
                            rowsAdapter.add(new ListRow(header, listRowAdapter));
                        }
                    }

                    if (rowsAdapter.size() > 0) {
                        rowsAdapter.notifyArrayItemRangeChanged(0, rowsAdapter.size());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error updating UI", e);
                }
            }
        });
    }

    private Video recordToVideo(PlayRecord record) {
        Video video = new Video();
        video.setTitle(record.getTitle() != null ? record.getTitle() : "");
        video.setPoster(record.getCover());
        video.setSource(record.getSource() != null ? record.getSource() : "");
        video.setSourceName(record.getSourceName());
        video.setYear(record.getYear());
        return video;
    }

    private Video favoriteToVideo(Favorite favorite) {
        Video video = new Video();
        video.setTitle(favorite.getTitle() != null ? favorite.getTitle() : "");
        video.setPoster(favorite.getCover());
        video.setSource(favorite.getSource() != null ? favorite.getSource() : "");
        video.setSourceName(favorite.getSourceName());
        video.setYear(favorite.getYear());
        return video;
    }
    
    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityActive = true;
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        isActivityActive = false;
    }
    
    @Override
    protected void onDestroy() {
        isActivityActive = false;
        super.onDestroy();
    }
}
