package com.example.calendar.Activities.ui;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import com.example.calendar.R;
import com.example.calendar.Services.TaskService;
import com.example.calendar.TaskContentProvider;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar); //Φόρτωση layout κεντρικού μενού

        Calendar calendar = Calendar.getInstance(); //Λήψη τορινής ημερομηνίας
        int year = calendar.get(Calendar.YEAR); //Λήψη έτους
        int month = calendar.get(Calendar.MONTH); //Λήψη μήνα
        int day = calendar.get(Calendar.DAY_OF_MONTH); //Λήψη ημέρας
        TextView MonthValue=findViewById(R.id.monthText);
        MonthValue.setText(day +"/"+(month +1)+"/"+ year); //Ορισμός text ημερομηνίας
        Button ExportTasksBtn=findViewById(R.id.exportTasksButton); //Δήλωση κουμπιού εξαγωγής tasks
        Button ViewTasksBtn=findViewById(R.id.viewTasksButton); //Δήλωση κουμπιού προβολής όλων των tasks
        Button CreateTasksBtn=findViewById(R.id.createTaskButton); //Δήλωση κουμπιού δημιουργίας task
        Button DeleteTaskBtn=findViewById(R.id.deleteTaskButton); //Δήλωση κουμπιού διαγραφής task
        Button ContentProviderBtn=findViewById(R.id.contentProviderButton); //Δήλωση κουμπιού content Provider
        ViewTasksBtn.setOnClickListener(v->{ //Στη περίπτωση που πατηθεί το κουμπί προβολής των tasks
            Intent ViewTasksIntent=new Intent(CalendarActivity.this,ViewTasksActivity.class); //Δημιουργία intent
            startActivity(ViewTasksIntent);  //Έναρξη δραστηριότητας
        });
        CreateTasksBtn.setOnClickListener(v -> { //Στη περίπτωση που πατηθεί το κουμπί δημιουργίας task
            Intent CreateTaskIntent=new Intent(CalendarActivity.this,AddTaskActivity.class); //Δημιουργία intent
            startActivity(CreateTaskIntent); //Έναρξη δραστηριότητας
        });
        DeleteTaskBtn.setOnClickListener(v -> { //Στη περίπτωση που πατηθεί το κουμπί διαγραφής task
            Intent DeleteTaskIntent=new Intent(CalendarActivity.this,DeleteTaskActivity.class); //Δημιουργία intent
            startActivity(DeleteTaskIntent); //Έναρξη δραστηριότητας
        });
        ExportTasksBtn.setOnClickListener(v -> { //Στη περίπτωση που πατηθεί το κουμπί εξαγωγής των tasks
            Intent ExportTasksIntent = new Intent(CalendarActivity.this, ExportTasksActivity.class); //Δημιουργία intent
            startActivity(ExportTasksIntent); //Έναρξη δραστηριότητας
        });
        ContentProviderBtn.setOnClickListener(v -> {
            //Δημιουργία ContentValues με τα δεδομένα της εργασίας
            ContentValues values = new ContentValues();
            values.put("shortName", "TaskName");
            values.put("briefDescription", "description");
            values.put("startTime", "2025-01-29 10:00");
            values.put("duration", 6);
            values.put("location", "Athens");

            //Εισαγωγή της νέας εργασίας μέσω του ContentProvider
            Uri uri = getContentResolver().insert(TaskContentProvider.CONTENT_URI, values);
            if (uri != null) {
                Toast.makeText(getApplicationContext(), "Task insertion successful", Toast.LENGTH_SHORT).show();

                //Προγραμματισμός της ενημέρωσης μετά από 30 δευτερόλεπτα
                new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                    // Δημιουργία ContentValues για την ενημέρωση
                    ContentValues updateValues = new ContentValues();
                    updateValues.put("shortName", "newTaskName");
                    updateValues.put("duration", 8);

                    //Ενημέρωση της εργασίας
                    int updatedRows = getContentResolver().update(uri, updateValues, null, null);
                    if (updatedRows > 0) {
                        Toast.makeText(getApplicationContext(), "Task updated successfully", Toast.LENGTH_SHORT).show();

                        //Προγραμματισμός της διαγραφής μετά από άλλα 30 δευτερόλεπτα
                        new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                            // Διαγραφή της εργασίας
                            int deletedRows = getContentResolver().delete(uri, null, null);
                            if (deletedRows > 0) {
                                Toast.makeText(getApplicationContext(), "Task Deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Task deletion failed", Toast.LENGTH_SHORT).show();
                            }
                        }, 30000);

                    } else {
                        Toast.makeText(getApplicationContext(), "Task update failed", Toast.LENGTH_SHORT).show();
                    }
                }, 30000);

            } else {
                Toast.makeText(getApplicationContext(), "Task insertion failed", Toast.LENGTH_SHORT).show();
            }
        });
        Intent serviceIntent = new Intent(CalendarActivity.this, TaskService.class); //Δημιουργία intent
        startService(serviceIntent); //Έναρξη δραστηριότητας
    }
}
