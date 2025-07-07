package com.example.calendar.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calendar.Entities.Status;
import com.example.calendar.Entities.Task;
import com.example.calendar.R;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final Context context; //Το context της δραστηριότητας
    private final List<Task> taskList; //Λίστα με τα tasks
    private final List<Status> statusList; //Λίστα με τα status
    private final OnItemClickListener listener; //Listener για τα κλικ στα items


    public TaskAdapter(Context context, List<Task> taskList, List<Status> statusList, OnItemClickListener listener) { //Κατασκευαστής του adapter
        this.context = context;
        this.taskList = taskList;
        this.statusList = statusList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Δημιουργία του view από το layout του item
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        //Ανάκτηση του task και του status για τη συγκεκριμένη θέση
        Task task = taskList.get(position);
        Status status = statusList.get(position);
        holder.tvTaskId.setText(String.valueOf(task.getUid())); //Εμφάνιση του ID του task
        holder.tvShortName.setText(task.getShortName()); //Εμφάνιση του short name
        holder.tvStatus.setText(status.getStatusId()); //Εμφάνιση του status ID

        //Ορισμός listener για το κλικ στο item
        holder.itemView.setOnClickListener(v -> listener.onItemClick(task));
    }

    @Override
    public int getItemCount() {
        //Επιστροφή του αριθμού των items στη λίστα
        return taskList.size();
    }

    //Ορισμός interface για τα κλικ στα items
    public interface OnItemClickListener {
        void onItemClick(Task task); //Μέθοδος που καλείται όταν γίνεται κλικ σε item
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder { //ViewHolder κλάση για την αποθήκευση των views

        TextView tvTaskId, tvShortName, tvStatus; //Views για εμφάνιση των δεδομένων

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskId = itemView.findViewById(R.id.TaskIdvalue); //Ανάκτηση του TextView για το ID του task
            tvShortName = itemView.findViewById(R.id.ShortNameValue); //Ανάκτηση του TextView για το short name
            tvStatus = itemView.findViewById(R.id.StatusValue); //Ανάκτηση του TextView για το status
        }
    }
}
