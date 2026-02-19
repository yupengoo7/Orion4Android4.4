package com.lunatv.models;

import java.io.Serializable;
import java.util.List;

public class Video implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String title;
    private String poster;
    private List<String> episodes;
    private String source;
    private String sourceName;
    private String year;
    private String desc;
    private String director;
    private String actor;
    private String type;
    private String area;
    private String remarks;

    public Video() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }

    public List<String> getEpisodes() { return episodes; }
    public void setEpisodes(List<String> episodes) { this.episodes = episodes; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getActor() { return actor; }
    public void setActor(String actor) { this.actor = actor; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}