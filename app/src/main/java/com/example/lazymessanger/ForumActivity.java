package com.example.lazymessanger;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GestureDetectorCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import alerts.CommentSection;
import controlers.ImageManager;
import controlers.asyncTasks.ForumLayoutBuilder;
import controlers.asyncTasks.ImageLoader;
import controlers.asyncTasks.TagLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import models.BookmarkForum;
import models.Forum;
import models.ForumView;
import models.User;

import static controlers.UserManager.COLLECTION_USER;
import static models.Forum.FORUMS;

public class ForumActivity extends AppCompatActivity {

    private static final String TAG = "ForumActivity";
    private static final int MAX_LINES = 8;
    public static final String FORUM_VIEWS = "forumViews";

    private TextView name;
    private TextView headerText;
    private LinearLayout layout;
    private CircleImageView imageView;
    private ChipGroup chipGroup;
    private Toolbar toolbar;

    GestureDetectorCompat gestureDetector;

    private Intent intent;
    private FirebaseAuth auth;

    // forum
    private Forum forum;
    private FirebaseFirestore database;

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(null);
        }

        toolbar = findViewById(R.id.forum_toolbar);

        this.savedInstanceState = savedInstanceState;
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        chipGroup = findViewById(R.id.tag_list);

        name = findViewById(R.id.name);
        headerText = findViewById(R.id.forum_header);
        layout = findViewById(R.id.description_container);

        imageView = findViewById(R.id.profile_pic);
        intent = getIntent();

        preLoadForum();
        //loadComments();

    }

    // pre loading basic details
    private void preLoadForum() {

        String forumString = intent.getStringExtra("forum-string");
        String nameString = intent.getStringExtra("user-name");
        if (forumString != null) {
            forum = new Gson().fromJson(forumString, Forum.class);

            if (forum != null && getComponentName() != null) {

                headerText.setText(forum.getHeader());
                name.setText(nameString);

                submitView(forum);
                loadForum();
            }

        }
    }

    // to make sure that the user has seen this (views)
    private void submitView(final Forum forum) {

        final ForumView forumView = new ForumView(auth.getUid(), forum.getId());

        final CollectionReference collection = FirebaseFirestore.getInstance()
                .collection(FORUM_VIEWS);

        collection
                .whereEqualTo("uid", auth.getUid())
                .whereEqualTo("forumId", forum.getId()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        collection.add(forumView);
                    }
                });
    }

    // loading the whole forum
    private void loadForum() {

        if (this.forum != null) {
            Task<QuerySnapshot> forumTask = database.collection(FORUMS)
                    .whereEqualTo("id", forum.getId())
                    .get();

            List<String> categories = forum.getAbout();
            String[] tags = categories.toArray(new String[0]);

            new TagLoader(this, chipGroup).execute(tags);

            forumTask.addOnSuccessListener(queryDocumentSnapshots -> new ForumLayoutBuilder(
                    this,
                    savedInstanceState,
                    layout,
                    forum.getContents(),
                    ForumLayoutBuilder.FOR_FORUM).execute());

            Task<Uri> uriTask = FirebaseStorage.getInstance()
                    .getReference(ImageManager.PROFILE_IMAGES)
                    .child(forum.getUid())
                    .getDownloadUrl();

            uriTask.addOnSuccessListener(uri -> Picasso.get().load(uri).into(imageView));

        }
    }

    private void downloadUserData(Forum forum) {
        database.collection(COLLECTION_USER)
                .document(forum.getUid())
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    name.setText(user.getName());

                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference(ImageManager.THUMBNAILS)
                            .child(user.getUid());

                    final Task<Uri> uriTask = storageReference.getDownloadUrl();

                    uriTask.addOnSuccessListener(uri -> new ImageLoader(imageView).execute(uri));

                    uriTask.addOnFailureListener(e -> Log.d(TAG, "onFailure: image load fail..."));
                }
            }
        });
    }

    public void showCommentSheet(View view) {
        CommentSection commentSection = new CommentSection(forum);
        commentSection.show(getSupportFragmentManager(), "comment-section");
    }

    private void setBookmarkIcon(MenuItem item) {
        String forumId = forum.getId();

        database.collection(Forum.BOOKMARKS)
                .whereEqualTo("bookmarkerUid", auth.getUid())
                .whereEqualTo("id", forumId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        item.setIcon(R.drawable.bookmark);
                    }
                });
    }

    public void addToBookmarks(MenuItem item) {
        String forumId = forum.getId();
        item.setEnabled(false);
        database.collection(Forum.BOOKMARKS)
                .whereEqualTo("bookmarkerUid", auth.getUid())
                .whereEqualTo("id", forumId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {

                        BookmarkForum bookmarkForum = new BookmarkForum(auth.getUid(), forum);

                        database.collection(Forum.BOOKMARKS)
                                .add(bookmarkForum)
                                .addOnSuccessListener(documentReference -> {
                                    item.setIcon(R.drawable.bookmark);
                                    item.setEnabled(true);
                                });
                    } else {
                        database.collection(Forum.BOOKMARKS)
                                .document(queryDocumentSnapshots.getDocuments().get(0).getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    item.setIcon(R.drawable.bookmark_border);
                                    item.setEnabled(true);
                                });
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forum_view_menu, menu);
        MenuItem item = menu.findItem(R.id.bookmark);
        setBookmarkIcon(item);
        return super.onCreateOptionsMenu(menu);
    }

    public void analyzeForum(MenuItem item) {
        Intent intent = new Intent(this, ActivityForumAnalyze.class);
        intent.putExtra("forum-id", forum.getId());
        startActivity(intent);

    }
}
