package com.example.calendar.Activities.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.calendar.DAO.TaskDao;
import com.example.calendar.Entities.Task;
import com.example.calendar.MyAppDatabase;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExportTasksActivity extends AppCompatActivity {
    TaskDao taskDao; //Δήλωση του Dao
    private static final String TAG = "ExportTasksActivity"; //Tag για logging

    private final Executor executor = Executors.newSingleThreadExecutor();  //Εκτελεστής για τη διαχείριση διεργασιών στο background

    //Εκκίνηση ενός ActivityResultLauncher για τη δημιουργία αρχείων
    private final ActivityResultLauncher<String> createDocumentLauncher =
            registerForActivityResult(new CreateDocument("text/plain"), resultUri -> {
                if (resultUri != null) { //Αν επιστραφεί URI αρχείου
                    writeToFile(resultUri); //Εγγραφή των tasks στο αρχείο
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Ανάκτηση του αντικειμένου TaskDao από τη βάση δεδομένων
        MyAppDatabase db = MyAppDatabase.getInstance(getApplicationContext());
        taskDao = db.taskDao();

        //Εκτέλεση της δημιουργίας του αρχείου σε background thread
        executor.execute(() -> {
            List<Task> tasks = taskDao.getUpdatableTasks(); //Ανάκτηση tasks που μπορούν να ενημερωθούν
            createFile(tasks); //Δημιουργία αρχείου
        });
    }

    private void createFile(List<Task> tasks) {
        //Δημιουργία ενός string με τις εργασίες που θα εξαχθούν
        StringBuilder tasksText = new StringBuilder("Task List:\n\n");
        for (Task task : tasks) {
            //Προσθήκη πληροφοριών για κάθε εργασία
            tasksText.append("Task ID: ").append(task.getUid()).append("\n")
                    .append("Short Name: ").append(task.getShortName()).append("\n")
                    .append("Status: ").append(taskDao.getStatusById(task.getUid()).getStatusId()).append("\n")
                    .append("Start Date: ").append(task.getStartTime()).append("\n")
                    .append("Duration: ").append(task.getDuration()).append("\n")
                    .append("Location: ").append(task.getLocation()).append("\n\n");
        }
        //Εκκίνηση intent για τη δημιουργία αρχείου με προεπιλεγμένο όνομα
        createDocumentLauncher.launch("tasks_export.txt");
    }

    private void writeToFile(Uri uri) {
        //Δημιουργία νέου εκτελεστή για τη διαδικασία εγγραφής σε background
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            ContentResolver contentResolver = getContentResolver(); //Για τη διαχείριση αρχείων
            StringBuilder tasksText = new StringBuilder(); //Για την αποθήκευση δεδομένων σε string

            try (OutputStream outputStream = contentResolver.openOutputStream(uri); //Άνοιγμα ροής δεδομένων
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) { //Writer για εγγραφή

                if (outputStream == null) { //Έλεγχος αν το outputStream είναι null
                    Log.e(TAG, "Failed to open output stream");
                    return;
                }

                //Δημιουργία περιεχομένων για το αρχείο
                tasksText.append("Task List:\n\n");
                List<Task> tasks = taskDao.getUpdatableTasks(); //Ανάκτηση εργασιών
                if (tasks == null || tasks.isEmpty()) { //Έλεγχος αν δεν υπάρχουν εργασίες
                    Log.e(TAG, "No tasks found to write");
                }

                assert tasks != null; //Επιβεβαίωση ότι η λίστα των εργασιών δεν είναι null
                for (Task task : tasks) {
                    //Προσθήκη των δεδομένων κάθε εργασίας στο string
                    tasksText.append("Task ID: ").append(task.getUid()).append("\n")
                            .append("Short Name: ").append(task.getShortName()).append("\n")
                            .append("Status: ").append(taskDao.getStatusById(task.getUid()).getStatusId()).append("\n")
                            .append("Start Date: ").append(task.getStartTime()).append("\n")
                            .append("Duration: ").append(task.getDuration()).append("\n")
                            .append("Location: ").append(task.getLocation()).append("\n\n");
                }

                //Εγγραφή στο αρχείο
                writer.write(tasksText.toString());
                Log.d(TAG, "File written successfully");

                //Μετά την ολοκλήρωση της εγγραφής, επιστροφή στην προηγούμενη δραστηριότητα
                runOnUiThread(this::finish);

            } catch (IOException e) { //Διαχείριση σφαλμάτων κατά την εγγραφή
                Log.e(TAG, "Error writing file", e);
            }
        });
    }
}
