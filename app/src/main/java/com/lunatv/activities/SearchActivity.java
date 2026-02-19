package com.lunatv.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lunatv.LunaTVApp;
import com.lunatv.R;
import com.lunatv.adapters.CardPresenter;
import com.lunatv.adapters.SearchHistoryAdapter;
import com.lunatv.api.LunaTVApi;
import com.lunatv.models.Video;
import com.lunatv.utils.Preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchActivity extends Activity {
    private EditText etSearch;
    private ImageButton btnBack;
    private Button btnClearHistory;
    private RecyclerView rvSearchHistory;
    private RecyclerView rvSearchResults;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    
    private SearchHistoryAdapter historyAdapter;
    private CardPresenter cardPresenter;
    private List<String> searchHistory = new ArrayList<>();
    private List<Video> searchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        
        initViews();
        loadSearchHistory();
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        btnBack = findViewById(R.id.btn_back);
        btnClearHistory = findViewById(R.id.btn_clear_history);
        rvSearchHistory = findViewById(R.id.rv_search_history);
        rvSearchResults = findViewById(R.id.rv_search_results);
        tvEmpty = findViewById(R.id.tv_empty);
        progressBar = findViewById(R.id.progress_bar);
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        btnClearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSearchHistory();
            }
        });
        
        // 搜索输入框
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(etSearch.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        
        // 设置搜索历史列表
        rvSearchHistory.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new SearchHistoryAdapter(searchHistory, new SearchHistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onHistoryClick(String keyword) {
                etSearch.setText(keyword);
                performSearch(keyword);
            }
            
            @Override
            public void onHistoryDelete(String keyword) {
                removeFromHistory(keyword);
            }
        });
        rvSearchHistory.setAdapter(historyAdapter);
        
        // 设置搜索结果列表
        rvSearchResults.setLayoutManager(new GridLayoutManager(this, 4));
        cardPresenter = new CardPresenter();
    }

    private void loadSearchHistory() {
        Set<String> history = Preferences.getSearchHistory();
        searchHistory.clear();
        searchHistory.addAll(history);
        historyAdapter.notifyDataSetChanged();
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 保存搜索历史
        Preferences.addSearchHistory(query);
        loadSearchHistory();
        
        // 显示加载状态
        progressBar.setVisibility(View.VISIBLE);
        rvSearchResults.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        rvSearchHistory.setVisibility(View.GONE);
        btnClearHistory.setVisibility(View.GONE);
        
        // 执行搜索
        LunaTVApi api = LunaTVApp.getInstance().getApiClient();
        api.search(query, new LunaTVApi.ApiCallback<List<Video>>() {
            @Override
            public void onSuccess(List<Video> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        searchResults.clear();
                        
                        if (result != null && !result.isEmpty()) {
                            searchResults.addAll(result);
                            showSearchResults();
                        } else {
                            showEmptyView();
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SearchActivity.this, "搜索失败: " + message, Toast.LENGTH_SHORT).show();
                        showSearchHistory();
                    }
                });
            }
        });
    }

    private void showSearchResults() {
        rvSearchResults.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        rvSearchHistory.setVisibility(View.GONE);
        btnClearHistory.setVisibility(View.GONE);
        
        // 创建卡片适配器
        android.support.v17.leanback.widget.ArrayObjectAdapter resultsAdapter = 
            new android.support.v17.leanback.widget.ArrayObjectAdapter(cardPresenter);
        for (Video video : searchResults) {
            resultsAdapter.add(video);
        }
        
        // 使用 Leanback 的 HorizontalGridView
        // 这里简化为使用普通的 RecyclerView 配合 CardPresenter
        SearchResultAdapter adapter = new SearchResultAdapter(searchResults, new SearchResultAdapter.OnVideoClickListener() {
            @Override
            public void onVideoClick(Video video) {
                Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_VIDEO, video);
                startActivity(intent);
            }
        });
        rvSearchResults.setAdapter(adapter);
    }

    private void showEmptyView() {
        rvSearchResults.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.VISIBLE);
        rvSearchHistory.setVisibility(View.GONE);
        btnClearHistory.setVisibility(View.GONE);
    }

    private void showSearchHistory() {
        rvSearchResults.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);
        rvSearchHistory.setVisibility(View.VISIBLE);
        btnClearHistory.setVisibility(View.VISIBLE);
    }

    private void clearSearchHistory() {
        Preferences.clearSearchHistory();
        searchHistory.clear();
        historyAdapter.notifyDataSetChanged();
    }

    private void removeFromHistory(String keyword) {
        // 这里需要修改 Preferences 类来支持删除单个历史记录
        // 暂时重新保存所有历史记录（除了被删除的）
        Set<String> history = Preferences.getSearchHistory();
        history.remove(keyword);
        Preferences.clearSearchHistory();
        for (String item : history) {
            Preferences.addSearchHistory(item);
        }
        loadSearchHistory();
    }
    
    // 搜索结果适配器
    private static class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
        private List<Video> videos;
        private OnVideoClickListener listener;
        
        interface OnVideoClickListener {
            void onVideoClick(Video video);
        }
        
        SearchResultAdapter(List<Video> videos, OnVideoClickListener listener) {
            this.videos = videos;
            this.listener = listener;
        }
        
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video_card, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Video video = videos.get(position);
            // 绑定数据...
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVideoClick(video);
                }
            });
        }
        
        @Override
        public int getItemCount() {
            return videos.size();
        }
        
        static class ViewHolder extends RecyclerView.ViewHolder {
            ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}