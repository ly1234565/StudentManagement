package com.zjgsu.studentmanagement.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.zjgsu.studentmanagement.R;
import com.zjgsu.studentmanagement.Util.myDatabaseHelper;
import com.zjgsu.studentmanagement.Util.myReceiver;

public class mainActivity extends AppCompatActivity {
    private long exit_time;
    private static final String CHANNEL_NAME = "Messages";
    private static final String CHANNEL_DESCRIPTION = "Messages are .....";
    private static final String CHANNEL_ID = "com.zjgsu.studentmanagement.message";
    NotificationManager notificationManager;
    myReceiver broadcast = new myReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        Button admin = findViewById(R.id.main_activity_admin);
        Button student = findViewById(R.id.main_activity_student);

        //remember to delete it after test finished
        //new order: do not remove it because something need this
        Button quickButton = findViewById(R.id.main_quick_button);

        myDatabaseHelper dbHelper = myDatabaseHelper.getInstance(this);
        dbHelper.getWritableDatabase();

        admin.setOnClickListener(v -> {
            Intent intent = new Intent(mainActivity.this, admin_login_activity.class);
            startActivity(intent);
        });
        student.setOnClickListener(v -> {
            Intent intent = new Intent(mainActivity.this, student_login_activity.class);
            startActivity(intent);
        });
        //remember to delete it after test finished
        //new order: do not remove it because something need this
        quickButton.setOnClickListener(v -> {
            sendNotification();
        });

        IntentFilter intentFilter = new IntentFilter("android.intent.action.TIME_TICK");
        registerReceiver(broadcast, intentFilter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exit_time > 2000) {
                Toast.makeText(mainActivity.this, "双击退出键以退出", Toast.LENGTH_SHORT).show();
                exit_time = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendNotification() {
        Intent intent = new Intent(this, student_total_score.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcast);
    }
}