package com.example.tasksmanagement;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Task> tasks; // Liste principale des tâches
    private List<Task> filteredTasks; // Liste filtrée des tâches
    private TaskAdapter adapter; // Adaptateur pour la ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation de la liste principale des tâches
        tasks = new ArrayList<>();
        tasks.add(new Task("Apprendre Android", "Cours en ligne", "2023-12-01", false));
        tasks.add(new Task("Achever le projet", "Finaliser le code", "2023-11-30", true));
        tasks.add(new Task("Envoyer le rapport", "Soumettre avant deadline", "2023-12-05", false));

        // Initialisation de la liste filtrée (par défaut, afficher toutes les tâches)
        filteredTasks = new ArrayList<>(tasks);

        // Configuration de l'adaptateur pour la liste filtrée
        adapter = new TaskAdapter(this, filteredTasks);
        ListView taskListView = findViewById(R.id.task_list);
        taskListView.setAdapter(adapter);

        // Bouton pour modifier une tâche
        Button editButton = findViewById(R.id.edit_button);
        editButton.setOnClickListener(v -> {
            for (Task task : tasks) {
                if (task.isSelected()) {
                    showEditTaskDialog(task);
                    return;
                }
            }
            Toast.makeText(this, "Veuillez sélectionner une tâche à modifier.", Toast.LENGTH_SHORT).show();
        });

        // Configuration du Spinner pour gérer le filtre
        Spinner filterSpinner = findViewById(R.id.filter_spinner);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filter = parent.getItemAtPosition(position).toString();
                applyFilter(filter); // Appliquer le filtre sélectionné
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                applyFilter("Toutes les tâches");
            }
        });

        // Bouton pour ajouter une tâche
        Button addButton = findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> showAddTaskDialog());

        // Bouton pour supprimer les tâches sélectionnées
        Button deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> deleteSelectedTasks());

        // Gestion des Insets pour adapter la vue à l'écran
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Appliquer le filtre par défaut au démarrage
        applyFilter("Toutes les tâches");
    }

    // Méthode pour ajouter une nouvelle tâche à la liste principale
    private void addTask(String taskTitle, String description, String dueDate) {
        if (!taskTitle.isEmpty() && !description.isEmpty() && !dueDate.isEmpty()) {
            tasks.add(new Task(taskTitle, description, dueDate, false)); // Ajouter une nouvelle tâche
            applyFilter("Toutes les tâches"); // Réappliquer le filtre actuel
        } else {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
        }
    }

    // Méthode pour afficher une boîte de dialogue d'ajout de tâche
    private void showAddTaskDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Ajouter une tâche");

        // Charger le layout pour la boîte de dialogue
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_task, null);
        builder.setView(dialogView);

        EditText titleInput = dialogView.findViewById(R.id.edit_task_title);
        EditText descriptionInput = dialogView.findViewById(R.id.edit_task_description);
        EditText dueDateInput = dialogView.findViewById(R.id.edit_task_due_date);

        // Boutons de confirmation et d'annulation
        builder.setPositiveButton("Ajouter", (dialog, which) -> {
            String taskTitle = titleInput.getText().toString();
            String description = descriptionInput.getText().toString();
            String dueDate = dueDateInput.getText().toString();
            addTask(taskTitle, description, dueDate);
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Méthode pour supprimer les tâches sélectionnées
    private void deleteSelectedTasks() {
        tasks.removeIf(Task::isSelected);
        applyFilter("Toutes les tâches");
    }

    // Méthode pour appliquer le filtre sur les tâches
    private void applyFilter(String filter) {
        filteredTasks.clear();

        switch (filter) {
            case "Tâches terminées":
                for (Task task : tasks) {
                    if (task.isCompleted()) {
                        filteredTasks.add(task);
                    }
                }
                break;

            case "Tâches non terminées":
                for (Task task : tasks) {
                    if (!task.isCompleted()) {
                        filteredTasks.add(task);
                    }
                }
                break;

            default:
                filteredTasks.addAll(tasks);
                break;
        }

        adapter.notifyDataSetChanged();
    }

    // Méthode pour afficher une boîte de dialogue de modification de tâche
    private void showEditTaskDialog(Task task) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Modifier la tâche");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_task, null);
        builder.setView(dialogView);

        EditText titleInput = dialogView.findViewById(R.id.edit_task_title);
        EditText descriptionInput = dialogView.findViewById(R.id.edit_task_description);
        EditText dueDateInput = dialogView.findViewById(R.id.edit_task_due_date);

        titleInput.setText(task.getTitle());
        descriptionInput.setText(task.getDescription());
        dueDateInput.setText(task.getDueDate());

        builder.setPositiveButton("Modifier", (dialog, which) -> {
            task.setTitle(titleInput.getText().toString());
            task.setDescription(descriptionInput.getText().toString());
            task.setDueDate(dueDateInput.getText().toString());
            adapter.notifyDataSetChanged();
        });

        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
