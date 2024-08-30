// TODO: 2023/11/29 teacher类我还没想好要不要加，不要的话删掉就好。
// TODO: 2023/11/29 我有一个绝妙的想法，学生表可以被多个用户编辑，只需要做出限制即可。

package com.zjgsu.studentmanagement.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class myDatabaseHelper extends SQLiteOpenHelper {
    private static myDatabaseHelper instance;
    public static final String CREATE_ADMIN = "create table " +
            "admin(id integer primary key autoincrement, name text, password text)";

    public static final String CREATE_TEACHER = "create table " +
      "teacher(id text primary key, name text, password text)";
    public static final String CREATE_STUDENT = "create table " +
            "student(id text primary key," +
            "name text, password text, sex text, number text," +
            "mathScore integer, chineseScore integer, englishScore integer)";

    private myDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ADMIN);
        /*db.execSQL(CREATE_TEACHER);*/
        db.execSQL(CREATE_STUDENT);
        db.execSQL("alter table student add column ranking integer");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int i1) {

        if (oldVersion == 1) {
            db.execSQL("alter table student add column ranking integer");
        }
    }

    public static myDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new myDatabaseHelper(context, "StudentManagement.db", null, 2);
        }
        return instance;

    }
}
