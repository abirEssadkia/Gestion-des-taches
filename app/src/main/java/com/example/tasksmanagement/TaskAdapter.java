package com.example.tasksmanagement;
import android.widget.ImageView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private List<Task> tasks;

    public TaskAdapter(Context context, List<Task> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return tasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        }

        Task task = tasks.get(position);

        CheckBox selectBox = convertView.findViewById(R.id.task_select);
        TextView title = convertView.findViewById(R.id.task_title);
        ImageView statusIcon = convertView.findViewById(R.id.task_status_icon);

        // Afficher le titre
        title.setText(task.getTitle());

        // Définir l’état de la case à cocher
        selectBox.setChecked(task.isSelected());

        // Mettre à jour le champ `isSelected` lorsque l'utilisateur coche ou décoche
        selectBox.setOnCheckedChangeListener((buttonView, isChecked) -> task.setSelected(isChecked));

        // Définir l'icône en fonction du statut terminé ou non
        if (task.isCompleted()) {
            statusIcon.setImageResource(R.drawable.ic_done); // Icône pour "terminé"
        } else {
            statusIcon.setImageResource(R.drawable.ic_not_done); // Icône pour "non terminé"
        }

        // Gérer le clic sur l'icône pour basculer le statut terminé/non terminé
        statusIcon.setOnClickListener(v -> {
            task.setCompleted(!task.isCompleted()); // Basculer l'état terminé/non terminé
            notifyDataSetChanged(); // Actualiser la vue
        });

        return convertView;
    }



}
