package com.example.calendar;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.calendar.DAO.TaskDao;
import com.example.calendar.Entities.Status;
import com.example.calendar.Entities.Task;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskContentProvider extends ContentProvider {
    //Ορισμός σταθερών για την αυθεντικότητα, τον πίνακα και το URI
    private static final String AUTHORITY = "com.example.calendar.provider";
    private static final String TASKS_TABLE = "tasks";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TASKS_TABLE);

    // Ορισμός των τύπων URI
    private static final int TASKS = 1;
    private static final int TASK_ID = 2;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private ExecutorService executorService;

    static {
        // Ορισμός της αντιστοίχισης URI
        uriMatcher.addURI(AUTHORITY, TASKS_TABLE, TASKS);
        uriMatcher.addURI(AUTHORITY, TASKS_TABLE + "/#", TASK_ID);
    }

    private TaskDao taskDao;

    @Override
    public boolean onCreate() {
        //Αρχικοποίηση της βάσης δεδομένων και του executorService
        Context context = getContext();
        MyAppDatabase database = MyAppDatabase.getInstance(context);
        taskDao = database.taskDao();
        executorService = Executors.newSingleThreadExecutor(); //ExecutorService για ασύγχρονες εργασίες
        return true; //Επιστροφή true όταν η αρχικοποίηση ολοκληρωθεί
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Ορισμός του cursor και του CountDownLatch για ασύγχρονη εκτέλεση
        final Cursor[] cursor = new Cursor[1];
        CountDownLatch latch = new CountDownLatch(1);  //Χρήση του CountDownLatch για την αναμονή ολοκλήρωσης της ασύγχρονης εργασίας

        switch (uriMatcher.match(uri)) {
            case TASKS:
                //Αν το URI αντιστοιχεί σε όλα τα tasks
                executorService.execute(() -> {
                    try {
                        cursor[0] = taskDao.getAllTasksCursor();  //Ανάκτηση όλων των tasks
                    } catch (Exception e) {
                        Log.e("TaskContentProvider", "Error querying tasks", e);  //Καταγραφή σφάλματος σε περίπτωση αποτυχίας
                    } finally {
                        latch.countDown();  //Απελευθέρωση latch όταν ολοκληρωθεί
                    }
                });
                break;
            case TASK_ID:
                //Αν το URI αντιστοιχεί σε συγκεκριμένο task
                executorService.execute(() -> {
                    try {
                        cursor[0] = taskDao.getTaskByIdCursor((int) ContentUris.parseId(uri));  //Ανάκτηση task με ID
                    } catch (Exception e) {
                        Log.e("TaskContentProvider", "Error querying task by ID", e);  //Καταγραφή σφάλματος σε περίπτωση αποτυχίας
                    } finally {
                        latch.countDown(); //Απελευθέρωση latch όταν ολοκληρωθεί
                    }
                });
                break;
            default:
                //Αν το URI δεν αντιστοιχεί σε καμία από τις προηγούμενες περιπτώσεις
                throw new IllegalArgumentException("Unknown URI: " + uri);  //Εξαίρεση για άγνωστο URI
        }
        try {
            latch.await();  //Αναμονή ολοκλήρωσης ασύγχρονης εργασίας
        } catch (InterruptedException e) {
            Log.e("TaskContentProvider", "Interrupted while querying", e);  //Καταγραφή σφάλματος σε περίπτωση διακοπής
        }
        return cursor[0];  //Επιστροφή το Cursor με τα αποτελέσματα
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TASKS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + TASKS_TABLE;  //Επιστροφή του τύπου για πολλά tasks
            case TASK_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + TASKS_TABLE;  //Επιστροφή του τύπου για ένα μόνο task
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);  //Εξαίρεση για άγνωστο URI
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatcher.match(uri) == TASKS) {
            final long[] id = new long[1];
            CountDownLatch latch = new CountDownLatch(1);
            executorService.execute(() -> {
                try {
                    assert values != null;
                    id[0] = taskDao.insertContentTask(Task.fromContentValues(values));  //Εισαγωγή task
                    if (id[0] != -1) {
                        Status status = new Status(Status.RECORDED, (int) id[0]);
                        taskDao.insertStatus(status);
                    }
                } catch (Exception e) {
                    Log.e("TaskContentProvider", "Error inserting task or status", e);  //Καταγραφή σφάλματος σε περίπτωση αποτυχίας
                } finally {
                    latch.countDown();  //Απελευθέρωση latch όταν ολοκληρωθεί
                }
            });
            try {
                latch.await();  //Αναμονή για την ολοκλήρωση της εισαγωγής
            } catch (InterruptedException e) {
                Log.e("TaskContentProvider", "Interrupted while inserting", e);  //Καταγραφή σφάλματος σε περίπτωση διακοπής
            }
            return ContentUris.withAppendedId(CONTENT_URI, id[0]);  //Επιστροφή του URI του νέου task
        } else {
            throw new IllegalArgumentException("Invalid URI for insert operation: " + uri);  //Εξαίρεση για άκυρο URI
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        if (uriMatcher.match(uri) == TASK_ID) {
            final int[] rows = new int[1];
            final int taskId = (int) ContentUris.parseId(uri);
            CountDownLatch latch = new CountDownLatch(1);
            executorService.execute(() -> {
                try {
                    Task task = taskDao.getTaskById(taskId);  //Ανάκτηση του task με ID
                    if (task != null) {
                        //Ενημέρωση των πεδίων του task με τα νέα δεδομένα
                        assert values != null;
                        if (values.containsKey("shortName")) {
                            task.setShortName(values.getAsString("shortName"));
                        }
                        if (values.containsKey("duration")) {
                            task.setDuration(values.getAsInteger("duration"));
                        }
                        rows[0] = taskDao.updateTask(task);  //Ενημέρωση του task στη βάση
                    } else {
                        rows[0] = 0; //Δεν βρέθηκε task για ενημέρωση
                    }
                } catch (Exception e) {
                    Log.e("TaskContentProvider", "Error updating task", e);  //Καταγραφή σφάλματος σε περίπτωση αποτυχίας
                } finally {
                    latch.countDown(); //Απελευθέρωση latch όταν ολοκληρωθεί
                }
            });
            try {
                latch.await();  //Αναμονή για την ολοκλήρωση της ενημέρωσης
            } catch (InterruptedException e) {
                Log.e("TaskContentProvider", "Interrupted while updating", e);  //Καταγραφή σφάλματος σε περίπτωση αποτυχίας
            }
            return rows[0];  //Επιστροφή τον αριθμού των επηρεαζόμενων γραμμών
        } else {
            throw new IllegalArgumentException("Invalid URI for update operation: " + uri); //Εξαίρεση για άκυρο URI
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int[] rows = new int[1];
        CountDownLatch latch = new CountDownLatch(1);
        if (uriMatcher.match(uri) == TASK_ID) {
            executorService.execute(() -> {
                try {
                    rows[0] = taskDao.deleteTaskById((int) ContentUris.parseId(uri));  //Διαγραφή του task με το ID
                } catch (Exception e) {
                    Log.e("TaskContentProvider", "Error deleting task", e);  //Καταγραφή σφάλματος σε περίπτωση αποτυχίας
                } finally {
                    latch.countDown();  //Απελευθέρωση latch όταν ολοκληρωθεί
                }
            });
            try {
                latch.await();  //Αναμονή για την ολοκλήρωση της διαγραφής
            } catch (InterruptedException e) {
                Log.e("TaskContentProvider", "Interrupted while deleting", e);  //Καταγραφή σφάλματος σε περίπτωση αποτυχίας
            }
            return rows[0];   //Επιστροφή τον αριθμού των επηρεαζόμενων γραμμών
        } else {
            throw new IllegalArgumentException("Invalid URI for delete operation: " + uri);  //Εξαίρεση για άκυρο URI
        }
    }
}
