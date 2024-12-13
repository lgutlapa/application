
package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.util.Log;

import com.example.todolist.db.DatabaseHelper;
import com.example.todolist.db.TaskSchema;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ListView listView;
    private static final String LOG_TAG = "ToDoListApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        listView = findViewById(R.id.todo_list);

        refreshTaskList();
    }

    private void refreshTaskList() {
        ArrayList<String> tasks = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskSchema.TaskTable.TABLE_NAME, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String task = cursor.getString(cursor.getColumnIndex(TaskSchema.TaskTable.COLUMN_TASK_NAME));
            tasks.add(task);
        }

        if (adapter == null) {
            adapter = new ArrayAdapter<>(this, R.layout.list_item_task, R.id.task_name, tasks);
            listView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(tasks);
            adapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_task) {
            showAddTaskDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddTaskDialog() {
        final EditText input = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("New Task")
                .setMessage("Enter your task:")
                .setView(input)
                .setPositiveButton("Add", (dialog, which) -> {
                    String task = input.getText().toString();
                    addTaskToDatabase(task);
                    refreshTaskList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addTaskToDatabase(String task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskSchema.TaskTable.COLUMN_TASK_NAME, task);
        db.insert(TaskSchema.TaskTable.TABLE_NAME, null, values);
        db.close();
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        EditText taskView = parent.findViewById(R.id.task_name);
        String task = taskView.getText().toString();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TaskSchema.TaskTable.TABLE_NAME, TaskSchema.TaskTable.COLUMN_TASK_NAME + "=?", new String[]{task});
        db.close();

        refreshTaskList();
    }
}
