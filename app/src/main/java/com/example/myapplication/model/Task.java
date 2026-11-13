package com.example.myapplication.model;

import java.io.Serializable;

/**
 * Model class representing a personal task.
 */
public class Task implements Serializable {
    private int id;
    private String title;
    private String description;
    private String dueDate;
    private String priority; // "Thấp", "Trung bình", "Cao"
    private int status;      // 0: Chưa thực hiện, 1: Đã hoàn thành

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_COMPLETED = 1;

    public static final String PRIORITY_LOW = "Thấp";
    public static final String PRIORITY_MEDIUM = "Trung bình";
    public static final String PRIORITY_HIGH = "Cao";

    // Constructor without ID (for new tasks)
    public Task(String title, String description, String dueDate, String priority, int status) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    // Full constructor (for loading from SQLite)
    public Task(int id, String title, String description, String dueDate, String priority, int status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate != null ? dueDate : "";
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority != null ? priority : PRIORITY_MEDIUM;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return this.status == STATUS_COMPLETED;
    }

    public String getStatusText() {
        return isCompleted() ? "Đã hoàn thành" : "Chưa thực hiện";
    }
}
