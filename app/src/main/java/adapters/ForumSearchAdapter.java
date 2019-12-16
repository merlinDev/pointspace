package adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lazymessanger.ForumActivity;
import com.example.lazymessanger.R;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import customAnimators.CustomAnimator;
import models.Content;
import models.Forum;

public class ForumSearchAdapter extends RecyclerView.Adapter<ForumSearchAdapter.ViewHolder> {

    private static final String TAG = "UserSearchAdapter";

    private List<Forum> forums;
    private Activity activity;

    public ForumSearchAdapter(Activity activity) {
        this.activity = activity;
        this.forums = new ArrayList<>();
    }

    public void addItems(List<Forum> newForums) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return forums.size();
            }

            @Override
            public int getNewListSize() {
                return newForums.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return forums.get(oldItemPosition).getId().equals(newForums.get(newItemPosition).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return forums.get(oldItemPosition).equals(newForums.get(newItemPosition));
            }

        });

        forums.clear();
        forums.addAll(newForums);
        diffResult.dispatchUpdatesTo(this);
    }

    public void clearItems() {
        int last = forums.size();
        forums.clear();
        notifyItemRangeRemoved(0, last);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.forum_search_result_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Forum forum = forums.get(position);
        holder.headerText.setText(forum.getHeader());
        holder.forumImage.setImageDrawable(null);
        ArrayList<Content> contents = forum.getContents();
        for (Content content : contents) {
            if (content.getType().equals(Content.CONTENT_TEXT)) {
                holder.desc.setText(content.getContent());
                break;
            }
        }

        for (Content content : contents) {
            if (content.getType().equals(Content.CONTENT_IMAGE)) {
                Uri uri = Uri.parse(content.getContent());
                Picasso.get().load(uri).into(holder.forumImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.forumImage.setVisibility(View.VISIBLE);
                        CustomAnimator.fadeAnimate(holder.forumImage);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.forumImage.setVisibility(View.GONE);
                    }
                });
                break;
            }
        }

        holder.view.setOnClickListener(view -> {
            final Intent intent = new Intent(activity, ForumActivity.class);
            final String forumString = new Gson().toJson(forum);
            intent.putExtra("forum-string", forumString);
            //intent.putExtra("image", bitmap);
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return forums.size();
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

        private ImageView forumImage;
        private TextView headerText;
        private TextView desc;
        private View view;

        ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            forumImage = view.findViewById(R.id.forum_image);
            headerText = view.findViewById(R.id.forum_header);
            desc = view.findViewById(R.id.forum_desc);
        }
    }
}
