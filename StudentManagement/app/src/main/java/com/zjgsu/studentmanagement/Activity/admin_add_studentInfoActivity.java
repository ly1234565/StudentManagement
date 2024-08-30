package com.zjgsu.studentmanagement.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zjgsu.studentmanagement.R;
import com.zjgsu.studentmanagement.Util.admin;
import com.zjgsu.studentmanagement.Util.myDatabaseHelper;
import com.zjgsu.studentmanagement.Util.student;

import java.util.Objects;

import Service.ServiceGenerator;
import Service.admin_login_Service;
import Service.student_Manage_Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class admin_add_studentInfoActivity extends AppCompatActivity {
    private EditText name;
    private EditText sex;
    private EditText id;
    private EditText number;
    private EditText password;
    private EditText math;
    private EditText chinese;
    private EditText english;
    private String oldID;
    private myDatabaseHelper dbHelper;
    Intent oldData;
    private static final String TAG = "admin_add_studentInfoAc";
    private final student_Manage_Service studentManageService= ServiceGenerator.studentManageService();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_add_student_info);

        name = findViewById(R.id.add_student_layout_name);
        sex = findViewById(R.id.add_student_layout_sex);
        id = findViewById(R.id.add_student_layout_id);
        number = findViewById(R.id.add_student_layout_number);
        password = findViewById(R.id.add_student_layout_password);
        math = findViewById(R.id.add_student_layout_math);
        chinese = findViewById(R.id.add_student_layout_chinese);
        english = findViewById(R.id.add_student_layout_english);
        dbHelper = myDatabaseHelper.getInstance(this);

        oldData = getIntent();
        if (Objects.equals(oldData.getStringExtra("haveData"), "true")) {
            initInfo();
        }

        Button sure = findViewById(R.id.add_student_layout_sure);
        sure.setOnClickListener(v -> {
            String id_ = id.getText().toString();
            String name_ = name.getText().toString();
            String sex_ = sex.getText().toString();
            String password_ = password.getText().toString();
            String number_ = number.getText().toString();
            String mathScore = math.getText().toString();
            int math=Integer.valueOf(mathScore);
            String chineseScore = chinese.getText().toString();
            int chinese=Integer.valueOf(chineseScore);
            String englishScore = english.getText().toString();
            int english=Integer.valueOf(englishScore);
            int order=0;

            if (!TextUtils.isEmpty(id_) && !TextUtils.isEmpty(name_) && !TextUtils.isEmpty(sex_)) {

                if (sex_.matches("[女|男]")) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.beginTransaction();
                    db.execSQL("delete from student where id=?", new String[]{oldID});
                    Call<Void> call = studentManageService.deletestudents(oldID);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "onFailure: ", t);
                        }
                    });

                    Cursor cursor = db.rawQuery("select * from student where id=?", new String[]{id_});
                    if (cursor.moveToNext()) {
                        Toast.makeText(admin_add_studentInfoActivity.this, "已有学生使用该学号,请重新输入", Toast.LENGTH_SHORT).show();
                    } else {
                        student student = new student(chinese,english, id_,math, name_,number_, password_,sex_,order);
                        Call<student> call_new = studentManageService.createstudents(student);
                        call_new.enqueue(new Callback<student>() {
                            @Override
                            public void onResponse(Call<student> call, Response<student> response) {
                                student student = response.body();
                                Log.d(TAG, "onResponse: id: 1" );
                            }
                            @Override
                            public void onFailure(Call<student> call, Throwable t) {
                                Log.e(TAG, "onFailure: ", t);
                            }
                        });
                        db.execSQL("insert into student(id,name,sex,password,number,mathScore,chineseScore,englishScore) values(?,?,?,?,?,?,?,?)", new String[]{id_, name_, sex_, password_, number_, mathScore, chineseScore, englishScore,});
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        Intent intent = new Intent(admin_add_studentInfoActivity.this, adminActivity.class);
                        startActivity(intent);
                    }
                    cursor.close();
                } else {
                    Toast.makeText(admin_add_studentInfoActivity.this, "请输入正确的性别信息", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(admin_add_studentInfoActivity.this, "姓名，学号，性别均不能为空", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void initInfo() {
        String oldName = oldData.getStringExtra("name");
        name.setText(oldName);
        String oldSex = oldData.getStringExtra("sex");
        sex.setText(oldSex);
        String oldId = oldData.getStringExtra("id");
        oldID = oldId;
        id.setText(oldId);
        String oldNumber = oldData.getStringExtra("number");
        number.setText(oldNumber);
        String oldPassword = oldData.getStringExtra("password");
        password.setText(oldPassword);
        int mathScore = oldData.getIntExtra("mathScore", 0);
        math.setText(String.valueOf(mathScore));
        int chineseScore = oldData.getIntExtra("chineseScore", 0);
        chinese.setText(String.valueOf(chineseScore));
        int englishScore = oldData.getIntExtra("englishScore", 0);
        english.setText(String.valueOf(englishScore));
    }
}