package com.zjgsu.studentmanagement.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zjgsu.studentmanagement.R;
import com.zjgsu.studentmanagement.Util.myDatabaseHelper;

public class studentActivity extends AppCompatActivity {
    private String ID;
    private Intent intent;
    private myDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.student_layout);

        Button select = findViewById(R.id.student_activity_selectInfo);
        Button changePassword = findViewById(R.id.student_activity_changePassword);
        dbHelper = myDatabaseHelper.getInstance(this);
        intent = getIntent();

        select.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(studentActivity.this);
            builder.setTitle("个人信息");
            ID = intent.getStringExtra("id");
            StringBuilder sb = new StringBuilder();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from student where id=?", new String[]{ID});
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                String number = cursor.getString(cursor.getColumnIndexOrThrow("number"));
                int mathScore = cursor.getInt(cursor.getColumnIndexOrThrow("mathScore"));
                int chineseScore = cursor.getInt(cursor.getColumnIndexOrThrow("chineseScore"));
                int englishScore = cursor.getInt(cursor.getColumnIndexOrThrow("englishScore"));
                int ranking = cursor.getInt(cursor.getColumnIndexOrThrow("ranking"));
                sb.append("姓名：").append(name).append("\n");
                sb.append("学号：").append(id).append("\n");
                sb.append("手机号：").append(number).append("\n");
                sb.append("密码：").append(password).append("\n");
                sb.append("数学成绩：").append(mathScore).append("\n");
                sb.append("语文成绩：").append(chineseScore).append("\n");
                sb.append("英语成绩：").append(englishScore).append("\n");
                int sum = mathScore + chineseScore + englishScore;
                sb.append("总成绩：").append(sum).append("\n");
                sb.append("名次：").append(ranking).append("\n");
            }
            cursor.close();
            builder.setMessage(sb.toString());
            builder.create().show();
        });

        changePassword.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(studentActivity.this);
            LayoutInflater factory = LayoutInflater.from(studentActivity.this);
            final View view = factory.inflate(R.layout.change_password_layout, null);
            builder.setView(view);
            builder.setTitle("修改密码");
            builder.setNegativeButton("取消", null);

            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                final EditText firstPassword = view.findViewById(R.id.student_change_password);
                final EditText secondPassword = view.findViewById(R.id.student_change_password_second_password);

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String first = firstPassword.getText().toString();
                    String second = secondPassword.getText().toString();
                    if (!TextUtils.isEmpty(first) && !TextUtils.isEmpty(second)) {
                        if (first.matches("[0-9]{6}") && second.matches("[0-9]{6}")) {
                            if (second.equals(first)) {
                                ID = intent.getStringExtra("id");//获取传入的学号用于修改密码
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                db.execSQL("update student set password=? where id=?", new String[]{second, ID});
                                Toast.makeText(studentActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(studentActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(studentActivity.this, "密码必须为6位数字", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(studentActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.create().show();
        });
    }
}
