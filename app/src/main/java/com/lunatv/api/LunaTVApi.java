package com.lunatv.api;

import android.util.Log;

import com.google.gson.Gson;
import com.lunatv.models.Favorite;
import com.lunatv.models.PlayRecord;
import com.lunatv.models.Video;
import com.lunatv.utils.Preferences;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class LunaTVApi {
    private static final String TAG = "LunaTVApi";
    private static final int CONNECT_TIMEOUT = 30;
    private static final int READ_TIMEOUT = 30;
    
    private ApiService apiService;
    private String baseUrl;
    
    public interface ApiService {
        @POST("/api/login")
        Call<LoginResponse> login(@Body LoginRequest request);
        
        @GET("/api/server-config")
        Call<ServerConfigResponse> getServerConfig();
        
        @GET("/api/search")
        Call<SearchResponse> search(@Query("q") String query);
        
        @GET("/api/detail")
        Call<DetailResponse> getDetail(
            @Query("source") String source,
            @Query("id") String id
        );
        
        @GET("/api/favorites")
        Call<Map<String, Favorite>> getFavorites();
        
        @POST("/api/favorites")
        Call<ApiResponse> addFavorite(@Body FavoriteRequest request);
        
        @DELETE("/api/favorites")
        Call<ApiResponse> removeFavorite(@Query("key") String key);
        
        @GET("/api/playrecords")
        Call<Map<String, PlayRecord>> getPlayRecords();
        
        @POST("/api/playrecords")
        Call<ApiResponse> savePlayRecord(@Body PlayRecordRequest request);
        
        @DELETE("/api/playrecords")
        Call<ApiResponse> removePlayRecord(@Query("key") String key);
        
        @GET("/api/searchhistory")
        Call<List<String>> getSearchHistory();
        
        @POST("/api/searchhistory")
        Call<List<String>> addSearchHistory(@Body SearchHistoryRequest request);
        
        @DELETE("/api/searchhistory")
        Call<ApiResponse> clearSearchHistory();
    }
    
    public LunaTVApi(String baseUrl) {
        this.baseUrl = formatBaseUrl(baseUrl);
        initRetrofit();
    }
    
    private String formatBaseUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            String host = url.split("/")[0];
            if (host.matches("^((\\d{1,3}\\.){3}\\d{1,3})(:\\d+)?$") || host.contains(":")) {
                url = "http://" + url;
            } else {
                url = "https://" + url;
            }
        }
        if (!url.endsWith("/")) {
            url = url + "/";
        }
        return url;
    }
    
    private void initRetrofit() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .cookieJar(new JavaNetCookieJar(new java.net.CookieManager()))
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder();
                    
                    // 添加 Cookie
                    String cookie = Preferences.getAuthCookie();
                    if (cookie != null && !cookie.isEmpty()) {
                        builder.header("Cookie", cookie);
                    }
                    
                    return chain.proceed(builder.build());
                }
            });
        
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        
        apiService = retrofit.create(ApiService.class);
    }
    
    // 登录
    public void login(String username, String password, final ApiCallback<LoginResponse> callback) {
        LoginRequest request = new LoginRequest(username, password);
        apiService.login(request).enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 保存所有 Cookie
                    List<String> cookies = response.headers().values("Set-Cookie");
                    if (cookies != null && !cookies.isEmpty()) {
                        StringBuilder cookieBuilder = new StringBuilder();
                        for (String cookie : cookies) {
                            if (cookieBuilder.length() > 0) {
                                cookieBuilder.append("; ");
                            }
                            cookieBuilder.append(cookie.split(";")[0]); // 只取 name=value 部分
                        }
                        Preferences.setAuthCookie(cookieBuilder.toString());
                        Log.d(TAG, "Saved cookies: " + cookieBuilder.toString());
                    }
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("登录失败: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Login failed", t);
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 搜索
    public void search(String query, final ApiCallback<List<Video>> callback) {
        apiService.search(query).enqueue(new retrofit2.Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, retrofit2.Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getResults());
                } else {
                    callback.onError("搜索失败: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 获取详情
    public void getDetail(String source, String id, final ApiCallback<Video> callback) {
        apiService.getDetail(source, id).enqueue(new retrofit2.Callback<DetailResponse>() {
            @Override
            public void onResponse(Call<DetailResponse> call, retrofit2.Response<DetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getVideo());
                } else {
                    callback.onError("获取详情失败: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<DetailResponse> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 获取收藏
    public void getFavorites(final ApiCallback<Map<String, Favorite>> callback) {
        apiService.getFavorites().enqueue(new retrofit2.Callback<Map<String, Favorite>>() {
            @Override
            public void onResponse(Call<Map<String, Favorite>> call, retrofit2.Response<Map<String, Favorite>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("获取收藏失败: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, Favorite>> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 添加收藏
    public void addFavorite(String key, Favorite favorite, final ApiCallback<Void> callback) {
        FavoriteRequest request = new FavoriteRequest(key, favorite);
        apiService.addFavorite(request).enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("添加收藏失败: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 移除收藏
    public void removeFavorite(String key, final ApiCallback<Void> callback) {
        apiService.removeFavorite(key).enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("移除收藏失败: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 获取播放记录
    public void getPlayRecords(final ApiCallback<Map<String, PlayRecord>> callback) {
        apiService.getPlayRecords().enqueue(new retrofit2.Callback<Map<String, PlayRecord>>() {
            @Override
            public void onResponse(Call<Map<String, PlayRecord>> call, retrofit2.Response<Map<String, PlayRecord>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("获取播放记录失败: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<Map<String, PlayRecord>> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    // 保存播放记录
    public void savePlayRecord(String key, PlayRecord record, final ApiCallback<Void> callback) {
        PlayRecordRequest request = new PlayRecordRequest(key, record);
        apiService.savePlayRecord(request).enqueue(new retrofit2.Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("保存播放记录失败: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }
    
    // 请求和响应类
    public static class LoginRequest {
        public String username;
        public String password;
        
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }
    
    public static class LoginResponse {
        public boolean ok;
    }
    
    public static class ServerConfigResponse {
        public String SiteName;
        public String StorageType;
        public String Version;
    }
    
    public static class SearchResponse {
        public List<Video> results;
        
        public List<Video> getResults() { return results; }
    }
    
    public static class DetailResponse {
        public Video video;
        
        public Video getVideo() { return video; }
    }
    
    public static class ApiResponse {
        public boolean success;
    }
    
    public static class FavoriteRequest {
        public String key;
        public Favorite favorite;
        
        public FavoriteRequest(String key, Favorite favorite) {
            this.key = key;
            this.favorite = favorite;
        }
    }
    
    public static class PlayRecordRequest {
        public String key;
        public PlayRecord record;
        
        public PlayRecordRequest(String key, PlayRecord record) {
            this.key = key;
            this.record = record;
        }
    }
    
    public static class SearchHistoryRequest {
        public String keyword;
        
        public SearchHistoryRequest(String keyword) {
            this.keyword = keyword;
        }
    }
}