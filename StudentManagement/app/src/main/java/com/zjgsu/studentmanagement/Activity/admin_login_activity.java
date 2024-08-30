package com.zjgsu.studentmanagement.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zjgsu.studentmanagement.R;
import com.zjgsu.studentmanagement.Util.admin;
import com.zjgsu.studentmanagement.Util.myDatabaseHelper;

import Service.ServiceGenerator;
import Service.admin_login_Service;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class admin_login_activity extends AppCompatActivity {
    private EditText name;
    private EditText password;
    private myDatabaseHelper dbHelper;
    private static final String TAG = "admin_login_activity";

    private final admin_login_Service adminLoginService= ServiceGenerator.adminLoginService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_login_layout);
        name = findViewById(R.id.admin_login_activity_name_input);
        password = findViewById(R.id.admin_login_activity_password_input);
        Button login = findViewById(R.id.admin_login_activity_login);
        TextView register = findViewById(R.id.admin_login_activity_register);
        TextView forgetNum = findViewById(R.id.admin_login_activity_forgetNum);

        dbHelper = myDatabaseHelper.getInstance(this);

        login.setOnClickListener(view -> {
            String nameInfo = name.getText().toString();
            int id=Integer.parseInt(nameInfo);
            String passwordInfo = password.getText().toString();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("select password from admin where name=?", new String[]{nameInfo});
            String pI = null;
            if (cursor.moveToNext()) {
                pI = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            }

            if (passwordInfo.equals(pI)) {
                Intent intent = new Intent(admin_login_activity.this, adminActivity.class);
                startActivity(intent);
                cursor.close();
            } else {
               // Toast.makeText(admin_login_activity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
            //调用json服务器方法实现
            Call<admin> call = adminLoginService.getadmin(id);
            call.enqueue(new Callback<admin>() {
                @Override
                public void onResponse(Call<admin> call, Response<admin> response) {
                    admin admin = response.body();

                    if(passwordInfo.equals(admin.getPassword())){
                        Intent intent = new Intent(admin_login_activity.this, adminActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(admin_login_activity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<admin> call, Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                }
            });
        });

        register.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(admin_login_activity.this);
            LayoutInflater factory = LayoutInflater.from(admin_login_activity.this);
            final View textEntryView = factory.inflate(R.layout.admin_register_layout, null);
            builder.setTitle("管理员注册");
            builder.setView(textEntryView);

            EditText code = textEntryView.findViewById(R.id.admin_register_info);
            EditText id_info = textEntryView.findViewById(R.id.admin_register_id);
            EditText name = textEntryView.findViewById(R.id.admin_register_name);
            EditText Password = textEntryView.findViewById(R.id.admin_register_password);

            builder.setNegativeButton("取消", (dialogInterface, i) -> {

            });

            builder.setPositiveButton("确定", (dialogInterface, i) -> {
                String codeInfo = code.getText().toString();

                if (codeInfo.equals("88888")) {
                    String text = id_info.getText().toString();
                    int id = Integer.valueOf(text);
                    String Name = name.getText().toString();
                    String password = Password.getText().toString();
                    admin admin = new admin();
                    admin.setId(id);
                    admin.setName(Name);
                    admin.setPassword(password);

                   // SQLiteDatabase db = dbHelper.getWritableDatabase();

                    if (password.matches("[0-9]{6}")) {
                        Call<admin> call = adminLoginService.createadmin(admin);
                        call.enqueue(new Callback<admin>() {
                            @Override
                            public void onResponse(Call<admin> call, Response<admin> response) {
                                admin admin = response.body();
                                Log.d(TAG, "onResponse: id: " + admin.getId()
                                        + " name: " + admin.getName()
                                        + " password: " + admin.getPassword());
                            }
                            @Override
                            public void onFailure(Call<admin> call, Throwable t) {
                                Log.e(TAG, "onFailure: ", t);
                            }
                        });

                    }else {
                        Toast.makeText(admin_login_activity.this, "密码为6位纯数字", Toast.LENGTH_SHORT).show();
                    }
                        //调用SQlite数据库方法实现
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Cursor cursor = db.rawQuery("select name from admin where name=? ", new String[]{Name});
                    if (cursor.moveToNext()) {
                        Toast.makeText(admin_login_activity.this, "该用户已经存在", Toast.LENGTH_SHORT).show();
                    } else {
                        db.execSQL("insert into admin(name,password)values(?,?)", new String[]{Name, password});
                    }
                    cursor.close();
                } else {
                    Toast.makeText(admin_login_activity.this, "注册码错误", Toast.LENGTH_SHORT).show();
                }
            });

            builder.create().show();
        });
        forgetNum.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(admin_login_activity.this);
            LayoutInflater factory = LayoutInflater.from(admin_login_activity.this);
            final View textEntryView = factory.inflate(R.layout.admin_forgetnum_layout, null);
            builder.setTitle("忘记密码？");
            builder.setView(textEntryView);

            final EditText code = textEntryView.findViewById(R.id.admin_forgetNum_info);
            final EditText changedNum = textEntryView.findViewById(R.id.admin_forgetNum_changedNum);

            builder.setNegativeButton("取消", (dialogInterface, i) -> {

            });

            builder.setPositiveButton("确定", (dialogInterface, i) -> {
                String codeInfo = code.getText().toString();
                if (!codeInfo.isEmpty()) {
                    String changedNumInfo = changedNum.getText().toString();
                    int id=Integer.valueOf(codeInfo);
                    /*
                    Call<admin> call = adminLoginServiceService.getadmin(id);
                    call.enqueue(new Callback<admin>() {
                        @Override
                        public void onResponse(Call<admin> call, Response<admin> response) {
                            admin admin = response.body();
                            admin.setPassword(changedNumInfo);
                        }
                        @Override
                        public void onFailure(Call<admin> call, Throwable t) {
                            Log.e(TAG, "onFailure: ", t);
                        }
                    });*/
                    //调用SQlite数据库实现
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    db.execSQL("update admin set password=? where id=?", new String[]{changedNumInfo, codeInfo});
                    Toast.makeText(admin_login_activity.this, "更改成功", Toast.LENGTH_SHORT).show();
                }
            });
            builder.create().show();
        });
    }
}
