package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Task;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskClickListener {
        void onStatusToggle(Task task, boolean isCompleted);
        void onEditClick(Task task);
        void onDeleteClick(Task task);
        void onItemClick(Task task);
    }

    private final Context context;
    private List<Task> taskList = new ArrayList<>();
    private final OnTaskClickListener listener;

    public TaskAdapter(Context context, OnTaskClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.taskList = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardTask;
        TextView tvTaskId, tvPriorityBadge, tvStatusBadge;
        CheckBox cbStatus;
        TextView tvTitle, tvDescription, tvDueDate;
        ImageButton btnEdit, btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTask = itemView.findViewById(R.id.cardTask);
            tvTaskId = itemView.findViewById(R.id.tvTaskId);
            tvPriorityBadge = itemView.findViewById(R.id.tvPriorityBadge);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            cbStatus = itemView.findViewById(R.id.cbStatus);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDueDate = itemView.findViewById(R.id.tvDueDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Task task) {
            // Task ID
            tvTaskId.setText(String.format(context.getString(R.string.label_task_id), task.getId()));

            // Task Title
            tvTitle.setText(task.getTitle());

            // Task Description (hide if empty)
            if (task.getDescription() != null && !task.getDescription().trim().isEmpty()) {
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(task.getDescription());
            } else {
                tvDescription.setVisibility(View.GONE);
            }

            // Due Date
            if (task.getDueDate() != null && !task.getDueDate().trim().isEmpty()) {
                tvDueDate.setVisibility(View.VISIBLE);
                tvDueDate.setText(String.format("Hạn: %s", task.getDueDate()));
            } else {
                tvDueDate.setVisibility(View.GONE);
            }

            // Priority badge styling
            String priority = task.getPriority();
            tvPriorityBadge.setText(priority);
            if (Task.PRIORITY_HIGH.equalsIgnoreCase(priority)) {
                tvPriorityBadge.setBackgroundResource(R.drawable.bg_priority_high);
                tvPriorityBadge.setTextColor(ContextCompat.getColor(context, R.color.priority_high_text));
            } else if (Task.PRIORITY_MEDIUM.equalsIgnoreCase(priority)) {
                tvPriorityBadge.setBackgroundResource(R.drawable.bg_priority_medium);
                tvPriorityBadge.setTextColor(ContextCompat.getColor(context, R.color.priority_medium_text));
            } else {
                tvPriorityBadge.setBackgroundResource(R.drawable.bg_priority_low);
                tvPriorityBadge.setTextColor(ContextCompat.getColor(context, R.color.priority_low_text));
            }

            // Status badge and CheckBox
            boolean isCompleted = task.isCompleted();
            // Clear listener before setChecked to prevent unexpected callback loop
            cbStatus.setOnCheckedChangeListener(null);
            cbStatus.setChecked(isCompleted);

            if (isCompleted) {
                tvStatusBadge.setText(context.getString(R.string.status_completed));
                tvStatusBadge.setBackgroundResource(R.drawable.bg_status_completed);
                tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_completed_text));
                // Strike-through effect on completed task title
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_disabled));
                cardTask.setAlpha(0.85f);
            } else {
                tvStatusBadge.setText(context.getString(R.string.status_pending));
                tvStatusBadge.setBackgroundResource(R.drawable.bg_status_pending);
                tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_pending_text));
                // Remove strike-through
                tvTitle.setPaintFlags(tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
                cardTask.setAlpha(1.0f);
            }

            // Listeners
            cbStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onStatusToggle(task, isChecked);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(task);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(task);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(task);
                }
            });
        }
    }
}
