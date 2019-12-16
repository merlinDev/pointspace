package alerts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lazymessanger.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import controlers.CommentLoader;
import controlers.NotificationManager;
import models.Comment;
import models.Forum;
import models.NotificationPayload;

import static controlers.UserManager.COLLECTION_USER;

public class CommentSection extends BottomSheetDialogFragment {

    private static final String TAG = "CommentSection";

    private static final String COMMENTS = "comments";

    private FirebaseAuth auth;

    private RecyclerView recyclerView;
    private FirebaseFirestore database;
    private Forum forum;

    private EditText commentBox;
    private ImageButton commentBtn;

    public CommentSection(Forum forum) {
        this.forum = forum;
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_section_layout, container, false);

        recyclerView = view.findViewById(R.id.comment_section);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        commentBox = view.findViewById(R.id.comment_text);
        commentBtn = view.findViewById(R.id.add_comment);
        commentBtn.setOnClickListener(this::addComment);

        loadComments();
        return view;
    }

    private void loadComments() {
        CommentLoader commentLoader = new CommentLoader(getActivity(), recyclerView, forum.getId());
        commentLoader.loadComments();
    }

    // add comment
    private void addComment(View view) {
        CollectionReference commentReference = database.collection(COMMENTS);

        if (commentBox.getText() != null && !commentBox.getText().toString().isEmpty()) {
            String commentText = commentBox.getText().toString();

            final Comment comment = new Comment(auth.getUid(), forum.getId(), commentText);
            commentBox.setText(null);

            commentReference.add(comment)
                    .addOnSuccessListener(documentReference -> FirebaseFirestore.getInstance().collection(COLLECTION_USER)
                            .whereEqualTo("uid", auth.getUid())
                            .get().addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                                    String userName = (String) snapshot.get("name");
                                    String title = userName + " commented on your thread.";

                                    // sending notification
                                    NotificationPayload payload = new NotificationPayload(title, commentText, "normal");
                                    NotificationManager notificationManager = new NotificationManager(getContext(), forum.getUid());
                                    notificationManager.sendNotification(payload);
                                } else {
                                    Log.d(TAG, "addComment: no user found");
                                }
                            }))
                    .addOnFailureListener(e -> Log.d(TAG, "onFailure: comment not sent."));
        }
    }
}
