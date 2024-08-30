package com.zjgsu.studentmanagement.Util;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zjgsu.studentmanagement.R;

import java.util.List;

public class studentScoreAdapter extends RecyclerView.Adapter<studentScoreAdapter.ViewHolder>{
    @NonNull
    public List<student> mStudentList;
    private static final String TAG = "studentScoreAdapter";
    public studentScoreAdapter (List<student> list){
        mStudentList = list;
        Log.d(TAG, "studentScoreAdapter: 1");
    }
    @Override
    public studentScoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_score_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        student student = mStudentList.get(position);
        holder.student_score_id.setText(student.getId());
        holder.student_score_order.setText("" + student.getOrder());
        holder.student_score_name.setText(student.getName());
        Log.d(TAG, "onBindViewHolder: 2");
        int totalScore = student.getMathScore()+student.getEnglishScore()+student.getChineseScore();
        holder.student_score_total.setText("" + totalScore);
        Log.d(TAG, "onBindViewHolder: 1");
    }

    @Override
    public int getItemCount() {
        return mStudentList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView student_score_order;
        TextView student_score_name;
        TextView student_score_id;
        TextView student_score_total;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            student_score_order=itemView.findViewById(R.id.student_score_order);
            student_score_name=itemView.findViewById(R.id.student_score_name);
            student_score_id=itemView.findViewById(R.id.student_score_id);
            student_score_total=itemView.findViewById(R.id.student_score_total);
            Log.d(TAG, "ViewHolder: 1");
        }
    }
}
