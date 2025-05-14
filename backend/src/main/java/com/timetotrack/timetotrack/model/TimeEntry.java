package com.timetotrack.timetotrack.model;

import java.time.LocalDateTime;

public class TimeEntry {
    private Long id;
    private LocalDateTime fromTime;
    private LocalDateTime toTime;
    private Integer projectId;
    private Integer userId;

    public TimeEntry() {
    }

    public TimeEntry(Long id, LocalDateTime fromTime, LocalDateTime toTime, Integer projectId, Integer userId) {
        this.id = id;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.projectId = projectId;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFromTime() {
        return fromTime;
    }

    public void setFromTime(LocalDateTime fromTime) {
        this.fromTime = fromTime;
    }

    public LocalDateTime getToTime() {
        return toTime;
    }

    public void setToTime(LocalDateTime toTime) {
        this.toTime = toTime;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
