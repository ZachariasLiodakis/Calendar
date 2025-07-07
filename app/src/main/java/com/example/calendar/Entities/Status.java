package com.example.calendar.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "statuses",primaryKeys = {"taskId", "statusId"},
        foreignKeys = @ForeignKey(entity = Task.class, parentColumns = "uid", childColumns = "taskId", onDelete = ForeignKey.CASCADE))
public class Status {

    @ColumnInfo(name = "statusId")
    @NonNull
    private final String statusId;
    @ColumnInfo(name = "taskId")
    private final int taskId;


    public static final String RECORDED = "recorded";
    public static final String IN_PROGRESS = "in-progress";
    public static final String EXPIRED = "expired";
    public static final String COMPLETED = "completed";

    public Status(@NonNull String statusId, int taskId) {
        this.statusId = statusId;
        this.taskId = taskId;
    }

    public int getTaskId() {
        return taskId;
    }

    @NonNull
    public String getStatusId() {
        return statusId;
    }
}