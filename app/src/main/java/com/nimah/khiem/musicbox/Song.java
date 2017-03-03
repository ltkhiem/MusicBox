package com.nimah.khiem.musicbox;

/**
 * Created by Khiem on 8/12/2016.
 */
public class Song {
    private String title;
    private String singer;
    private String sourceUrl;
    private String imageUrl;
    private String detailUrl;

    public Song(String title, String singer, String sourceUrl) {
        this.title = title;
        this.singer = singer;
        this.sourceUrl = sourceUrl;
    }

    public Song(String title, String singer, String sourceUrl, String imageUrl) {
        this.title = title;
        this.singer = singer;
        this.sourceUrl = sourceUrl;
        this.imageUrl = imageUrl;
    }

    public Song(String title, String singer, String sourceUrl, String imageUrl, String detailUrl) {
        this.title = title;
        this.singer = singer;
        this.sourceUrl = sourceUrl;
        this.imageUrl = imageUrl;
        this.detailUrl = detailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }
}

