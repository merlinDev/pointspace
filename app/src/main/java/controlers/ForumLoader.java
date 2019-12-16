package controlers;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import adapters.BookmarkForumAdapter;
import adapters.FirestoreForumAdapter;
import customAnimators.CustomAnimator;
import models.BookmarkForum;
import models.Forum;

import static models.Forum.FORUMS;

public class ForumLoader {

    private static final String TAG = "ForumLoader";

    private static final String TIME_STAMP = "timeStamp";
    private static final int MAX_LIMIT = 5;
    private static CollectionReference forumRef = FirebaseFirestore.getInstance().collection(FORUMS);

    private DocumentSnapshot lastSnapshot;

    // adapter
    private RecyclerView recyclerView;
    private Activity activity;
    private LifecycleOwner lifecycleOwner;
    private SwipeRefreshLayout refreshLayout;
    private View loadingScreen;
    private Bundle savedInstanceState;

    private FirebaseAuth auth;

    private String tag;
    private boolean forBookmarks;

    public ForumLoader(LifecycleOwner lifecycleOwner, Activity activity, Bundle savedInstanceState, RecyclerView recyclerView, SwipeRefreshLayout refreshLayout, View loadingScreen) {
        this.activity = activity;
        this.lifecycleOwner = lifecycleOwner;
        this.loadingScreen = loadingScreen;
        this.recyclerView = recyclerView;
        this.refreshLayout = refreshLayout;
        this.savedInstanceState = savedInstanceState;
    }

    public ForumLoader(LifecycleOwner lifecycleOwner, Activity activity, Bundle savedInstanceState, RecyclerView recyclerView, SwipeRefreshLayout refreshLayout, View loadingScreen, String tag) {
        this.activity = activity;
        this.lifecycleOwner = lifecycleOwner;
        this.loadingScreen = loadingScreen;
        this.recyclerView = recyclerView;
        this.refreshLayout = refreshLayout;
        this.savedInstanceState = savedInstanceState;
        this.tag = tag;
    }

    public ForumLoader(LifecycleOwner lifecycleOwner, Activity activity, Bundle savedInstanceState, RecyclerView recyclerView, SwipeRefreshLayout refreshLayout, View loadingScreen, boolean forBookmarks) {
        this.activity = activity;
        this.lifecycleOwner = lifecycleOwner;
        this.loadingScreen = loadingScreen;
        this.recyclerView = recyclerView;
        this.refreshLayout = refreshLayout;
        this.savedInstanceState = savedInstanceState;
        this.forBookmarks = forBookmarks;

        auth = FirebaseAuth.getInstance();
    }

    public void loadForumFromDatabase() {

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(15)
                .build();

        Query query;

        if (tag != null) {
            query = forumRef
                    .whereArrayContains("about", tag)
                    .orderBy(TIME_STAMP, Query.Direction.DESCENDING);

            FirestorePagingOptions<BookmarkForum> options = new FirestorePagingOptions.Builder<BookmarkForum>()
                    .setLifecycleOwner(lifecycleOwner)
                    .setQuery(query, config, BookmarkForum.class)
                    .build();

            BookmarkForumAdapter bookmarkForumAdapter = new BookmarkForumAdapter(options, activity, savedInstanceState);
            recyclerView.setAdapter(bookmarkForumAdapter);

        } else if (forBookmarks) {
            query = FirebaseFirestore.getInstance()
                    .collection(Forum.BOOKMARKS)
                    .whereEqualTo("bookmarkerUid", auth.getUid());

            FirestorePagingOptions<BookmarkForum> options = new FirestorePagingOptions.Builder<BookmarkForum>()
                    .setLifecycleOwner(lifecycleOwner)
                    .setQuery(query, config, BookmarkForum.class)
                    .build();


            BookmarkForumAdapter bookmarkForumAdapter = new BookmarkForumAdapter(options, activity, savedInstanceState);
            recyclerView.setAdapter(bookmarkForumAdapter);

        } else {
            query = forumRef.orderBy(TIME_STAMP, Query.Direction.DESCENDING);

            FirestorePagingOptions<Forum> options = new FirestorePagingOptions.Builder<Forum>()
                    .setLifecycleOwner(lifecycleOwner)
                    .setQuery(query, config, Forum.class)
                    .build();

            FirestoreForumAdapter firestoreForumAdapter = new FirestoreForumAdapter(options, activity, savedInstanceState);
            recyclerView.setAdapter(firestoreForumAdapter);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            refreshLayout.setRefreshing(false);
            if (loadingScreen != null) {
                CustomAnimator.hideAnimate(loadingScreen);
                loadingScreen.setVisibility(View.GONE);
            }
        });
    }

    public void loadUsersForums(String uid) {

        Query query = forumRef
                .whereEqualTo("uid", uid);

        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build();

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            //refreshLayout.setRefreshing(false);
            if (loadingScreen != null) {
                CustomAnimator.hideAnimate(loadingScreen);
                loadingScreen.setVisibility(View.GONE);
            }
        });

        FirestorePagingOptions<Forum> options = new FirestorePagingOptions.Builder<Forum>()
                .setLifecycleOwner(lifecycleOwner)
                .setQuery(query, config, Forum.class)
                .build();

        FirestoreForumAdapter forumAdapter = new FirestoreForumAdapter(options, activity, savedInstanceState);
        recyclerView.setAdapter(forumAdapter);
    }
}
