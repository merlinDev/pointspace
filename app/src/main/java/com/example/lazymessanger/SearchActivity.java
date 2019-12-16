package com.example.lazymessanger;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import adapters.ForumSearchAdapter;
import adapters.UserSearchAdapter;
import models.Forum;
import models.Tag;
import models.User;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    SearchView searchView;

    private static final String USERS = "users";
    private static final String FORUMS = "forums";
    private static final String TAGS = "forum_abouts";

    private RecyclerView userResultContainer;
    private RecyclerView forumResultContainer;
    private ChipGroup tagResultContainer;

    //arrays
    private List<Tag> tags;

    //databases
    CollectionReference userRef;
    CollectionReference forumRef;
    CollectionReference tagRef;

    //adapters
    ForumSearchAdapter forumSearchAdapter;
    UserSearchAdapter userSearchAdapter;

    boolean usersFound;
    boolean forumsFound;
    boolean tagsFound;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(this);

        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnClickListener(view -> {
            searchView.setFocusable(true);
            searchView.setIconified(false);
            searchView.requestFocusFromTouch();
        });

        userResultContainer = findViewById(R.id.user_results);
        forumResultContainer = findViewById(R.id.forum_results);
        tagResultContainer = findViewById(R.id.tag_results);

        tags = new ArrayList<>();

        forumSearchAdapter = new ForumSearchAdapter(this);
        forumResultContainer.setAdapter(forumSearchAdapter);

        userSearchAdapter = new UserSearchAdapter(this);
        userResultContainer.setAdapter(userSearchAdapter);

        userResultContainer.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        forumResultContainer.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        userRef = FirebaseFirestore.getInstance().collection(USERS);
        forumRef = FirebaseFirestore.getInstance().collection(FORUMS);
        tagRef = FirebaseFirestore.getInstance().collection(TAGS);

        searchUsers("");
        searchForums("");
        searchTags("");

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        searchUsers(newText);
        searchForums(newText);
        searchTags(newText);

        return true;
    }

    private void searchUsers(String query) {
        userRef
                .orderBy("name")
                .startAt(query)
                .endAt(query + '\uf8ff')
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        usersFound = true;
                        userSearchAdapter.addItems(queryDocumentSnapshots.toObjects(User.class));
                    } else {
                        usersFound = false;
                        userSearchAdapter.clearItems();
                    }
                });

    }

    private void searchForums(String query) {
        forumRef
                .orderBy("header")
                .startAt(query)
                .endAt(query + '\uf8ff')
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        forumsFound = true;
                        forumSearchAdapter.addItems(queryDocumentSnapshots.toObjects(Forum.class));
                    } else {
                        forumsFound = false;
                        forumSearchAdapter.clearItems();
                    }

                });

    }

    private void searchTags(String query) {
        tagRef.orderBy("about")
                .startAt(query)
                .endAt(query + '\uf8ff')
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        tagsFound = true;
                        tagResultContainer.removeAllViews();
                        tags = queryDocumentSnapshots.toObjects(Tag.class);
                        for (Tag tag : tags) {
                            Chip chip = new Chip(this);
                            chip.setText(tag.getAbout());
                            chip.setOnClickListener(view -> {
                                Intent intent = new Intent(this, TagForumsActivity.class);
                                intent.putExtra("tag", tag.getAbout());
                                startActivity(intent);
                            });
                            tagResultContainer.addView(chip);
                        }
                    } else {
                        tagsFound = false;
                        tagResultContainer.removeAllViews();
                    }
                });
    }

    private void clearAdapters() {
        userSearchAdapter.clearItems();
        forumSearchAdapter.clearItems();
    }
}
