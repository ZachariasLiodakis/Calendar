package com.example.calendar.Activities.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendar.Adapters.TaskAdapter;
import com.example.calendar.DAO.TaskDao;
import com.example.calendar.Entities.Status;
import com.example.calendar.Entities.Task;
import com.example.calendar.MyAppDatabase;
import com.example.calendar.R;

import java.util.ArrayList;
import java.util.List;

public class ViewTasksActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTasks; //RecyclerView για την εμφάνιση των tasks
    private TaskAdapter taskAdapter; //Προσαρμογέας για το RecyclerView
    private TaskDao taskDao; //Data Access Object για τα tasks
    private Handler mainHandler; //Χειριστής για την εκτέλεση εργασιών στο main thread

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tasks); //Ορισμός του layout της δραστηριότητας

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks); // Ανάκτηση του RecyclerView από το layout
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this)); //Ορισμός του layout manager για κατακόρυφη διάταξη

        Button BackButton = findViewById(R.id.BackButton); //Ανάκτηση του κουμπιού επιστροφής
        BackButton.setOnClickListener(v -> { //Ορισμός listener για το κουμπί
            Intent ReturnToMenuIntent = new Intent(ViewTasksActivity.this, CalendarActivity.class); //Δημιουργία intent για επιστροφή στο μενού
            startActivity(ReturnToMenuIntent); //Εκκίνηση της δραστηριότητας CalendarActivity
        });

        //Αρχικοποίηση της βάσης δεδομένων και του DAO
        MyAppDatabase db = MyAppDatabase.getInstance(getApplicationContext());
        taskDao = db.taskDao();

        mainHandler = new Handler(Looper.getMainLooper()); //Αρχικοποίηση του χειριστή για ενημέρωση UI στο κύριο νήμα

        //Φόρτωση δεδομένων σε background thread
        loadTasksInBackground();
    }

    private void loadTasksInBackground() {
        new Thread(() -> { //Δημιουργία νέου thread για φόρτωση δεδομένων
            List<Status> statusList = new ArrayList<>(); //Λίστα για την αποθήκευση των status
            //Εκτέλεση του query στη βάση δεδομένων σε background thread
            List<Task> taskList = taskDao.getUpdatableSortedTasks();
            for (Task task : taskList) {
                Log.d("DatabaseInfoI", "Task UID: " + task.getUid() + ", StartTime: " + task.getStartTime());
                statusList.add(taskDao.getStatusById(task.getUid()));
            }
            //Μετά την ανάκτηση των δεδομένων, ενημέρωση του UI στο κύριο νήμα
            mainHandler.post(() -> {
                taskAdapter = new TaskAdapter(ViewTasksActivity.this, taskList, statusList, task -> {
                    //Δημιουργία intent
                    Intent intent = new Intent(ViewTasksActivity.this, TaskDetailsActivity.class);
                    intent.putExtra("task", task); //Μεταφορά του task μέσω του intent
                    startActivity(intent); //Εκκίνηση της δραστηριότητας TaskDetailsActivity
                });
                recyclerViewTasks.setAdapter(taskAdapter); //Ρύθμιση του adapter στο RecyclerView
            });
        }).start();
    }
}
