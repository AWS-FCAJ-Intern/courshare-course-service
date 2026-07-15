package com.courshare.course.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    private String id;

    @Column(name = "section_id", nullable = false)
    private String sectionId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String type = "VIDEO";

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "media_url")
    private String mediaUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "\"order\"", nullable = false)
    private int orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", insertable = false, updatable = false)
    private Section section;

    protected Lesson() {
    }

    public Lesson(String sectionId, String title, String description, String type, String videoUrl, String mediaUrl, String content, int orderIndex) {
        this.id = java.util.UUID.randomUUID().toString();
        this.sectionId = sectionId;
        this.title = title;
        this.description = description;
        this.type = type != null ? type : "VIDEO";
        this.videoUrl = videoUrl;
        this.mediaUrl = mediaUrl;
        this.content = content;
        this.orderIndex = orderIndex;
    }

    public String getId() {
        return id;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Section getSection() {
        return section;
    }
}
