package com.zjgsu.studentmanagement.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zjgsu.studentmanagement.R;
import com.zjgsu.studentmanagement.Util.myDatabaseHelper;

public class student_login_activity extends AppCompatActivity {
    private EditText name;
    private EditText password;
    private myDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_login_layout);
        name = findViewById(R.id.student_login_activity_name_input);
        password = findViewById(R.id.student_login_activity_password_input);
        Button login = findViewById(R.id.student_login_activity_login);
        TextView register = findViewById(R.id.student_login_activity_register);
        TextView forgetNum = findViewById(R.id.student_login_activity_forgetNum);

        dbHelper = myDatabaseHelper.getInstance(this);

        login.setOnClickListener(view -> {
            String studentId = name.getText().toString();
            String studentPassword = password.getText().toString();

            if (!TextUtils.isEmpty(studentId) && !TextUtils.isEmpty(studentPassword)) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                Cursor cursor = db.rawQuery("select password from student where id=?", new String[]{studentId});
                if (cursor.moveToNext()) {
                    String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                    if (password.equals(studentPassword)) {
                        Intent intent = new Intent(student_login_activity.this, studentActivity.class);
                        intent.putExtra("id", name.getText().toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(student_login_activity.this, "密码错误请重新输入", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(student_login_activity.this, "该学号未注册，请联系老师", Toast.LENGTH_SHORT).show();
                }
                cursor.close();
            }
        });

        register.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(student_login_activity.this);
            LayoutInflater factory = LayoutInflater.from(student_login_activity.this);
            final View textEntryView = factory.inflate(R.layout.student_register_layout, null);
            builder.setTitle("学生注册");
            builder.setView(textEntryView);

            builder.setNegativeButton("取消", (dialogInterface, i) -> {

            });

            builder.setPositiveButton("确定", (dialogInterface, i) -> Toast.makeText(student_login_activity.this, "此功能暂不支持", Toast.LENGTH_SHORT).show());

            builder.create().show();
        });

        forgetNum.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(student_login_activity.this);
            LayoutInflater factory = LayoutInflater.from(student_login_activity.this);
            final View textEntryView = factory.inflate(R.layout.student_forgetnum_layout, null);
            builder.setTitle("忘记密码？");
            builder.setView(textEntryView);

            final EditText code = textEntryView.findViewById(R.id.student_forgetNum_info);
            final EditText phoneNum = textEntryView.findViewById(R.id.student_forgetNum_phoneNum);
            final EditText changedNum = textEntryView.findViewById(R.id.student_forgetNum_changedNum);

            builder.setNegativeButton("取消", (dialogInterface, i) -> {

            });

            builder.setPositiveButton("确定", (dialogInterface, i) -> {
                String codeInfo = code.getText().toString();
                String phoneNumInfo = phoneNum.getText().toString();

                if (!codeInfo.isEmpty() && !phoneNumInfo.isEmpty()) {
                    String changedNumInfo = changedNum.getText().toString();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Cursor cursor = db.rawQuery("select number from student where id=?", new String[]{codeInfo});
                    String pI = null;
                    if (cursor.moveToNext()) {
                        pI = cursor.getString(cursor.getColumnIndexOrThrow("number"));
                    }

                    if (phoneNumInfo.equals(pI)) {
                        db.execSQL("update student set password=? where id=?", new String[]{changedNumInfo, codeInfo});
                        Toast.makeText(student_login_activity.this, "更改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(student_login_activity.this, "请输入正确的学号与电话号码", Toast.LENGTH_SHORT).show();
                    }

                    cursor.close();
                } else {
                    Toast.makeText(student_login_activity.this, "输入值不能为空", Toast.LENGTH_SHORT).show();
                }
            });

            builder.create().show();
        });
    }
}