package com.zjgsu.studentmanagement.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.zjgsu.studentmanagement.R;

public class adminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_layout);

        Button select = findViewById(R.id.admin_activity_select);
        Button add = findViewById(R.id.admin_activity_add);
        Button order = findViewById(R.id.admin_activity_order);

        select.setOnClickListener(v -> {
            Intent intent = new Intent(adminActivity.this, admin_studentInfoActivity.class);
            startActivity(intent);
        });
        add.setOnClickListener(v -> {
            Intent intent = new Intent(adminActivity.this, admin_add_studentInfoActivity.class);
            intent.putExtra("haveData", "false");
            startActivity(intent);
        });
        order.setOnClickListener(v -> {
            Intent intent = new Intent(adminActivity.this, subject_score_order.class);
            startActivity(intent);
        });
    }
}