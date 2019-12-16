package com.example.lazymessanger;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import controlers.ForumLoader;

public class TagForumsActivity extends AppCompatActivity {

    String tag;
    private RecyclerView recyclerView;
    private TextView tagLabel;

    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_fourms);

        tagLabel = findViewById(R.id.tag_label);

        recyclerView = findViewById(R.id.forum_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(() -> loadForums(savedInstanceState));

        tag = getIntent().getStringExtra("tag");
        tagLabel.setText(tag);
        loadForums(savedInstanceState);
    }

    private void loadForums(Bundle savedInstanceState) {

        Toast.makeText(this, tag, Toast.LENGTH_SHORT).show();

        ForumLoader forumLoader = new ForumLoader(
                this,
                this,
                savedInstanceState,
                recyclerView,
                refreshLayout,
                null,
                tag);

        forumLoader.loadForumFromDatabase();
    }
}
