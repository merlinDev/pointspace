package adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lazymessanger.R;
import com.example.lazymessanger.ThreadRegisterActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.Content;
import models.DraftForum;

public class DraftForumAdapter extends RecyclerView.Adapter<DraftForumAdapter.ViewHolder> {

    private Context context;
    private List<DraftForum> draftForums;

    public DraftForumAdapter(Context context, List<DraftForum> draftForums) {
        this.context = context;
        this.draftForums = draftForums;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.draft_forum_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DraftForum draft = draftForums.get(position);

        holder.draftForum = draft;

        Gson gson = new Gson();
        List<Content> contents = gson.fromJson(draft.getContentList(), new TypeToken<List<Content>>() {
        }.getType());

        if (contents.size() > 0) {
            Content content = contents.get(0);

            holder.header.setText(draft.getHeader());
            if (content.getType().equals(Content.CONTENT_TEXT)) {
                holder.desc.setText(content.getContent());
            }
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE dd MMMM yyyy, HH:mm", Locale.US);
        String dateTime = dateFormat.format(new Date(draft.getTimeStamp()));

        holder.time.setText(dateTime);
    }

    @Override
    public int getItemCount() {
        return draftForums.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView header;
        private TextView desc;
        private TextView time;
        private DraftForum draftForum;

        ViewHolder(@NonNull View view) {
            super(view);

            header = view.findViewById(R.id.forum_header);
            desc = view.findViewById(R.id.description);
            time = view.findViewById(R.id.time);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ThreadRegisterActivity.class);
            intent.putExtra("draft", draftForum);
            context.startActivity(intent);
        }
    }
}
