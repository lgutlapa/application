
package com.example.todolist.db;

import android.provider.BaseColumns;

public class TaskSchema {
    public static final class TaskTable implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TASK_NAME = "name";
    }
}
