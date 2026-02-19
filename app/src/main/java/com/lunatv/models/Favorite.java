package com.lunatv.models;

import java.io.Serializable;

public class Favorite implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sourceName;
    private int totalEpisodes;
    private String title;
    private String year;
    private String cover;
    private long saveTime;
    private String searchTitle;
    private String source;
    private String videoId;

    public Favorite() {}

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public int getTotalEpisodes() { return totalEpisodes; }
    public void setTotalEpisodes(int totalEpisodes) { this.totalEpisodes = totalEpisodes; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public long getSaveTime() { return saveTime; }
    public void setSaveTime(long saveTime) { this.saveTime = saveTime; }

    public String getSearchTitle() { return searchTitle; }
    public void setSearchTitle(String searchTitle) { this.searchTitle = searchTitle; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }
}