package com.zjgsu.studentmanagement.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.zjgsu.studentmanagement.R;
import com.zjgsu.studentmanagement.Util.student;
import com.zjgsu.studentmanagement.Util.studentAdapter;
import com.zjgsu.studentmanagement.Util.myDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

import Service.ServiceGenerator;
import Service.student_Manage_Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class admin_studentInfoActivity extends AppCompatActivity {
    private final List<student> studentList = new ArrayList<>();
    private myDatabaseHelper dbHelper;
    private studentAdapter adapter;
    private final student_Manage_Service studentManageService= ServiceGenerator.studentManageService();
    private static final String TAG = "admin_studentInfoActivi";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_info_activity_layout);
        dbHelper = myDatabaseHelper.getInstance(this);
        initStudent(); //从数据库中检索所有学生信息
        adapter = new studentAdapter(admin_studentInfoActivity.this, R.layout.student_item, studentList);
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            final student student = studentList.get(position);
            AlertDialog.Builder builder = new AlertDialog.Builder(admin_studentInfoActivity.this);
            LayoutInflater factory = LayoutInflater.from(admin_studentInfoActivity.this);
            final View textEntryView = factory.inflate(R.layout.student_info_layout, null);
            builder.setView(textEntryView);
            builder.setTitle("请选择相关操作");


            Button selectInfo = textEntryView.findViewById(R.id.student_info_select);
            selectInfo.setOnClickListener(v -> {

                AlertDialog.Builder select_builder = new AlertDialog.Builder(admin_studentInfoActivity.this);
                select_builder.setTitle("学生详细信息");
                select_builder.setPositiveButton("修改信息", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(admin_studentInfoActivity.this, admin_add_studentInfoActivity.class);
                        intent.putExtra("haveData", "true");
                        intent.putExtra("name", student.getName());
                        intent.putExtra("sex", student.getSex());
                        intent.putExtra("id", student.getId());
                        intent.putExtra("number", student.getNumber());
                        intent.putExtra("password", student.getPassword());
                        intent.putExtra("mathScore", student.getMathScore());
                        intent.putExtra("chineseScore", student.getChineseScore());
                        intent.putExtra("englishScore", student.getEnglishScore());
                        startActivity(intent);

                    }
                });
                select_builder.setNegativeButton("删除信息", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        db.execSQL("delete from student where id=?", new String[]{student.getId()});
                        studentList.remove(position);
                        adapter.notifyDataSetChanged();
                        Call<Void> call = studentManageService.deletestudents(student.getId());
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {

                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e(TAG, "onFailure: ", t);
                            }
                        });
                    }
                });
                StringBuilder sb = new StringBuilder();
                sb.append("姓名：").append(student.getName()).append("\n");
                sb.append("学号：").append(student.getId()).append("\n");
                sb.append("手机号：").append(student.getNumber()).append("\n");
                int math = student.getMathScore();
                sb.append("数学成绩：").append(math).append("\n");
                int chinese = student.getChineseScore();
                sb.append("语文成绩：").append(chinese).append("\n");
                int english = student.getEnglishScore();
                sb.append("英语成绩：").append(english).append("\n");
                int sum = math + chinese + english;
                sb.append("总成绩：").append(sum).append("\n");
                sb.append("排名：").append(student.getOrder()).append("\n");
                select_builder.setMessage(sb.toString());
                select_builder.create().show();

            });

            Button delete_info = textEntryView.findViewById(R.id.student_info_delete);
            delete_info.setOnClickListener(v -> {
                AlertDialog.Builder delete_builder = new AlertDialog.Builder(admin_studentInfoActivity.this);
                delete_builder.setTitle("警告！！！！");
                delete_builder.setMessage("您将删除该学生信息，此操作不可逆，请谨慎操作！");

                delete_builder.setNegativeButton("取消", null);
                delete_builder.setPositiveButton("确定", (dialog, which) -> {
                    Call<Void> call = studentManageService.deletestudents(student.getId());
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "onFailure: ", t);
                        }
                    });
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("delete from student where id=?", new String[]{student.getId()});
                    studentList.remove(position);
                    adapter.notifyDataSetChanged();
                });

                delete_builder.create().show();
            });

            Button update_info = textEntryView.findViewById(R.id.student_info_update);
            update_info.setOnClickListener(v -> {
                Intent intent = new Intent(admin_studentInfoActivity.this, admin_add_studentInfoActivity.class);
                intent.putExtra("haveData", "true");
                intent.putExtra("name", student.getName());
                intent.putExtra("sex", student.getSex());
                intent.putExtra("id", student.getId());
                intent.putExtra("number", student.getNumber());
                intent.putExtra("password", student.getPassword());
                intent.putExtra("mathScore", student.getMathScore());
                intent.putExtra("chineseScore", student.getChineseScore());
                intent.putExtra("englishScore", student.getEnglishScore());
                startActivity(intent);
            });

            builder.setNegativeButton("取消", (dialogInterface, i) -> {

            });

            builder.create().show();
        });
    }

    private void initStudent() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from student order by id", null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String sex = cursor.getString(cursor.getColumnIndexOrThrow("sex"));
            String number = cursor.getString(cursor.getColumnIndexOrThrow("number"));
            int mathScore = cursor.getInt(cursor.getColumnIndexOrThrow("mathScore"));
            int chineseScore = cursor.getInt(cursor.getColumnIndexOrThrow("chineseScore"));
            int englishScore = cursor.getInt(cursor.getColumnIndexOrThrow("englishScore"));
            int order = cursor.getInt(cursor.getColumnIndexOrThrow("ranking"));
            studentList.add(new student(chineseScore, englishScore, id, mathScore, name, number, password, sex, order));
        }
        cursor.close();
    }
}