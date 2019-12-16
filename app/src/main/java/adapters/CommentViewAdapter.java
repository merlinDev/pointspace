package adapters;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lazymessanger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import controlers.CommentCallback;
import controlers.ImageManager;
import controlers.asyncTasks.ImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Comment;
import models.User;

public class CommentViewAdapter extends RecyclerView.Adapter<CommentViewAdapter.CommentViewHolder> {

    private static final String TAG = "CommentViewAdapter";
    private static final String USERS = "users";

    private List<Comment> comments;
    private Activity activity;

    // firestore
    private FirebaseFirestore database;

    public CommentViewAdapter(Activity activity, List<Comment> comments) {
        this.activity = activity;
        this.comments = comments;

        database = FirebaseFirestore.getInstance();

        Log.d(TAG, "CommentViewAdapter: called.");
    }

    public void addItems(final List<Comment> newData) {
        try {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new CommentCallback(CommentViewAdapter.this.comments, newData));
            updateNew(newData);
            diffResult.dispatchUpdatesTo(CommentViewAdapter.this);
        } catch (IllegalStateException | IndexOutOfBoundsException e) {
            Log.d(TAG, "run: RecyclerView is computing...");
        }
    }

    private void updateNew(List<Comment> data) {
        this.comments.addAll(data);
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        System.out.println("viewholder created..................");

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentViewHolder holder, final int position) {

        System.out.println("binding comment :::::::::: " + comments.get(position));

        final Comment comment = comments.get(position);
        String uid = comment.getUid();

        Task<DocumentSnapshot> snapshotTask = database.collection(USERS)
                .document(uid)
                .get();

        snapshotTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {

                        holder.id = documentSnapshot.getId();
                        holder.name.setText(user.getName());
                        holder.comment.setText(comment.getComment());

                        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMMM yyyy, HH:mm", Locale.US);
                        String dateTime = dateFormat.format(new Date(comment.getTimeStamp()));

                       // holder.time.setText(dateTime);

                        StorageReference storageReference = FirebaseStorage.getInstance()
                                .getReference(ImageManager.THUMBNAILS)
                                .child(comment.getUid());

                        Task<Uri> task = storageReference.getDownloadUrl();

                        task.addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                // using httpUrlConnection
                                new ImageLoader(holder.image).execute(task.getResult());

                                // using 3rd party lib
                                //Picasso.get().load(task.getResult()).into(holder.image);

                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        String id;
        TextView name;
        TextView time;
        TextView comment;

        CircleImageView image;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            //time = itemView.findViewById(R.id.time);
            comment = itemView.findViewById(R.id.comment);

            image = itemView.findViewById(R.id.profile_pic);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String[] extras = {"forum-key", id};
            //Transitions.startActivity(activity, ForumActivity.class, extras, question);
        }
    }
}
