package com.zjgsu.studentmanagement.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.zjgsu.studentmanagement.R;

public class subject_score_order extends AppCompatActivity {
    private EditText subject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_score_order);

        subject= findViewById(R.id.subject_input_editTextText);
        Button order = findViewById(R.id.score_order);

        order.setOnClickListener(v -> {
            Intent intent = new Intent(subject_score_order.this, student_total_score.class);
            intent.putExtra("subject", subject.getText().toString());
            startActivity(intent);
        });
    }
}