package adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lazymessanger.ForumActivity;
import com.example.lazymessanger.R;
import com.example.lazymessanger.ThreadRegisterActivity;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import controlers.ImageManager;
import controlers.VoteManager;
import controlers.asyncTasks.ImageLoader;
import controlers.asyncTasks.LayoutBuilder;
import customAnimators.CustomAnimator;
import customAnimators.Transitions;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Content;
import models.Forum;
import models.User;

public class FirestoreForumAdapter extends FirestorePagingAdapter<Forum, FirestoreForumAdapter.ForumViewHolder> {

    private static final String TAG = "FirestoreForumAdapter";
    private static final String USERS = "users";
    private static final String UPVOTES = "upVotes";
    private static final String DOWNVOTES = "downVotes";

    private Activity activity;

    // firestore
    private FirebaseFirestore database;
    private Task<DocumentSnapshot> firestoreTask;
    private Bundle savedInstanceState;


    //auth
    private FirebaseAuth auth;

    /**
     * Construct a new FirestorePagingAdapter from the given {@link FirestorePagingOptions}.
     *
     * @param options
     */
    public FirestoreForumAdapter(@NonNull FirestorePagingOptions<Forum> options, Activity activity, Bundle savedInstanceState) {


        super(options);
        Log.d(TAG, "FirestoreForumAdapter: adapter called................................");
        this.activity = activity;
        this.savedInstanceState = savedInstanceState;
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thread_layout, parent, false);
        return new ForumViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ForumViewHolder holder, int i, @NonNull final Forum forum) {
        final String uid = forum.getUid();

        firestoreTask = database.collection(USERS)
                .document(uid)
                .get();

        Log.d(TAG, "onBindViewHolder: forum :::::::::::::: " + forum.getId());

        firestoreTask.addOnSuccessListener(documentSnapshot -> {

            if (documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                if (user != null) {
                    holder.name.setText(user.getName());

                    ArrayList<Content> contents = forum.getContents();
                    if (contents.size() == 1) {
                        new LayoutBuilder(activity, savedInstanceState, holder.container, contents.get(0)).execute();
                    } else if (contents.size() > 1) {
                        new LayoutBuilder(activity, savedInstanceState, holder.container, contents.get(0), contents.get(1)).execute();
                    }

                    holder.forum = forum;

                    holder.name.setBackground(null);
                    holder.header.setBackground(null);
                    holder.layout.setBackground(null);
                    holder.layout.setMinimumHeight(0);

                    holder.header.setText(forum.getHeader());

                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMMM yyyy, HH:mm", Locale.US);
                    String dateTime = dateFormat.format(new Date(forum.getTimeStamp()));

                    holder.date.setText(dateTime);

                    FirebaseFirestore.getInstance()
                            .collection(ForumActivity.FORUM_VIEWS)
                            .whereEqualTo("forumId", forum.getId())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                int viewCount = queryDocumentSnapshots.size();
                                holder.views.setText(String.valueOf(viewCount));
                            });

                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference(ImageManager.THUMBNAILS)
                            .child(forum.getUid());

                    Task<Uri> task = storageReference.getDownloadUrl();

                    task.addOnSuccessListener(uri -> {
                        new ImageLoader(holder.image).execute(uri);
                        CustomAnimator.fadeAnimate(holder.image);
                    });

                    holder.voteManager = new VoteManager(
                            activity,
                            forum,
                            holder.upVotes,
                            holder.downVotes,
                            holder.upVoteButton,
                            holder.downVoteButton
                    );

                    holder.settings.setOnClickListener(v -> {
                        PopupMenu menu = new PopupMenu(activity, v);

                        if (forum.getUid().equals(auth.getUid())) {
                            menu.getMenuInflater().inflate(R.menu.forum_card_menu, menu.getMenu());
                            menu.show();

                            menu.setOnMenuItemClickListener(menuItem -> {

                                int itemId = menuItem.getItemId();

                                switch (itemId) {
                                    case R.id.edit: {
                                        Intent intent = new Intent(activity, ThreadRegisterActivity.class);
                                        intent.putExtra("edit-forum", forum);
                                        activity.startActivity(intent);
                                        break;
                                    }

                                    case R.id.delete: {

                                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                        builder.setTitle(R.string.are_you_sure_you_want_to_delete_this_thread);

                                        builder.setPositiveButton("yes", (dialogInterface, i1) -> FirebaseFirestore.getInstance()
                                                .collection(Forum.FORUMS)
                                                .whereEqualTo("id", forum.getId())
                                                .get()
                                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                                    if (!queryDocumentSnapshots.isEmpty()) {
                                                        FirebaseFirestore.getInstance()
                                                                .collection(Forum.FORUMS)
                                                                .document(queryDocumentSnapshots.getDocuments().get(0).getId())
                                                                .delete()
                                                                .addOnSuccessListener(aVoid -> FirebaseFirestore.getInstance()
                                                                        .collection(Forum.BOOKMARKS)
                                                                        .whereEqualTo("id", forum.getId())
                                                                        .get()
                                                                        .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                                                            if (!queryDocumentSnapshots1.isEmpty()) {
                                                                                FirebaseFirestore.getInstance()
                                                                                        .collection(Forum.BOOKMARKS)
                                                                                        .document(queryDocumentSnapshots1.getDocuments().get(0).getId())
                                                                                        .delete()
                                                                                        .addOnSuccessListener(aVoid1 -> {
                                                                                            notifyDataSetChanged();
                                                                                        });
                                                                            }
                                                                        }));
                                                    }
                                                })).setNegativeButton("no", (dialogInterface, i12) -> {

                                        });

                                        builder.create().show();

                                        break;
                                    }
                                }

                                return false;
                            });
                        }

                    });

                    // animation
                    CustomAnimator.fadeAnimate(holder.name);
                    CustomAnimator.fadeAnimate(holder.header);
                    CustomAnimator.fadeAnimate(holder.upVotes);
                    CustomAnimator.fadeAnimate(holder.downVotes);
                    CustomAnimator.fadeAnimate(holder.views);
                    CustomAnimator.fadeAnimate(holder.upVoteButton);
                    CustomAnimator.fadeAnimate(holder.downVoteButton);
                    CustomAnimator.fadeAnimate(holder.layout);
                }
            }
        });
    }

    private void deleteForum(Forum forum) {
        Toast.makeText(activity, "deleting : " + forum.getId(), Toast.LENGTH_SHORT).show();
    }

    class ForumViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        Forum forum;
        TextView name;
        TextView header;
        TextView date;
        CircleImageView image;
        TextView views;

        TextView upVotes;
        TextView downVotes;
        VoteManager voteManager;

        ImageButton upVoteButton;
        ImageButton downVoteButton;
        ImageButton settings;

        //contents
        View container;

        ChipGroup chipGroup;
        LinearLayout layout;

        ForumViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            header = itemView.findViewById(R.id.forum_header);
            date = itemView.findViewById(R.id.date);
            layout = itemView.findViewById(R.id.forum_container);
            views = itemView.findViewById(R.id.viewCount);
            image = itemView.findViewById(R.id.image);
            upVotes = itemView.findViewById(R.id.upVotes);
            downVotes = itemView.findViewById(R.id.downVotes);
            upVoteButton = itemView.findViewById(R.id.upVoteButton);
            downVoteButton = itemView.findViewById(R.id.downVoteButton);
            chipGroup = itemView.findViewById(R.id.chipGroup);
            settings = itemView.findViewById(R.id.threadSettings);
            container = itemView.findViewById(R.id.container);

            upVoteButton.setOnClickListener(v -> voteManager.voteForum(VoteManager.UP_VOTE));
            downVoteButton.setOnClickListener(v -> voteManager.voteForum(VoteManager.DOWN_VOTE));

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            final Intent intent = new Intent(activity, ForumActivity.class);
            new Thread(new Runnable() {
                @Override
                synchronized public void run() {
                    while (true) {
                        if (firestoreTask.isComplete()) {
                            final String forumString = new Gson().toJson(forum);
                            intent.putExtra("forum-string", forumString);
                            intent.putExtra("user-name", name.getText().toString());
                            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                            //intent.putExtra("image", bitmap);
                            Transitions.startActivity(activity, intent, header);

                            break;
                        }
                    }
                }
            }).start();
        }

        @Override
        public boolean onLongClick(View v) {
            Toast.makeText(activity, "long click", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
