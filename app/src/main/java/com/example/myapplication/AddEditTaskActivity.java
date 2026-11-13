package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.database.DatabaseHelper;
import com.example.myapplication.model.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK = "TASK_DATA";

    private MaterialToolbar toolbar;
    private LinearLayout layoutTaskId;
    private TextView tvEditTaskId;
    private TextInputLayout tilTitle, tilDescription, tilDueDate;
    private TextInputEditText edtTitle, edtDescription, edtDueDate;
    private RadioGroup rgPriority, rgStatus;
    private RadioButton rbPriorityLow, rbPriorityMedium, rbPriorityHigh;
    private RadioButton rbStatusPending, rbStatusCompleted;
    private MaterialButton btnSave, btnCancel;

    private DatabaseHelper databaseHelper;
    private Task existingTask = null;
    private boolean isEditMode = false;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_task);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addEditRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseHelper = new DatabaseHelper(this);
        initViews();
        checkIntentData();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbarAddEdit);
        layoutTaskId = findViewById(R.id.layoutTaskId);
        tvEditTaskId = findViewById(R.id.tvEditTaskId);
        tilTitle = findViewById(R.id.tilTitle);
        tilDescription = findViewById(R.id.tilDescription);
        tilDueDate = findViewById(R.id.tilDueDate);
        edtTitle = findViewById(R.id.edtTitle);
        edtDescription = findViewById(R.id.edtDescription);
        edtDueDate = findViewById(R.id.edtDueDate);
        rgPriority = findViewById(R.id.rgPriority);
        rgStatus = findViewById(R.id.rgStatus);
        rbPriorityLow = findViewById(R.id.rbPriorityLow);
        rbPriorityMedium = findViewById(R.id.rbPriorityMedium);
        rbPriorityHigh = findViewById(R.id.rbPriorityHigh);
        rbStatusPending = findViewById(R.id.rbStatusPending);
        rbStatusCompleted = findViewById(R.id.rbStatusCompleted);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void checkIntentData() {
        if (getIntent().hasExtra(EXTRA_TASK)) {
            existingTask = (Task) getIntent().getSerializableExtra(EXTRA_TASK);
        }

        if (existingTask != null) {
            isEditMode = true;
            toolbar.setTitle(R.string.title_edit_task);
            btnSave.setText(R.string.action_update);

            layoutTaskId.setVisibility(View.VISIBLE);
            tvEditTaskId.setText(String.format("#%d", existingTask.getId()));

            edtTitle.setText(existingTask.getTitle());
            edtDescription.setText(existingTask.getDescription());
            edtDueDate.setText(existingTask.getDueDate());

            // Set priority
            String priority = existingTask.getPriority();
            if (Task.PRIORITY_HIGH.equalsIgnoreCase(priority)) {
                rbPriorityHigh.setChecked(true);
            } else if (Task.PRIORITY_LOW.equalsIgnoreCase(priority)) {
                rbPriorityLow.setChecked(true);
            } else {
                rbPriorityMedium.setChecked(true);
            }

            // Set status
            if (existingTask.isCompleted()) {
                rbStatusCompleted.setChecked(true);
            } else {
                rbStatusPending.setChecked(true);
            }
        } else {
            isEditMode = false;
            toolbar.setTitle(R.string.title_add_task);
            btnSave.setText(R.string.action_save);
            layoutTaskId.setVisibility(View.GONE);

            // Default due date: tomorrow
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_MONTH, 1);
            edtDueDate.setText(sdf.format(tomorrow.getTime()));
        }
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());

        // DatePicker dialog on clicking due date field
        View.OnClickListener dateClickListener = v -> showDatePickerDialog();
        edtDueDate.setOnClickListener(dateClickListener);
        tilDueDate.setEndIconOnClickListener(dateClickListener);

        // Clear error text on user input
        edtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    tilTitle.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Save button
        btnSave.setOnClickListener(v -> saveTask());
    }

    private void showDatePickerDialog() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    edtDueDate.setText(sdf.format(calendar.getTime()));
                }, year, month, day);

        datePickerDialog.show();
    }

    private void saveTask() {
        String title = edtTitle.getText() != null ? edtTitle.getText().toString().trim() : "";
        String description = edtDescription.getText() != null ? edtDescription.getText().toString().trim() : "";
        String dueDate = edtDueDate.getText() != null ? edtDueDate.getText().toString().trim() : "";

        // Validation: Title cannot be empty
        if (title.isEmpty()) {
            tilTitle.setError(getString(R.string.error_empty_title));
            edtTitle.requestFocus();
            return;
        }

        // Priority
        String priority;
        int checkedPriorityId = rgPriority.getCheckedRadioButtonId();
        if (checkedPriorityId == R.id.rbPriorityHigh) {
            priority = Task.PRIORITY_HIGH;
        } else if (checkedPriorityId == R.id.rbPriorityLow) {
            priority = Task.PRIORITY_LOW;
        } else {
            priority = Task.PRIORITY_MEDIUM;
        }

        // Status
        int status = (rgStatus.getCheckedRadioButtonId() == R.id.rbStatusCompleted)
                ? Task.STATUS_COMPLETED : Task.STATUS_PENDING;

        if (isEditMode && existingTask != null) {
            existingTask.setTitle(title);
            existingTask.setDescription(description);
            existingTask.setDueDate(dueDate);
            existingTask.setPriority(priority);
            existingTask.setStatus(status);

            int result = databaseHelper.updateTask(existingTask);
            if (result > 0) {
                Toast.makeText(this, R.string.msg_task_updated, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Có lỗi xảy ra khi cập nhật!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Task newTask = new Task(title, description, dueDate, priority, status);
            long newId = databaseHelper.insertTask(newTask);
            if (newId > 0) {
                Toast.makeText(this, R.string.msg_task_added, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Có lỗi xảy ra khi thêm công việc!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
