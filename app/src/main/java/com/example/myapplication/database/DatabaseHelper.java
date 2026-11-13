package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.model.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "task_manager.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    public static final String TABLE_TASKS = "tasks";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_DUE_DATE = "due_date";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_STATUS = "status";

    private static final String CREATE_TABLE_TASKS = "CREATE TABLE " + TABLE_TASKS + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_TITLE + " TEXT NOT NULL, "
            + COLUMN_DESCRIPTION + " TEXT, "
            + COLUMN_DUE_DATE + " TEXT, "
            + COLUMN_PRIORITY + " TEXT, "
            + COLUMN_STATUS + " INTEGER DEFAULT 0"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TASKS);
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    /**
     * Inserts realistic initial sample tasks so the app displays meaningful data right after install.
     */
    private void insertSampleData(SQLiteDatabase db) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DAY_OF_MONTH, 1);
        String tomorrow = sdf.format(cal.getTime());

        cal.add(Calendar.DAY_OF_MONTH, 3);
        String inFourDays = sdf.format(cal.getTime());

        cal.add(Calendar.DAY_OF_MONTH, 5);
        String inNineDays = sdf.format(cal.getTime());

        // Sample 1: Completed
        ContentValues cv1 = new ContentValues();
        cv1.put(COLUMN_TITLE, "Cài đặt môi trường Android Studio & SDK");
        cv1.put(COLUMN_DESCRIPTION, "Kiểm tra phiên bản JDK 17, thiết lập Emulator và cấu hình Gradle.");
        cv1.put(COLUMN_DUE_DATE, "10/11/2026");
        cv1.put(COLUMN_PRIORITY, Task.PRIORITY_MEDIUM);
        cv1.put(COLUMN_STATUS, Task.STATUS_COMPLETED);
        db.insert(TABLE_TASKS, null, cv1);

        // Sample 2: Pending, High priority
        ContentValues cv2 = new ContentValues();
        cv2.put(COLUMN_TITLE, "Hoàn thiện bài tập giữa kỳ môn Lập trình Di động");
        cv2.put(COLUMN_DESCRIPTION, "Xây dựng ứng dụng quản lý công việc cá nhân với SQLite và đầy đủ chức năng.");
        cv2.put(COLUMN_DUE_DATE, tomorrow);
        cv2.put(COLUMN_PRIORITY, Task.PRIORITY_HIGH);
        cv2.put(COLUMN_STATUS, Task.STATUS_PENDING);
        db.insert(TABLE_TASKS, null, cv2);

        // Sample 3: Pending, Medium priority
        ContentValues cv3 = new ContentValues();
        cv3.put(COLUMN_TITLE, "Ôn tập kiến thức Intent, SQLite và RecyclerView");
        cv3.put(COLUMN_DESCRIPTION, "Đọc lại slide bài giảng tuần 4 đến tuần 8 để chuẩn bị vấn đáp.");
        cv3.put(COLUMN_DUE_DATE, inFourDays);
        cv3.put(COLUMN_PRIORITY, Task.PRIORITY_MEDIUM);
        cv3.put(COLUMN_STATUS, Task.STATUS_PENDING);
        db.insert(TABLE_TASKS, null, cv3);

        // Sample 4: Pending, Low priority
        ContentValues cv4 = new ContentValues();
        cv4.put(COLUMN_TITLE, "Đăng ký đề tài bài tập lớn cuối kỳ");
        cv4.put(COLUMN_DESCRIPTION, "Gửi danh sách nhóm và tên đề tài cho giảng viên hướng dẫn.");
        cv4.put(COLUMN_DUE_DATE, inNineDays);
        cv4.put(COLUMN_PRIORITY, Task.PRIORITY_LOW);
        cv4.put(COLUMN_STATUS, Task.STATUS_PENDING);
        db.insert(TABLE_TASKS, null, cv4);
    }

    /**
     * Add a new task to SQLite.
     */
    public long insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, task.getTitle());
        cv.put(COLUMN_DESCRIPTION, task.getDescription());
        cv.put(COLUMN_DUE_DATE, task.getDueDate());
        cv.put(COLUMN_PRIORITY, task.getPriority());
        cv.put(COLUMN_STATUS, task.getStatus());

        long newId = db.insert(TABLE_TASKS, null, cv);
        db.close();
        return newId;
    }

    /**
     * Update task details in SQLite.
     */
    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE, task.getTitle());
        cv.put(COLUMN_DESCRIPTION, task.getDescription());
        cv.put(COLUMN_DUE_DATE, task.getDueDate());
        cv.put(COLUMN_PRIORITY, task.getPriority());
        cv.put(COLUMN_STATUS, task.getStatus());

        int rows = db.update(TABLE_TASKS, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
        return rows;
    }

    /**
     * Update task status (completed / pending).
     */
    public int updateTaskStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_STATUS, status);

        int rows = db.update(TABLE_TASKS, cv, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    /**
     * Delete a task by ID.
     */
    public int deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    /**
     * Get a task by ID.
     */
    public Task getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COLUMN_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);

        Task task = null;
        if (cursor != null && cursor.moveToFirst()) {
            task = cursorToTask(cursor);
            cursor.close();
        }
        db.close();
        return task;
    }

    /**
     * Get all tasks ordered by status (pending first) and ID descending.
     */
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + " ORDER BY " + COLUMN_STATUS + " ASC, " + COLUMN_ID + " DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                taskList.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }

    /**
     * Search and filter tasks by title keyword, status, and priority.
     *
     * @param keyword        text search for task title
     * @param statusFilter   "Tất cả", "Chưa thực hiện", "Đã hoàn thành"
     * @param priorityFilter "Tất cả", "Cao", "Trung bình", "Thấp"
     */
    public List<Task> searchAndFilterTasks(String keyword, String statusFilter, String priorityFilter) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        StringBuilder query = new StringBuilder("SELECT * FROM " + TABLE_TASKS + " WHERE 1=1");
        List<String> argsList = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            query.append(" AND ").append(COLUMN_TITLE).append(" LIKE ?");
            argsList.add("%" + keyword.trim() + "%");
        }

        if (statusFilter != null && !statusFilter.equals("Tất cả") && !statusFilter.equals("Tất cả trạng thái")) {
            query.append(" AND ").append(COLUMN_STATUS).append(" = ?");
            if (statusFilter.equals("Đã hoàn thành")) {
                argsList.add(String.valueOf(Task.STATUS_COMPLETED));
            } else {
                argsList.add(String.valueOf(Task.STATUS_PENDING));
            }
        }

        if (priorityFilter != null && !priorityFilter.equals("Tất cả") && !priorityFilter.equals("Tất cả mức độ ưu tiên")) {
            query.append(" AND ").append(COLUMN_PRIORITY).append(" = ?");
            argsList.add(priorityFilter);
        }

        query.append(" ORDER BY ").append(COLUMN_STATUS).append(" ASC, ").append(COLUMN_ID).append(" DESC");

        String[] args = argsList.toArray(new String[0]);
        Cursor cursor = db.rawQuery(query.toString(), args);

        if (cursor.moveToFirst()) {
            do {
                taskList.add(cursorToTask(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }

    /**
     * Count total tasks.
     */
    public int getTotalTasksCount() {
        return getCount("SELECT COUNT(*) FROM " + TABLE_TASKS);
    }

    /**
     * Count completed tasks.
     */
    public int getCompletedTasksCount() {
        return getCount("SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE " + COLUMN_STATUS + " = " + Task.STATUS_COMPLETED);
    }

    /**
     * Count pending tasks.
     */
    public int getPendingTasksCount() {
        return getCount("SELECT COUNT(*) FROM " + TABLE_TASKS + " WHERE " + COLUMN_STATUS + " = " + Task.STATUS_PENDING);
    }

    private int getCount(String sql) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    private Task cursorToTask(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
        String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DUE_DATE));
        String priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY));
        int status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS));

        return new Task(id, title, description, dueDate, priority, status);
    }
}
