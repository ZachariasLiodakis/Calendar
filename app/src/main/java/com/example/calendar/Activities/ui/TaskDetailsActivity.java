package com.example.calendar.Activities.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.calendar.DAO.TaskDao;
import com.example.calendar.Entities.Status;
import com.example.calendar.Entities.Task;
import com.example.calendar.MyAppDatabase;
import com.example.calendar.R;

public class TaskDetailsActivity extends AppCompatActivity {

    TaskDao taskDao; //Δήλωση του Dao
    Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details); //Φόρτωση layout για την προβολή των λεπτομεριών του task
        TextView tvTaskId = findViewById(R.id.tvTaskDetailIdValue); //Δήλωση μεταβλητής
        TextView tvShortName = findViewById(R.id.tvTaskDetailShortNameValue); //Δήλωση μεταβλητής
        TextView tvStatus = findViewById(R.id.tvTaskDetailStatusValue); //Δήλωση μεταβλητής
        TextView tvStartDate = findViewById(R.id.tvTaskDetailStartDateValue); //Δήλωση μεταβλητής
        TextView tvDuration = findViewById(R.id.tvTaskDetailDurationValue); //Δήλωση μεταβλητής
        TextView tvLocationValue = findViewById(R.id.tvTaskDetailLocationValue); //Δήλωση μεταβλητής
        TextView tvDescriptionValue = findViewById(R.id.tvTaskDetailDescriptionValue); //Δήλωση μεταβλητής
        LinearLayout locationLayout = findViewById(R.id.locationLayout); //Δήλωση μεταβλητής
        Intent intent = getIntent(); //Δημιουργία intent
        task = (Task) intent.getSerializableExtra("task");
        Button btnBack = findViewById(R.id.btnBack); //Δήλωση μεταβλητής
        Button btnChangeStatus = findViewById(R.id.btnChangeStatus); //Δήλωση μεταβλητής
        Button btnViewOnGmaps = findViewById(R.id.btnViewOnGmaps); //Δήλωση μεταβλητής
        btnBack.setOnClickListener(v -> { //Στην περίπτωση που πατηθεί το κουμπί επιστροφής
            Intent ReturnToMenuIntent = new Intent(TaskDetailsActivity.this, ViewTasksActivity.class); //Δημιουργία intent
            startActivity(ReturnToMenuIntent); //Έναρξη δραστηριότητας main menu
        });
        btnChangeStatus.setOnClickListener(v -> changestatustocompleted()); //Στην περίπτωση που πατηθεί το κουμπί αλλαγής κατάστασης, κλήση της μεθόδου αλλαγής
        btnViewOnGmaps.setOnClickListener(v -> viewOnGoogleMaps()); //Στην περίπτωση που πατηθεί το κουμπί προβολής στους χάρτες, κλήση της μεθόδου

        if (task != null) { //Αν το task δεν είναι κενό
            String location = task.getLocation(); //Εξαγωγή τοποθεσίας
            if (location == null || location.isEmpty()) { //Αν η τοποθεσία είναι κενή
                locationLayout.setVisibility(View.GONE); //Απόκρυψη πεδίου τοποθεσίας
                btnViewOnGmaps.setVisibility(View.GONE); //Απόκρυψη κουμπιού προβολής στους χάρτες
            } else { //Αν η τοποθεσία δεν είναι κενή
                tvLocationValue.setText(location);
                locationLayout.setVisibility(View.VISIBLE); //Εμφάνιση πεδίου τοποθεσίας
                btnViewOnGmaps.setVisibility(View.VISIBLE); //Εμφάνιση κουμπιού προβολής στους χάρτες
            }
            tvTaskId.setText(String.valueOf(task.getUid()));
            tvShortName.setText(task.getShortName());


            new Thread(() -> { //Ανάκτηση κατάστασης στο παρασκήνιο
                try {
                    MyAppDatabase db = MyAppDatabase.getInstance(getApplicationContext());
                    taskDao = db.taskDao();
                    Status status = taskDao.getStatusById(task.getUid());
                    runOnUiThread(() -> tvStatus.setText(status.getStatusId()));
                } catch (Exception e) {
                    runOnUiThread(() -> Toast.makeText(this, "Error loading status", Toast.LENGTH_SHORT).show());
                    throw new RuntimeException("Error loading status"); // Να καταγράφουμε το σφάλμα για debugging
                }
            }).start();

            tvStartDate.setText(task.getStartTime());
            tvDuration.setText(String.valueOf(task.getDuration()));
            tvDescriptionValue.setText(task.getBriefDescription());
        }
    }

    private void changestatustocompleted() { //Μέθοδος αλλαγής κατάστασης
        new Thread(() -> {
            try {
                MyAppDatabase db = MyAppDatabase.getInstance(getApplicationContext());
                taskDao = db.taskDao();
                taskDao.updateTaskStatus(task.getUid(), Status.COMPLETED); //Αλλαγή σε completed
                runOnUiThread(() -> Toast.makeText(this, "Status changed successfully", Toast.LENGTH_SHORT).show()); //Εμφάνιση pop up μυνήματος
            } catch (Exception e) { //Αν προκύψει σφάλμα
                runOnUiThread(() -> Toast.makeText(this, "Could not change status to completed", Toast.LENGTH_SHORT).show()); //Εμφάνιση pop up μυνήματος
                throw new RuntimeException("Could not change status to completed");
            }
        }).start();
    }

    private void viewOnGoogleMaps() { //Μέθοδος προβολής στους χάρτες
        new Thread(() -> {
            try {
                String location = task.getLocation();
                String geoUri = "geo:0,0?q=" + Uri.encode(location);
                Uri gmmIntentUri = Uri.parse(geoUri);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                runOnUiThread(() -> {
                    try {
                        startActivity(mapIntent);
                    } catch (Exception e) { //Αν προκύψει σφάλμα
                        Toast.makeText(this, "Google Maps is not installed.", Toast.LENGTH_SHORT).show(); //Εμφάνιση pop up μυνήματος
                    }
                });
            } catch (Exception e) { //Αν προκύψει σφάλμα
                runOnUiThread(() ->
                        Toast.makeText(this, "An error occurred while trying to open the location.", Toast.LENGTH_SHORT).show() //Εμφάνιση pop up μυνήματος
                );
                throw new RuntimeException("An error occurred while trying to open the location.");
            }
        }).start();
    }
}
