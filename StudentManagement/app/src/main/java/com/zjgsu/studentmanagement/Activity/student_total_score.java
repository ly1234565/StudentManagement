package com.zjgsu.studentmanagement.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.zjgsu.studentmanagement.R;
import com.zjgsu.studentmanagement.Util.myDatabaseHelper;
import com.zjgsu.studentmanagement.Util.student;
import com.zjgsu.studentmanagement.Util.studentScoreAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class student_total_score extends AppCompatActivity {
    private final List<student> list = new ArrayList<>();
    private final List<student> failed_student_list = new ArrayList<>();
    private ExecutorService executorService;
    private myDatabaseHelper dbHelper;
    private Button averge_btn;

    private Button failed_student_btn;

    private int total;

    private int class_total;
    private int num;
    private int averge;
    private static final int averge_score_search=1;

    private static final int failed_student_search=1;
    private static final String TAG = "student_total_score";

    private final Handler handler=new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if(msg.what==averge_score_search){
                update_avergeUI(msg.arg1, msg.arg2);
            } else if (msg.what==failed_student_search) {

            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_total_score_layout);
        dbHelper = myDatabaseHelper.getInstance(this);
        Intent intent = getIntent();
        String subjectName = intent.getStringExtra("subject");
        if (Objects.equals(subjectName, "总成绩")) {
            initInfo();
        } else if (Objects.equals(subjectName, "语文")) {
            initInfo_Chinese();
        } else if (Objects.equals(subjectName, "数学")) {
            initInfo_Math();
        } else if (Objects.equals(subjectName, "英语")) {
            initInfo_English();
        } else {
            Toast.makeText(student_total_score.this, "请输入要查询的内容！！！", Toast.LENGTH_SHORT).show();
        }
        RecyclerView recyclerView = findViewById(R.id.student_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        studentScoreAdapter adapter=new studentScoreAdapter(list);
        Log.d(TAG, "onCreate: 1");
        recyclerView.setAdapter(adapter);
        Log.d(TAG, "onCreate: 2");
        /*studentScoreAdapter_new adapter = new studentScoreAdapter_new(student_total_score.this, R.layout.student_score_item, list);
        ListView total_score = findViewById(R.id.total_list_view);
        total_score.setAdapter(adapter);*/

        BarChart barChart = findViewById(R.id.score_bar_chart);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setMaxVisibleValueCount(60);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.animateY(1000);

        XAxis xAxis = barChart.getXAxis();
        YAxis yLeftAxis = barChart.getAxisLeft();
        YAxis yRightAxis = barChart.getAxisRight();

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setGranularity(1);
        xAxis.setLabelCount(list.size());

        String[] studentNames = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            studentNames[i] = list.get(i).getName();
        }
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(studentNames));

        yLeftAxis.setAxisMinimum(0f);
        yLeftAxis.setAxisMaximum(300f);
        yLeftAxis.setDrawGridLines(true);
        yLeftAxis.setTextSize(12f);
        yLeftAxis.setAxisLineWidth(2f);
        yLeftAxis.setTextColor(Color.BLACK);

        yRightAxis.setEnabled(false);

        LimitLine limitLine0 = new LimitLine(60f, "单科及格线");
        limitLine0.setLineWidth(2f);
        limitLine0.setLineColor(Color.RED);
        limitLine0.setTextSize(12f);
        limitLine0.setTextColor(Color.parseColor("#B22222"));
        yLeftAxis.addLimitLine(limitLine0);

        LimitLine limitLine1 = new LimitLine(180f, "三科及格线");
        limitLine1.setLineWidth(2f);
        limitLine1.setLineColor(Color.RED);
        limitLine1.setTextSize(12f);
        limitLine1.setTextColor(Color.parseColor("#B22222"));
        yLeftAxis.addLimitLine(limitLine1);

        BarData barData = generateBarData();
        barChart.setData(barData);
        barChart.invalidate();

        averge_btn=findViewById(R.id.average_score_btn);
        executorService= Executors.newFixedThreadPool(4);
        averge_btn.setOnClickListener(v->{
            executorService.submit(this::averge_method);
        });
        failed_student_btn=findViewById(R.id.failed_student_btn);
        failed_student_btn.setOnClickListener(v->{
            RecyclerView recyclerView_new = findViewById(R.id.student_list);
            LinearLayoutManager linearLayoutManager_new = new LinearLayoutManager(this);
            recyclerView_new.setLayoutManager(linearLayoutManager_new);
            for(int i=0;i<list.size();i++){
                num=0;
                student Student =list.get(i);
                total=Student.getChineseScore()+ Student.getEnglishScore()+ Student.getMathScore();
                if(total<60){
                    failed_student_list.add(Student);
                }
            }
            studentScoreAdapter adapter_new=new studentScoreAdapter(failed_student_list);
            recyclerView.setAdapter(adapter_new);
        });
    }
    void averge_method(){
        for(int i=0;i<list.size();i++){
            student Student =list.get(i);
            total=Student.getChineseScore()+ Student.getEnglishScore()+ Student.getMathScore();
            class_total+=total;
        }
        averge=(int)class_total/list.size();
        num=0;
        for(int i=0;i<list.size();i++){
            student Student =list.get(i);
            total=Student.getChineseScore()+ Student.getEnglishScore()+ Student.getMathScore();
            if(total<averge){
                num+=1;
            }
        }
        Message msg=Message.obtain();
        msg.what=averge_score_search;
        msg.arg1=averge;
        msg.arg2=num;
        handler.sendMessage(msg);
    }
    /*
    void failed_method(){
        Message msg=Message.obtain();
        msg.what=failed_student_search;
        for(int i=0;i<list.size();i++){
            num=0;
            student Student =list.get(i);
            total=Student.getChineseScore()+ Student.getEnglishScore()+ Student.getMathScore();
            if(total<60){
                failed_student_list.add(Student);
            }
        }
        msg.arg1=failed_student_list;
        handler.sendMessage(msg);
    }*/
    private void update_avergeUI(int averge,int num){
        AlertDialog.Builder builder = new AlertDialog.Builder(student_total_score.this);
        builder.setTitle("平均成绩情况");
        StringBuilder sb = new StringBuilder();
        sb.append("平均成绩：").append(averge).append("\n");
        sb.append("低于平均成绩的人数：").append(num).append("\n");
        builder.setMessage(sb.toString());
        builder.create().show();
    }
    /*
    private void update_failedUI(List<student> failed_student_list){
        RecyclerView recyclerView_new = findViewById(R.id.student_list);
        LinearLayoutManager linearLayoutManager_new = new LinearLayoutManager(this);
        recyclerView_new.setLayoutManager(linearLayoutManager_new);
        studentScoreAdapter adapter_new=new studentScoreAdapter(failed_student_list);
        recyclerView_new.setAdapter(adapter_new);
    }*/
    private void initInfo() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from student order by mathScore+chineseScore+englishScore desc", null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String sex = cursor.getString(cursor.getColumnIndexOrThrow("sex"));
            String number = cursor.getString(cursor.getColumnIndexOrThrow("number"));
            int mathScore = cursor.getInt(cursor.getColumnIndexOrThrow("mathScore"));
            int chineseScore = cursor.getInt(cursor.getColumnIndexOrThrow("chineseScore"));
            int englishScore = cursor.getInt(cursor.getColumnIndexOrThrow("englishScore"));
            db.execSQL("update  student set ranking=? where id=?", new String[]{String.valueOf(i), id});//将排名插入数据库
            list.add(new student(chineseScore, englishScore, id, mathScore, name, number, password, sex, i));
        }
        cursor.close();
        Log.d(TAG, "initInfo: 1");
    }

    private void initInfo_Chinese() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from student order by chineseScore desc", null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String sex = cursor.getString(cursor.getColumnIndexOrThrow("sex"));
            String number = cursor.getString(cursor.getColumnIndexOrThrow("number"));
            int chineseScore = cursor.getInt(cursor.getColumnIndexOrThrow("chineseScore"));
            db.execSQL("update  student set ranking=? where id=?", new String[]{String.valueOf(i), id});//将排名插入数据库
            list.add(new student(chineseScore, 0, id, 0, name, number, password, sex, i));
        }
        cursor.close();
    }

    private void initInfo_Math() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from student order by mathScore desc", null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String sex = cursor.getString(cursor.getColumnIndexOrThrow("sex"));
            String number = cursor.getString(cursor.getColumnIndexOrThrow("number"));
            int mathScore = cursor.getInt(cursor.getColumnIndexOrThrow("mathScore"));
            db.execSQL("update  student set ranking=? where id=?", new String[]{String.valueOf(i), id});//将排名插入数据库
            list.add(new student(0, 0, id, mathScore, name, number, password, sex, i));
        }
        cursor.close();
    }

    private void initInfo_English() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from student order by englishScore desc", null);
        int i = 0;
        while (cursor.moveToNext()) {
            i++;
            String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            String sex = cursor.getString(cursor.getColumnIndexOrThrow("sex"));
            String number = cursor.getString(cursor.getColumnIndexOrThrow("number"));
            int englishScore = cursor.getInt(cursor.getColumnIndexOrThrow("englishScore"));
            db.execSQL("update  student set ranking=? where id=?", new String[]{String.valueOf(i), id});//将排名插入数据库
            list.add(new student(0, englishScore, id, 0, name, number, password, sex, i));
        }
        cursor.close();
    }

    private BarData generateBarData() {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            student student = list.get(i);
            int totalScore = student.getChineseScore() + student.getEnglishScore() + student.getMathScore();
            entries.add(new BarEntry(i, totalScore));
        }

        BarDataSet dataSet = new BarDataSet(entries, "总成绩");
        dataSet.setColor(Color.rgb(0, 0, 100));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(14f);

        return new BarData(dataSet);
    }
}