package com.example.calendar.Entities;

import android.content.ContentValues;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "tasks")
public class Task implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    private int uid;

    @ColumnInfo(name = "shortName")
    private String shortName;

    @ColumnInfo(name = "briefDescription")
    private  String briefDescription;

    @ColumnInfo(name = "startTime")
    private String startTime;

    @ColumnInfo(name = "duration")
    private int duration;

    @ColumnInfo(name = "location")
    private String location;
    // Constructors
    public Task(String shortName, String briefDescription, String startTime, int duration, String location){
        this.shortName = shortName;
        this.briefDescription = briefDescription;
        this.startTime=startTime;
        this.duration = duration;
        this.location = location;
    }

    public Task() {
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getShortName() {
        return shortName;
    }

    public String getBriefDescription() {
        return briefDescription;
    }

    public String getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }
    public String getLocation() {
        return location;
    }

    public void setBriefDescription(String briefDescription) {
        this.briefDescription = briefDescription;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public static Task fromContentValues(ContentValues values) {
        final Task task = new Task();
        if (values.containsKey("uid")) {
            task.setUid(values.getAsInteger("uid"));
        }
        if (values.containsKey("shortName")) {
            task.setShortName(values.getAsString("shortName"));
        }
        if (values.containsKey("briefDescription")) {
            task.setBriefDescription(values.getAsString("briefDescription"));
        }
        if (values.containsKey("startTime")) {
            task.setStartTime(values.getAsString("startTime"));
        }
        if (values.containsKey("duration")) {
            task.setDuration(values.getAsInteger("duration"));
        }
        if (values.containsKey("location")) {
            task.setLocation(values.getAsString("location"));
        }
        return task;
    }


}
