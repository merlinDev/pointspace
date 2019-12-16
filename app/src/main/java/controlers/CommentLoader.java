package controlers;

import android.app.Activity;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import adapters.CommentViewAdapter;
import models.Comment;

public class CommentLoader {

    private static final String TAG = "ForumLoader";

    private static final String TIME_STAMP = "timeStamp";
    private static final int MAX_LIMIT = 5;
    private static final String COMMENTS = "comments";
    private static final String FORUM_ID = "forumId";
    private static CollectionReference commentRef;

    private DocumentSnapshot lastSnapshot;

    // adapter
    private CommentViewAdapter adapter;
    private RecyclerView recyclerView;
    private Activity activity;
    private String forumId;

    public CommentLoader(Activity activity, RecyclerView recyclerView, String forumId) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.forumId = forumId;
        commentRef = FirebaseFirestore.getInstance().collection(COMMENTS);
    }

    public void loadComments() {

        final Query query;

        if (lastSnapshot == null) {
            query = commentRef.whereEqualTo(FORUM_ID, forumId)
                    .limit(MAX_LIMIT);
        } else {
            query = commentRef.whereEqualTo(FORUM_ID, forumId)
                    .startAt(lastSnapshot)
                    .limit(MAX_LIMIT);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {

            System.out.println("query :::::::::::::: "+queryDocumentSnapshots.size());

            if (adapter != null) {
                Log.d(TAG, "onSuccess: adapter not null..................");
                adapter.addItems(queryDocumentSnapshots.toObjects(Comment.class));
            } else {
                Log.d(TAG, "onSuccess: adapter is null.....................");
                adapter = new CommentViewAdapter(activity, queryDocumentSnapshots.toObjects(Comment.class));
                recyclerView.setAdapter(adapter);
            }

            if (queryDocumentSnapshots.size() > 0) {
                lastSnapshot = queryDocumentSnapshots.getDocuments()
                        .get(queryDocumentSnapshots.size() - 1);
            }
        });

        System.out.println(".................................................");
    }


}
