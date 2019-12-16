package com.example.lazymessanger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import models.Forum;
import models.User;

public class ActivityForumAnalyze extends AppCompatActivity {

    private BarChart barChart;

    private ArrayList<BarEntry> commentEntries;
    private ArrayList<BarEntry> upVoteEntries;
    private ArrayList<BarEntry> downVoteEntries;

    private TextView username;
    private TextView header;
    private TextView upVotes;
    private TextView downVotes;

    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_analyze);

        username = findViewById(R.id.username);
        header = findViewById(R.id.forum_header);

        barChart = findViewById(R.id.bar_chart);
        barChart.animate();

        database = FirebaseFirestore.getInstance();

        commentEntries = new ArrayList<>();
        upVoteEntries = new ArrayList<>();
        downVoteEntries = new ArrayList<>();

        Intent intent = getIntent();
        String forumId = intent.getStringExtra("forum-id");

        setInfo(forumId);
        setGraph(forumId);
    }

    private void setGraph(String forumId) {
        BarData barData = new BarData();

        database.collection("comments")
                .whereEqualTo("forumId", forumId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        commentEntries.add(new BarEntry(2, queryDocumentSnapshots.size()));
                        BarDataSet commentDataSet = new BarDataSet(commentEntries, "comments");
                        commentDataSet.setColor(R.color.red);
                        barData.addDataSet(commentDataSet);
                        barChart.setData(barData);
                    }
                });

        database.collection("upVotes")
                .whereEqualTo("forumId", forumId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        upVoteEntries.add(new BarEntry(4, queryDocumentSnapshots.size()));
                        BarDataSet upVoteDataSet = new BarDataSet(upVoteEntries, "up votes");
                        upVoteDataSet.setColor(R.color.blue);
                        barData.addDataSet(upVoteDataSet);
                    }
                });

        database.collection("downVotes")
                .whereEqualTo("forumId", forumId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        downVoteEntries.add(new BarEntry(6, queryDocumentSnapshots.size()));
                        BarDataSet downVoteDataSet = new BarDataSet(downVoteEntries, "down votes");
                        downVoteDataSet.setColor(R.color.colorPrimary);
                        barData.addDataSet(downVoteDataSet);
                    }
                });
    }


    private void setInfo(String forumId) {
        database.collection("forums")
                .whereEqualTo("id", forumId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Forum forum = queryDocumentSnapshots.getDocuments().get(0).toObject(Forum.class);
                        if (forum != null) {

                            header.setText(forum.getHeader());

                            String uid = forum.getUid();

                            database.collection("users")
                                    .whereEqualTo("uid", uid)
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        if (!querySnapshot.isEmpty()) {
                                            User user = querySnapshot.getDocuments().get(0).toObject(User.class);
                                            username.setText(user.getName());
                                        }
                                    });
                        }
                    }
                });
    }
}
