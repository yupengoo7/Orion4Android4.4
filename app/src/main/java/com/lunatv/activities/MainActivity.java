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
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridView;
import android.util.Log;
import android.view.View;

import com.lunatv.LunaTVApp;
import com.lunatv.R;
import com.lunatv.adapters.CardPresenter;
import com.lunatv.api.LunaTVApi;
import com.lunatv.models.Favorite;
import com.lunatv.models.PlayRecord;
import com.lunatv.models.Video;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    
    private VerticalGridView verticalGrid;
    private ArrayObjectAdapter rowsAdapter;
    private CardPresenter cardPresenter;
    
    private List<Video> recommendedVideos = new ArrayList<>();
    private List<PlayRecord> recentRecords = new ArrayList<>();
    private List<Favorite> favorites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupAdapter();
        loadData();
    }

    private void initViews() {
        verticalGrid = findViewById(R.id.vertical_grid);
    }

    private void setupAdapter() {
        rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM, false));
        cardPresenter = new CardPresenter();
        
        ItemBridgeAdapter bridgeAdapter = new ItemBridgeAdapter(rowsAdapter);
        verticalGrid.setAdapter(bridgeAdapter);
    }

    private void loadData() {
        // 加载推荐视频（可以先放一些示例数据或从服务器获取）
        loadRecommended();
        
        // 加载最近观看
        loadRecentRecords();
        
        // 加载收藏
        loadFavorites();
    }

    private void loadRecommended() {
        // 这里可以从服务器获取推荐视频
        // 暂时使用搜索 API 获取一些热门内容
        LunaTVApi api = LunaTVApp.getInstance().getApiClient();
        if (api != null) {
            api.search("2024", new LunaTVApi.ApiCallback<List<Video>>() {
                @Override
                public void onSuccess(List<Video> result) {
                    if (result != null && !result.isEmpty()) {
                        recommendedVideos = result.subList(0, Math.min(10, result.size()));
                        updateUI();
                    }
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "Failed to load recommended: " + message);
                }
            });
        }
    }

    private void loadRecentRecords() {
        LunaTVApi api = LunaTVApp.getInstance().getApiClient();
        if (api != null) {
            api.getPlayRecords(new LunaTVApi.ApiCallback<Map<String, PlayRecord>>() {
                @Override
                public void onSuccess(Map<String, PlayRecord> result) {
                    if (result != null) {
                        recentRecords = new ArrayList<>(result.values());
                        updateUI();
                    }
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "Failed to load recent records: " + message);
                }
            });
        }
    }

    private void loadFavorites() {
        LunaTVApi api = LunaTVApp.getInstance().getApiClient();
        if (api != null) {
            api.getFavorites(new LunaTVApi.ApiCallback<Map<String, Favorite>>() {
                @Override
                public void onSuccess(Map<String, Favorite> result) {
                    if (result != null) {
                        favorites = new ArrayList<>(result.values());
                        updateUI();
                    }
                }

                @Override
                public void onError(String message) {
                    Log.e(TAG, "Failed to load favorites: " + message);
                }
            });
        }
    }

    private void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rowsAdapter.clear();

                // 添加推荐行
                if (!recommendedVideos.isEmpty()) {
                    HeaderItem header = new HeaderItem(0, getString(R.string.header_recommended));
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                    for (Video video : recommendedVideos) {
                        listRowAdapter.add(video);
                    }
                    rowsAdapter.add(new ListRow(header, listRowAdapter));
                }

                // 添加最近观看行
                if (!recentRecords.isEmpty()) {
                    HeaderItem header = new HeaderItem(1, getString(R.string.header_recent));
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                    for (PlayRecord record : recentRecords) {
                        Video video = recordToVideo(record);
                        listRowAdapter.add(video);
                    }
                    rowsAdapter.add(new ListRow(header, listRowAdapter));
                }

                // 添加收藏行
                if (!favorites.isEmpty()) {
                    HeaderItem header = new HeaderItem(2, getString(R.string.header_favorites));
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
                    for (Favorite favorite : favorites) {
                        Video video = favoriteToVideo(favorite);
                        listRowAdapter.add(video);
                    }
                    rowsAdapter.add(new ListRow(header, listRowAdapter));
                }

                rowsAdapter.notifyArrayItemRangeChanged(0, rowsAdapter.size());
            }
        });
    }

    private Video recordToVideo(PlayRecord record) {
        Video video = new Video();
        video.setTitle(record.getTitle());
        video.setPoster(record.getCover());
        video.setSource(record.getSource());
        video.setSourceName(record.getSourceName());
        video.setYear(record.getYear());
        return video;
    }

    private Video favoriteToVideo(Favorite favorite) {
        Video video = new Video();
        video.setTitle(favorite.getTitle());
        video.setPoster(favorite.getCover());
        video.setSource(favorite.getSource());
        video.setSourceName(favorite.getSourceName());
        video.setYear(favorite.getYear());
        return video;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 重新加载数据以更新最近观看和收藏
        loadData();
    }
}