package com.example.calendar.Services;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.calendar.DAO.TaskDao;
import com.example.calendar.Entities.Status;
import com.example.calendar.Entities.Task;
import com.example.calendar.MyAppDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskService extends Service {

    private final ExecutorService executor = Executors.newSingleThreadExecutor(); //Δήλωση του executor
    private final Handler handler = new Handler(); //Δήλωση του handler
    private TaskDao taskDao; //Δήλωση του dao
    private Runnable periodicTask; //Δήλωση του runnable
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm"); //Καθορισμός format

    @Override
    public void onCreate() {
        super.onCreate();
        MyAppDatabase db = MyAppDatabase.getInstance(getApplicationContext());
        taskDao = db.taskDao();

        //Περιοδικός έλεγχος (κάθε 10 δευτερόλεπτα)
        periodicTask = new Runnable() {
            @Override
            public void run() {
                Log.d("TaskService", "Periodic task running...");
                executor.execute(() -> {
                    List<Task> tasks = taskDao.getUpdatableTasks();
                    for (Task task : tasks) {
                        // Ενημέρωση της κατάστασης του task
                        updateStatus(task, new Date());
                    }
                });
                //Επαναφορά του Runnable μετά από 10 δευτερόλεπτα
                handler.postDelayed(this, 10000);
            }
        };

        //Εκκίνηση της περιοδικής εργασίας
        handler.post(periodicTask);
    }

    private void updateStatus(Task task, Date currentTime) { //Μέθοδος ενημέρωσης status
        try {
            Date startDateTime = parseStringToDate(task.getStartTime()); //Εξαγωγή τορινής ημερομηνίας
            Date endDateTime = new Date(startDateTime.getTime() + (long) task.getDuration() * 60 * 60 * 1000); //Υπολογισμός τελικής ημερομηνίας

            if (currentTime.after(startDateTime) && currentTime.before(endDateTime)) { //Σύγκριση ημερομηνίας
                taskDao.updateTaskStatus(task.getUid(), Status.IN_PROGRESS); //Αλλαγή σε in-progress αν είναι μέσα στα περιθώρια έναρξης και λήξης του task η τορινή ημερομηνία
            } else if (currentTime.after(endDateTime)) {
                taskDao.updateTaskStatus(task.getUid(), Status.EXPIRED); //Αλλαγή σε expired αν έχει περάσει η ημερομηνία λήξης
            }
        } catch (ParseException e) { //Στη περίπτωση προβλήματος
            throw new RuntimeException("Error updating status");
        }
    }

    private Date parseStringToDate(String dateTimeString) throws ParseException { //Formatter για την ημερομηνία
        return FORMATTER.parse(dateTimeString);
    }

    @Override
    public void onDestroy() { //Στο τερματισμό της υπηρεσίας
        super.onDestroy();
        handler.removeCallbacks(periodicTask); //Σταμάτημα της περιοδικής εργασίας
        executor.shutdown(); //Τερματισμός του Executor
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
