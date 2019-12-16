package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.lazymessanger.R;

import java.util.List;

import adapters.DraftForumAdapter;
import models.DraftForum;
import room.DraftForumDatabase;

public class DraftForumsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draft_forums, container, false);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.draft_recycler);
        recyclerView.setLayoutManager(layoutManager);

        if (getContext() != null) {
            new Thread(() -> {
                DraftForumDatabase database = Room.databaseBuilder(getContext(), DraftForumDatabase.class, "draft-forums").build();
                List<DraftForum> draftForums = database.forumDao().getAll();
                DraftForumAdapter adapter = new DraftForumAdapter(getContext(), draftForums);
                getActivity().runOnUiThread(() -> recyclerView.setAdapter(adapter));
            }).start();
        }


        return view;
    }
}
