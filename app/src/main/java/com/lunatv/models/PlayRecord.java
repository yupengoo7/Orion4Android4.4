package com.lunatv.models;

import java.io.Serializable;

public class PlayRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title;
    private String sourceName;
    private String cover;
    private int index;
    private int totalEpisodes;
    private int playTime;
    private int totalTime;
    private long saveTime;
    private String year;
    private String searchTitle;
    private String source;
    private String videoId;
    
    // 本地额外字段
    private int introEndTime;
    private int outroStartTime;

    public PlayRecord() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public int getTotalEpisodes() { return totalEpisodes; }
    public void setTotalEpisodes(int totalEpisodes) { this.totalEpisodes = totalEpisodes; }

    public int getPlayTime() { return playTime; }
    public void setPlayTime(int playTime) { this.playTime = playTime; }

    public int getTotalTime() { return totalTime; }
    public void setTotalTime(int totalTime) { this.totalTime = totalTime; }

    public long getSaveTime() { return saveTime; }
    public void setSaveTime(long saveTime) { this.saveTime = saveTime; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getSearchTitle() { return searchTitle; }
    public void setSearchTitle(String searchTitle) { this.searchTitle = searchTitle; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getVideoId() { return videoId; }
    public void setVideoId(String videoId) { this.videoId = videoId; }

    public int getIntroEndTime() { return introEndTime; }
    public void setIntroEndTime(int introEndTime) { this.introEndTime = introEndTime; }

    public int getOutroStartTime() { return outroStartTime; }
    public void setOutroStartTime(int outroStartTime) { this.outroStartTime = outroStartTime; }

    public int getProgress() {
        if (totalTime > 0) {
            return (int) ((playTime * 100.0) / totalTime);
        }
        return 0;
    }
}