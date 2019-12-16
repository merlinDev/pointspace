package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lazymessanger.R;

import controlers.ForumLoader;

public class BookmarkFragment extends Fragment implements LifecycleOwner {

    private static final boolean FOR_BOOKMARKS = true;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookmark, container, false);

        recyclerView = view.findViewById(R.id.bookmark_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.refreshLayout);

        loadBookmarks(savedInstanceState);
        return view;
    }

    private void loadBookmarks(Bundle savedInstanceState) {
        ForumLoader forumLoader = new ForumLoader(
                this,
                getActivity(),
                savedInstanceState,
                recyclerView,
                refreshLayout,
                null,
                FOR_BOOKMARKS);

        forumLoader.loadForumFromDatabase();
    }
}
