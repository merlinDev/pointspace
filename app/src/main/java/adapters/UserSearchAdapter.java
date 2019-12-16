package adapters;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lazymessanger.R;
import com.example.lazymessanger.UserDetailsActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import models.User;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {

    private static final String TAG = "UserSearchAdapter";

    private List<User> users;
    private Activity activity;

    public UserSearchAdapter(Activity activity) {
        this.activity = activity;
        this.users = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_search_result_layout, parent, false);
        return new ViewHolder(view);
    }

    public void addItems(List<User> newUsers) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return users.size();
            }

            @Override
            public int getNewListSize() {
                return newUsers.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return users.get(oldItemPosition).getUid().equals(newUsers.get(newItemPosition).getUid());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return users.get(oldItemPosition).equals(newUsers.get(newItemPosition));
            }
        });

        users.clear();
        users.addAll(newUsers);
        diffResult.dispatchUpdatesTo(this);
    }

    public void clearItems() {
        int last = users.size();
        users.clear();
        notifyItemRangeRemoved(0, last);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);

        Log.d(TAG, "onBindViewHolder: user : " + user.getName());

        holder.userName.setText(user.getName());

        FirebaseStorage.getInstance().getReference("thumbnails")
                .child(user.getUid())
                .getDownloadUrl()
                .addOnSuccessListener(uri -> Picasso.get().load(uri).into(holder.userImage));

        holder.view.setOnClickListener(view -> {
            Intent intent = new Intent(activity, UserDetailsActivity.class);
            intent.putExtra("user", user);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView userName;
        private View view;
        ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            userImage = view.findViewById(R.id.user_image);
            userName = view.findViewById(R.id.user_name);
        }
    }
}
