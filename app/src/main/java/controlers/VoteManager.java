package controlers;

import android.content.Context;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.lazymessanger.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import customAnimators.CustomAnimator;
import models.Forum;
import models.Vote;

public class VoteManager {

    private static final String TAG = "VoteManager";

    private final CollectionReference UP_VOTE_REFERENCE = FirebaseFirestore.getInstance().collection("upVotes");
    private final CollectionReference DOWN_VOTE_REFERENCE = FirebaseFirestore.getInstance().collection("downVotes");
    public static final int DOWN_VOTE = 0;
    public static final int UP_VOTE = 1;
    private FirebaseAuth auth;
    private Context context;
    private Forum forum;

    private TextView upVotes;
    private TextView downVotes;

    private ImageButton upVoteButton;
    private ImageButton downVoteButton;

    private boolean loadingUpVote;
    private boolean loadingDownVote;

    public VoteManager(Context context, Forum forum, TextView upVotes, TextView downVotes, ImageButton upVoteButton, ImageButton downVoteButton) {
        auth = FirebaseAuth.getInstance();
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.upVoteButton = upVoteButton;
        this.downVoteButton = downVoteButton;
        this.forum = forum;
        this.context = context;
        addVoteListeners();
    }

    private void addVoteListeners() {
        Query upVoteQuery = UP_VOTE_REFERENCE
                .whereEqualTo("forumId", forum.getId());

        upVoteQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {
            upVotes.setText(String.valueOf(queryDocumentSnapshots.size()));
            Log.d(TAG, "onEvent: upVote changed...........");
        });

        upVoteQuery.whereEqualTo("uid", auth.getUid())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                upVoteButton.setImageResource(R.drawable.up_blue);
            } else {
                upVoteButton.setImageResource(R.drawable.up);
            }
        });

        Query downVoteQuery = DOWN_VOTE_REFERENCE
                .whereEqualTo("forumId", forum.getId());

        downVoteQuery.addSnapshotListener((queryDocumentSnapshots, e) -> {
            downVotes.setText(String.valueOf(queryDocumentSnapshots.size()));
            Log.d(TAG, "onEvent: downVote changed...........");
        });

        downVoteQuery.whereEqualTo("uid", auth.getUid())
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                downVoteButton.setImageResource(R.drawable.down_red);
            } else {
                downVoteButton.setImageResource(R.drawable.down);
            }
        });
    }

    public void voteForum(final int type) {

        Query query;

        // upVote
        if (type == UP_VOTE) {
            if (!loadingUpVote) {
                loadingUpVote = true;
                query = UP_VOTE_REFERENCE
                        .whereEqualTo("uid", auth.getUid())
                        .whereEqualTo("forumId", forum.getId());

                query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Vote vote = new Vote(auth.getUid(), forum.getId());
                        UP_VOTE_REFERENCE.add(vote)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d(TAG, "onSuccess: upVote added.");
                                    upVoteButton.setImageResource(R.drawable.up_blue);
                                    CustomAnimator.popupAnimate(upVoteButton);
                                    downVoteButton.setImageResource(R.drawable.down);

                                    DOWN_VOTE_REFERENCE
                                            .whereEqualTo("uid", auth.getUid())
                                            .whereEqualTo("forumId", forum.getId())
                                            .get()
                                            .addOnSuccessListener(downVotequeryDocumentSnapshots -> {
                                                if (!downVotequeryDocumentSnapshots.isEmpty()) {
                                                    DocumentSnapshot documentSnapshot = downVotequeryDocumentSnapshots.getDocuments().get(0);
                                                    DOWN_VOTE_REFERENCE.document(documentSnapshot.getId())
                                                            .delete()
                                                            .addOnSuccessListener(aVoid -> loadingUpVote = false);
                                                }else{
                                                    loadingUpVote = false;
                                                }

                                            });
                                });
                    } else {
                        String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                        removeVote(id, type);
                    }
                });
            }

            // downVote
        } else if (type == DOWN_VOTE) {

            if (!loadingDownVote) {
                loadingDownVote = true;
                query = DOWN_VOTE_REFERENCE
                        .whereEqualTo("uid", auth.getUid())
                        .whereEqualTo("forumId", forum.getId());

                query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        Vote vote = new Vote(auth.getUid(), forum.getId());
                        DOWN_VOTE_REFERENCE.add(vote)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d(TAG, "onSuccess: downVote added.");
                                    downVoteButton.setImageResource(R.drawable.down_red);
                                    CustomAnimator.popupAnimate(downVoteButton);
                                    upVoteButton.setImageResource(R.drawable.up);

                                    UP_VOTE_REFERENCE
                                            .whereEqualTo("uid", auth.getUid())
                                            .whereEqualTo("forumId", forum.getId())
                                            .get()
                                            .addOnSuccessListener(upVotequeryDocumentSnapshots -> {
                                                if (!upVotequeryDocumentSnapshots.isEmpty()) {
                                                    DocumentSnapshot documentSnapshot = upVotequeryDocumentSnapshots.getDocuments().get(0);
                                                    UP_VOTE_REFERENCE.document(documentSnapshot.getId())
                                                            .delete()
                                                            .addOnSuccessListener(aVoid -> loadingDownVote = false);
                                                }else{
                                                    loadingDownVote = false;
                                                }

                                            });
                                });
                    } else {
                        String id = queryDocumentSnapshots.getDocuments().get(0).getId();
                        removeVote(id, type);
                    }
                });
            }
        }
    }


    // removing vote
    private void removeVote(String id, int type) {
        if (type == UP_VOTE) {
            if (loadingUpVote) {
                UP_VOTE_REFERENCE
                        .document(id)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "onSuccess: upVote removed....");
                            upVoteButton.setImageResource(R.drawable.up);
                            loadingUpVote = false;
                            loadingDownVote = false;
                        });
            }
        } else if (type == DOWN_VOTE) {
            if (loadingDownVote) {
                DOWN_VOTE_REFERENCE
                        .document(id)
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "onSuccess: upVote removed....");
                            downVoteButton.setImageResource(R.drawable.down);
                            loadingDownVote = false;
                            loadingUpVote = false;
                        });
            }
        }
    }


}
