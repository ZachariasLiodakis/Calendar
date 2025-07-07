package com.example.calendar.DAO;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.calendar.Entities.Status;
import com.example.calendar.Entities.Task;

import java.util.List;
import java.util.Map;

@Dao
public interface TaskDao {

    //Εισαγωγή νέου task
    @Insert
    void insertTask(Task task);
    //Εισαγωγή νέου task μέσω του content provider
    @Insert
    long insertContentTask(Task task);

    //Εισαγωγή νέου status
    @Insert
    void insertStatus(Status status);

    @Query("SELECT uid FROM tasks ORDER BY uid DESC LIMIT 1")
    long getLastTaskUid();

    //Ανάκτηση όλων των tasks
    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    //Ανάκτηση task βάσει UID
    @Query("SELECT * FROM tasks WHERE uid = :taskId")
    Task getTaskById(int taskId);

    //Ανάκτηση status task
    @Query("SELECT * FROM statuses WHERE taskId= :taskId")
    Status getStatusById(int taskId);

    //Ανάκτηση όλων των statuses
    @Query("SELECT * FROM statuses")
    List<Status> getAllStatuses();

    //Ανάκτηση ενημερώσιμων tasks
    @Query("SELECT * FROM tasks WHERE uid IN (SELECT taskId FROM statuses WHERE statusId != 'completed')")
    List<Task> getUpdatableTasks();

    //Ανάκτηση ενημερώσιμων tasks ταξινομημένα
    @Query("SELECT * FROM tasks WHERE uid IN (SELECT taskId FROM statuses WHERE statusId != 'completed') ORDER BY startTime ASC")
    List<Task> getUpdatableSortedTasks();

    //Ενημέρωση κατάστασης task βάσει taskId και statusId
    @Query("UPDATE statuses SET statusId = :newStatus WHERE taskId = :taskId")
    void updateTaskStatus(int taskId, String newStatus);

    //Διαγραφή task
    @Query("DELETE FROM tasks WHERE uid = :taskId")
    int deleteTaskById(int taskId); //Επιστρέφει affected rows
    //Διαγραφή status
    @Delete
    void deleteStatus(Status status);

    @Query("SELECT * FROM tasks")
    Cursor getAllTasksCursor();

    @Query("SELECT * FROM tasks WHERE uid = :taskId")
    Cursor getTaskByIdCursor(int taskId);

    @Update
    int updateTask(Task task);

}
