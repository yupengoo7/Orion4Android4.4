package com.lunatv.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lunatv.LunaTVApp;
import com.lunatv.R;
import com.lunatv.adapters.EpisodeAdapter;
import com.lunatv.api.LunaTVApi;
import com.lunatv.models.Favorite;
import com.lunatv.models.PlayRecord;
import com.lunatv.models.Video;
import com.lunatv.utils.Preferences;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailActivity extends Activity {
    public static final String EXTRA_VIDEO = "extra_video";
    
    private ImageView ivPoster;
    private TextView tvTitle, tvInfo, tvDirector, tvActor, tvDesc;
    private Button btnPlay, btnFavorite;
    private RecyclerView rvEpisodes;
    private ImageButton btnBack;
    
    private Video video;
    private List<String> episodes = new ArrayList<>();
    private EpisodeAdapter episodeAdapter;
    private boolean isFavorite = false;
    private int selectedEpisode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        
        video = (Video) getIntent().getSerializableExtra(EXTRA_VIDEO);
        if (video == null) {
            finish();
            return;
        }
        
        initViews();
        loadDetail();
        checkFavoriteStatus();
    }

    private void initViews() {
        ivPoster = findViewById(R.id.iv_poster);
        tvTitle = findViewById(R.id.tv_title);
        tvInfo = findViewById(R.id.tv_info);
        tvDirector = findViewById(R.id.tv_director);
        tvActor = findViewById(R.id.tv_actor);
        tvDesc = findViewById(R.id.tv_desc);
        btnPlay = findViewById(R.id.btn_play);
        btnFavorite = findViewById(R.id.btn_favorite);
        rvEpisodes = findViewById(R.id.rv_episodes);
        btnBack = findViewById(R.id.btn_back);
        
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo(selectedEpisode);
            }
        });
        
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavorite();
            }
        });
        
        // 设置选集列表
        rvEpisodes.setLayoutManager(new GridLayoutManager(this, 8));
        episodeAdapter = new EpisodeAdapter(episodes, new EpisodeAdapter.OnEpisodeClickListener() {
            @Override
            public void onEpisodeClick(int position) {
                selectedEpisode = position;
                playVideo(position);
            }
        });
        rvEpisodes.setAdapter(episodeAdapter);
    }

    private void loadDetail() {
        // 显示基本信息
        tvTitle.setText(video.getTitle());
        
        String info = String.format("%s | %s | %s", 
            video.getYear() != null ? video.getYear() : "",
            video.getType() != null ? video.getType() : "",
            video.getSourceName() != null ? video.getSourceName() : "");
        tvInfo.setText(info);
        
        if (video.getDirector() != null) {
            tvDirector.setText(getString(R.string.label_director) + " " + video.getDirector());
        } else {
            tvDirector.setVisibility(View.GONE);
        }
        
        if (video.getActor() != null) {
            tvActor.setText(getString(R.string.label_actor) + " " + video.getActor());
        } else {
            tvActor.setVisibility(View.GONE);
        }
        
        if (video.getDesc() != null) {
            tvDesc.setText(video.getDesc());
        } else {
            tvDesc.setVisibility(View.GONE);
        }
        
        // 加载海报
        if (video.getPoster() != null && !video.getPoster().isEmpty()) {
            Picasso.get()
                .load(video.getPoster())
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .into(ivPoster);
        }
        
        // 从服务器获取详情（包括剧集列表）
        if (video.getSource() != null && video.getId() != null) {
            LunaTVApi api = LunaTVApp.getInstance().getApiClient();
            api.getDetail(video.getSource(), video.getId(), new LunaTVApi.ApiCallback<Video>() {
                @Override
                public void onSuccess(Video result) {
                    if (result != null && result.getEpisodes() != null) {
                        episodes.clear();
                        episodes.addAll(result.getEpisodes());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                episodeAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }

                @Override
                public void onError(String message) {
                    // 如果已经有剧集数据，直接使用
                    if (video.getEpisodes() != null) {
                        episodes.clear();
                        episodes.addAll(video.getEpisodes());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                episodeAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            });
        } else if (video.getEpisodes() != null) {
            episodes.clear();
            episodes.addAll(video.getEpisodes());
            episodeAdapter.notifyDataSetChanged();
        }
    }

    private void checkFavoriteStatus() {
        LunaTVApi api = LunaTVApp.getInstance().getApiClient();
        String key = video.getSource() + "+" + video.getId();
        api.getFavorites(new LunaTVApi.ApiCallback<Map<String, Favorite>>() {
            @Override
            public void onSuccess(Map<String, Favorite> result) {
                if (result != null && result.containsKey(key)) {
                    isFavorite = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnFavorite.setText(R.string.action_unfavorite);
                        }
                    });
                }
            }

            @Override
            public void onError(String message) {
                // 忽略错误
            }
        });
    }

    private void toggleFavorite() {
        LunaTVApi api = LunaTVApp.getInstance().getApiClient();
        String key = video.getSource() + "+" + video.getId();
        
        if (isFavorite) {
            api.removeFavorite(key, new LunaTVApi.ApiCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    isFavorite = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnFavorite.setText(R.string.action_favorite);
                            Toast.makeText(DetailActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DetailActivity.this, "操作失败: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        } else {
            Favorite favorite = new Favorite();
            favorite.setTitle(video.getTitle());
            favorite.setCover(video.getPoster());
            favorite.setSource(video.getSource());
            favorite.setSourceName(video.getSourceName());
            favorite.setYear(video.getYear());
            favorite.setVideoId(video.getId());
            favorite.setTotalEpisodes(episodes.size());
            favorite.setSaveTime(System.currentTimeMillis());
            
            api.addFavorite(key, favorite, new LunaTVApi.ApiCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    isFavorite = true;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnFavorite.setText(R.string.action_unfavorite);
                            Toast.makeText(DetailActivity.this, "已添加收藏", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DetailActivity.this, "操作失败: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private void playVideo(int episodeIndex) {
        if (episodes.isEmpty() || episodeIndex >= episodes.size()) {
            Toast.makeText(this, "暂无可播放的视频", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.EXTRA_VIDEO, video);
        intent.putExtra(PlayerActivity.EXTRA_EPISODE_INDEX, episodeIndex);
        intent.putExtra(PlayerActivity.EXTRA_EPISODES, new ArrayList<>(episodes));
        startActivity(intent);
    }
}