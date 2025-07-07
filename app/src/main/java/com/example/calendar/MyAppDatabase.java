package com.example.calendar;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Context;

import com.example.calendar.DAO.TaskDao;
import com.example.calendar.Entities.Status;
import com.example.calendar.Entities.Task;

@Database(entities = {Task.class, Status.class}, version = 1, exportSchema = false)
public abstract class MyAppDatabase extends RoomDatabase {

    public abstract TaskDao taskDao(); //Δήλωση Dao

    private static volatile MyAppDatabase INSTANCE;

    public static MyAppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MyAppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MyAppDatabase.class, "task_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
