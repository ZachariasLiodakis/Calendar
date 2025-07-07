package com.example.calendar.Activities.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.calendar.DAO.TaskDao;
import com.example.calendar.MyAppDatabase;
import com.example.calendar.R;

public class DeleteTaskActivity extends AppCompatActivity {

    private TaskDao taskDao;
    private EditText taskIdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_task); //Φόρτωση layout διαγραφής task
        taskDao = MyAppDatabase.getInstance(this).taskDao();
        Button CancelButton=findViewById(R.id.cancelButton); //Δήλωση κουμπιού ακύρωσης
        Button DeleteButton=findViewById(R.id.DeleteButton); //Δήλωση κουμπιού διαγραφής
        taskIdEditText=findViewById(R.id.TaskIDEditText);
        DeleteButton.setOnClickListener(v -> deleteTask()); //Στη περίπτωση που πατηθεί το κουμπί delete, κλήση μεθόδου διαγραφής
        CancelButton.setOnClickListener(v->{ //Στη περίπτωση που πατηθεί το κουμπί
            Intent ReturnToMenuIntent=new Intent(DeleteTaskActivity.this, CalendarActivity.class); //Δημιουργία intent
            startActivity(ReturnToMenuIntent); //Έναρξη δραστηριότητας
        });
    }

    private void deleteTask() { //Μέθοδος διαγραφής task
        String taskIdStr = taskIdEditText.getText().toString();

        if (taskIdStr.isEmpty()) {//Έλεγχος αν το πεδίο είναι κενό
            Toast.makeText(this, "Please enter a Task ID", Toast.LENGTH_SHORT).show(); //Εμφάνιση pop up μυνήματος
            return; //Επιστροφή
        }
        int taskId = Integer.parseInt(taskIdStr);
        new Thread(() -> {
            try {

                if (taskDao.getTaskById(taskId) == null) { //Έλεγχος ύπαρξης του task
                    runOnUiThread(() -> Toast.makeText(DeleteTaskActivity.this, "Task not found", Toast.LENGTH_SHORT).show()); //Εμφάνιση pop up μυνήματος
                    return; //Επιστροφή
                }
                //Διαγραφή status και task
                taskDao.deleteStatus(taskDao.getStatusById(taskId));
                int taskDeleted = taskDao.deleteTaskById(taskId);

                runOnUiThread(() -> {
                    Toast.makeText(DeleteTaskActivity.this, "Rows affected:"+taskDeleted, Toast.LENGTH_SHORT).show(); //Εμφάνιση pop up μυνήματος
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(DeleteTaskActivity.this, "Error deleting task", Toast.LENGTH_SHORT).show()); //Εμφάνιση pop up μυνήματος
            }
        }).start();
    }
}