package com.example.calendar.Activities.ui;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.calendar.DAO.TaskDao;
import com.example.calendar.Entities.Status;
import com.example.calendar.Entities.Task;
import com.example.calendar.MyAppDatabase;
import com.example.calendar.R;

import java.util.Calendar;
import java.util.List;

@SuppressLint("DefaultLocale")
public class AddTaskActivity extends AppCompatActivity {

    private EditText shortNameEditText,descriptionEditText,durationEditText,locationEditText;
    private String StartDateText="";
    private TaskDao taskDao;
    private Spinner hoursSpinner,minutesSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task); //Φορτώνεται το layout για την αποθήκευση task

        taskDao = MyAppDatabase.getInstance(this).taskDao(); //Φόρτωση instance στη βάση δεδομένων
        logDatabaseContents();

        shortNameEditText = findViewById(R.id.shortNameEditText); //Εξαγωγή τιμής από το layout
        descriptionEditText = findViewById(R.id.descriptionEditText); //Εξαγωγή τιμής από το layout
        durationEditText = findViewById(R.id.durationEditText); //Εξαγωγή τιμής από το layout
        locationEditText = findViewById(R.id.locationEditText); //Εξαγωγή τιμής από το layout
        hoursSpinner = findViewById(R.id.hoursSpinner); //Εξαγωγή τιμής από το layout
        minutesSpinner = findViewById(R.id.minutesSpinner); //Εξαγωγή τιμής από το layout
        CalendarView calendarView = findViewById(R.id.calendarView); //Εξαγωγή τιμής από το layout
        Button saveButton = findViewById(R.id.saveButton); //Εξαγωγή τιμής από το layout
        Button cancelButton= findViewById(R.id.cancelButton); //Εξαγωγή τιμής από το layout
        saveButton.setOnClickListener(v -> saveTask()); //Όταν πατηθεί το κουμπί saveButton, τότε καλείται η μέθοδος αποθήκευσης του task
        cancelButton.setOnClickListener(v -> { //Όταν πατηθεί το κουμπί cancelButton, τότε ο χρήστης επιστρέφει στο κεντρικό μενού
            Intent ReturnToMenuIntent = new Intent(AddTaskActivity.this,CalendarActivity.class); //Δημιουργία intent επιστροφής
            startActivity(ReturnToMenuIntent);
        });

        String[] hours = new String[24]; //Δημιουργία πίνακα για ώρες
        String[] minutes = new String[60]; //Δημιουργία πίνακα για λεπτά

        for (int i = 0; i < 24; i++) {
            hours[i] = String.format("%02d", i); //Μορφή ως 00,,...,23
        }

        for (int i = 0; i < 60; i++) {
            minutes[i] = String.format("%02d", i); //Μορφή ως 00,,...,59
        }

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> { //Στη περίπτωση που ο χρήστης αλλάξει την ημερομηνία του calendar
            String formattedMonth = String.format("%02d", month + 1);  // Προσθήκη 1 γιατί οι μήνες ξεκινούν από 0
            String formattedDay = String.format("%02d", dayOfMonth);    // Δύο ψηφία για την ημέρα
            StartDateText = year + "-" + formattedMonth + "-" + formattedDay;
        });

        ArrayAdapter<String> hoursAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hours); //Δημιουργία adapters για να έχουν τη μορφή 01 (και οχι 1)
        hoursAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hoursSpinner.setAdapter(hoursAdapter);

        ArrayAdapter<String> minutesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, minutes); //Δημιουργία adapters για να έχουν τη μορφή 01 (και οχι 1)
        minutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        minutesSpinner.setAdapter(minutesAdapter);
    }

    private void saveTask() { //Μέθοδος αποθήκευσης task
        String shortName = shortNameEditText.getText().toString(); //Δήλωση μεταβλητής για όνομα task
        String description = descriptionEditText.getText().toString(); //Δήλωση μεταβλητής για περιγραφή task
        String location = locationEditText.getText().toString(); //Δήλωση μεταβλητής για περιοχή task
        int hours=hoursSpinner.getSelectedItemPosition(); //Δήλωση μεταβλητής για ώρες task
        int minutes=minutesSpinner.getSelectedItemPosition(); //Δήλωση μεταβλητής για λεπτά task
        String durationText = durationEditText.getText().toString(); //Δήλωση μεταβλητής για διάρκεια task
        int duration;
        if (shortName.isEmpty()) { //Στη περίπτωση που ο χρήστης δεν συμπληρώσει όνομα
            Toast.makeText(this, "Task name cannot be empty", Toast.LENGTH_SHORT).show(); //Εμφάνιση pop up μυνήματος
            return; //Επιστροφή
        }
        if (description.isEmpty()) { //Στη περίπτωση που ο χρήστης δεν συμπληρώσει περιγραφή
            Toast.makeText(this, "Task description cannot be empty", Toast.LENGTH_SHORT).show(); //Εμφάνιση pop up μυνήματος
            return; //Επιστροφή
        }
        if (StartDateText.isEmpty()) { //Στη περίπτωση που δεν αλλάξει την ημερομηνία εισαγωγή σημερινής ημερομηνίας
            Calendar calendar = Calendar.getInstance(); //Λήψη τορινής ημερομηνίας
            int currentYear = calendar.get(Calendar.YEAR); //Λήψη έτους
            int currentMonth = calendar.get(Calendar.MONTH) + 1; //Προσθήκη 1 γιατί οι μήνες ξεκινούν από το 0
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH); //Λήψη ημερομηνίας
            String formattedMonth = String.format("%02d", currentMonth); //Δύο ψηφία για τον μήνα
            String formattedDay = String.format("%02d", currentDay);    //Δύο ψηφία για την ημέρα
            StartDateText = currentYear + "-" +formattedMonth + "-" + formattedDay;
        }
        if (durationText.isEmpty()) { //Στη περίπτωση που η διάρκεια είναι κενή
            Toast.makeText(this, "Duration cannot be empty", Toast.LENGTH_SHORT).show(); //Εμφάνιση pop up μυνήματος
            return; //Επιστροφή
        }else{
            duration = Integer.parseInt(durationText);
            if(duration<0){ //Στη περίπτωση που ο χρήστης δώσει αρνητική διάρκεια
                Toast.makeText(this, "Duration cannot be a negative number", Toast.LENGTH_SHORT).show(); //Εμφάνιση pop up μυνήματος
                return; //Επιστροφή
            }
        }
        String combinedDateTime = StartDateText + " " + String.format("%02d:%02d", hours, minutes); //Συνδιασμός ημερομηνίας και ώρας
            Task task = new Task(shortName,description,combinedDateTime, duration,location); //Δημιουργία νέου αντικειμένου task

            new Thread(() -> { //Δημιουργία thread για την αποθήκευση του task
                try{
                    taskDao.insertTask(task); //Προσθήκη στη βάση δεδομένων
                    Status status=new Status(Status.RECORDED, (int) taskDao.getLastTaskUid()); //Δημιουργία αντικειμένου task με τιμή recorded
                    taskDao.insertStatus(status); //Προσθήκη στη βάση δεδομένων
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show(); //Εμφάνιση pop up μυνήματος επιτυχίας
                        finish();
                    });
                } catch (Exception e) {  //Στη περίπτωση σφάλματος
                    throw new RuntimeException(e);
                }
            }).start();
    }

    private void logDatabaseContents() {
        new Thread(() -> {
            List<Task> tasks = taskDao.getAllTasks(); // Ανάκτηση όλων των tasks
            for (Task task : tasks) {
                Log.d("DatabaseContents", "Task ID: " + task.getUid() +
                        ", Short Name: " + task.getShortName() +
                        ", Description: " + task.getBriefDescription() +
                        ", Start Time: " + task.getStartTime() +
                        ", Duration: " + task.getDuration() +
                        ", Location: " + task.getLocation());
            }
            List<Status> statuses=taskDao.getAllStatuses();
            for(Status status : statuses){
                Log.d("Databasestatuses","TaskId:"+status.getTaskId()+" ,Status:"+status.getStatusId());
            }

        }).start();
    }
}

