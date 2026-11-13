package com.example.myapplication;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.TaskAdapter;
import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {

    // Views
    private TextView tvStatTotal, tvStatCompleted, tvStatPending, tvProgressPercent;
    private ProgressBar progressBarTasks;
    private EditText edtSearch;
    private ImageButton btnClearSearch;
    private Spinner spinnerFilterStatus, spinnerFilterPriority;
    private RecyclerView rvTasks;
    private LinearLayout layoutEmptyState;
    private FloatingActionButton fabAddTask;

    // Database & Adapter
    private DatabaseHelper databaseHelper;
    private TaskAdapter taskAdapter;
    private final List<Task> currentTaskList = new ArrayList<>();

    // Activity Result Launcher for Add/Edit Task
    private final ActivityResultLauncher<Intent> taskActivityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    refreshData();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);
        initViews();
        setupRecyclerView();
        setupSpinners();
        setupSearchAndFilters();
        setupActions();

        // Initial data load
        refreshData();
    }

    private void initViews() {
        tvStatTotal = findViewById(R.id.tvStatTotal);
        tvStatCompleted = findViewById(R.id.tvStatCompleted);
        tvStatPending = findViewById(R.id.tvStatPending);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        progressBarTasks = findViewById(R.id.progressBarTasks);

        edtSearch = findViewById(R.id.edtSearch);
        btnClearSearch = findViewById(R.id.btnClearSearch);
        spinnerFilterStatus = findViewById(R.id.spinnerFilterStatus);
        spinnerFilterPriority = findViewById(R.id.spinnerFilterPriority);

        rvTasks = findViewById(R.id.rvTasks);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        fabAddTask = findViewById(R.id.fabAddTask);
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(this, this);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);
    }

    private void setupSpinners() {
        // Status filter items
        String[] statusOptions = new String[]{
                getString(R.string.filter_all_status),
                getString(R.string.status_pending),
                getString(R.string.status_completed)
        };
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, statusOptions);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterStatus.setAdapter(statusAdapter);

        // Priority filter items
        String[] priorityOptions = new String[]{
                getString(R.string.filter_all_priority),
                getString(R.string.priority_high),
                getString(R.string.priority_medium),
                getString(R.string.priority_low)
        };
        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, priorityOptions);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilterPriority.setAdapter(priorityAdapter);
    }

    private void setupSearchAndFilters() {
        // Search text watcher
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                applyFilterAndSearch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Clear search button
        btnClearSearch.setOnClickListener(v -> edtSearch.setText(""));

        // Status Spinner listener
        spinnerFilterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilterAndSearch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Priority Spinner listener
        spinnerFilterPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilterAndSearch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupActions() {
        // FAB to add new task
        fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
            taskActivityResultLauncher.launch(intent);
        });
    }

    /**
     * Refreshes both statistics and the task list according to current filters.
     */
    private void refreshData() {
        updateStatistics();
        applyFilterAndSearch();
    }

    /**
     * Calculates and updates total, completed, pending counts and progress bar.
     */
    private void updateStatistics() {
        int total = databaseHelper.getTotalTasksCount();
        int completed = databaseHelper.getCompletedTasksCount();
        int pending = databaseHelper.getPendingTasksCount();

        tvStatTotal.setText(String.valueOf(total));
        tvStatCompleted.setText(String.valueOf(completed));
        tvStatPending.setText(String.valueOf(pending));

        int percent = total > 0 ? (int) Math.round(((double) completed / total) * 100) : 0;
        progressBarTasks.setProgress(percent);
        tvProgressPercent.setText(String.format("%d%%", percent));
    }

    /**
     * Queries database with current search keyword, status filter, and priority filter.
     */
    private void applyFilterAndSearch() {
        String keyword = edtSearch.getText() != null ? edtSearch.getText().toString().trim() : "";
        String statusFilter = spinnerFilterStatus.getSelectedItem() != null
                ? spinnerFilterStatus.getSelectedItem().toString() : getString(R.string.filter_all_status);
        String priorityFilter = spinnerFilterPriority.getSelectedItem() != null
                ? spinnerFilterPriority.getSelectedItem().toString() : getString(R.string.filter_all_priority);

        List<Task> filtered = databaseHelper.searchAndFilterTasks(keyword, statusFilter, priorityFilter);
        currentTaskList.clear();
        currentTaskList.addAll(filtered);

        taskAdapter.setTasks(currentTaskList);

        // Show/hide empty state
        if (currentTaskList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvTasks.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvTasks.setVisibility(View.VISIBLE);
        }
    }

    // ==========================================
    // TaskAdapter.OnTaskClickListener Callbacks
    // ==========================================

    @Override
    public void onStatusToggle(Task task, boolean isCompleted) {
        int newStatus = isCompleted ? Task.STATUS_COMPLETED : Task.STATUS_PENDING;
        task.setStatus(newStatus);
        databaseHelper.updateTaskStatus(task.getId(), newStatus);

        Toast.makeText(this, isCompleted ? "Đã đánh dấu hoàn thành!" : "Đã chuyển về Chưa thực hiện",
                Toast.LENGTH_SHORT).show();

        // Refresh stats and re-filter list
        refreshData();
    }

    @Override
    public void onEditClick(Task task) {
        openEditScreen(task);
    }

    @Override
    public void onDeleteClick(Task task) {
        confirmDeleteTask(task);
    }

    @Override
    public void onItemClick(Task task) {
        showTaskDetailDialog(task);
    }

    private void openEditScreen(Task task) {
        Intent intent = new Intent(MainActivity.this, AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskActivity.EXTRA_TASK, task);
        taskActivityResultLauncher.launch(intent);
    }

    private void confirmDeleteTask(Task task) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(String.format(getString(R.string.dialog_delete_message), task.getTitle()))
                .setPositiveButton(R.string.dialog_btn_delete, (dialog, which) -> {
                    databaseHelper.deleteTask(task.getId());
                    Toast.makeText(MainActivity.this, R.string.msg_task_deleted, Toast.LENGTH_SHORT).show();
                    refreshData();
                })
                .setNegativeButton(R.string.dialog_btn_cancel, null)
                .show();
    }

    private void showTaskDetailDialog(Task task) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_task_detail);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        TextView tvDetailTaskId = dialog.findViewById(R.id.tvDetailTaskId);
        TextView tvDetailPriority = dialog.findViewById(R.id.tvDetailPriority);
        TextView tvDetailStatus = dialog.findViewById(R.id.tvDetailStatus);
        TextView tvDetailTitle = dialog.findViewById(R.id.tvDetailTitle);
        TextView tvDetailDueDate = dialog.findViewById(R.id.tvDetailDueDate);
        TextView tvDetailDescription = dialog.findViewById(R.id.tvDetailDescription);
        MaterialButton btnDetailToggleStatus = dialog.findViewById(R.id.btnDetailToggleStatus);
        MaterialButton btnDetailEdit = dialog.findViewById(R.id.btnDetailEdit);
        MaterialButton btnDetailClose = dialog.findViewById(R.id.btnDetailClose);

        tvDetailTaskId.setText(String.format(getString(R.string.label_task_id), task.getId()));
        tvDetailTitle.setText(task.getTitle());

        if (task.getDescription() != null && !task.getDescription().trim().isEmpty()) {
            tvDetailDescription.setText(task.getDescription());
        } else {
            tvDetailDescription.setText("(Không có mô tả)");
        }

        if (task.getDueDate() != null && !task.getDueDate().trim().isEmpty()) {
            tvDetailDueDate.setText(String.format("Ngày hết hạn: %s", task.getDueDate()));
        } else {
            tvDetailDueDate.setText("Ngày hết hạn: Chưa xác định");
        }

        // Priority
        tvDetailPriority.setText(task.getPriority());
        if (Task.PRIORITY_HIGH.equalsIgnoreCase(task.getPriority())) {
            tvDetailPriority.setBackgroundResource(R.drawable.bg_priority_high);
            tvDetailPriority.setTextColor(ContextCompat.getColor(this, R.color.priority_high_text));
        } else if (Task.PRIORITY_MEDIUM.equalsIgnoreCase(task.getPriority())) {
            tvDetailPriority.setBackgroundResource(R.drawable.bg_priority_medium);
            tvDetailPriority.setTextColor(ContextCompat.getColor(this, R.color.priority_medium_text));
        } else {
            tvDetailPriority.setBackgroundResource(R.drawable.bg_priority_low);
            tvDetailPriority.setTextColor(ContextCompat.getColor(this, R.color.priority_low_text));
        }

        // Status
        if (task.isCompleted()) {
            tvDetailStatus.setText(getString(R.string.status_completed));
            tvDetailStatus.setBackgroundResource(R.drawable.bg_status_completed);
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, R.color.status_completed_text));
            btnDetailToggleStatus.setText("Đánh dấu chưa xong");
        } else {
            tvDetailStatus.setText(getString(R.string.status_pending));
            tvDetailStatus.setBackgroundResource(R.drawable.bg_status_pending);
            tvDetailStatus.setTextColor(ContextCompat.getColor(this, R.color.status_pending_text));
            btnDetailToggleStatus.setText("Đánh dấu hoàn thành");
        }

        // Dialog buttons
        btnDetailToggleStatus.setOnClickListener(v -> {
            boolean newCompleted = !task.isCompleted();
            int newStatus = newCompleted ? Task.STATUS_COMPLETED : Task.STATUS_PENDING;
            task.setStatus(newStatus);
            databaseHelper.updateTaskStatus(task.getId(), newStatus);
            refreshData();
            dialog.dismiss();
            Toast.makeText(MainActivity.this, R.string.msg_status_updated, Toast.LENGTH_SHORT).show();
        });

        btnDetailEdit.setOnClickListener(v -> {
            dialog.dismiss();
            openEditScreen(task);
        });

        btnDetailClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
