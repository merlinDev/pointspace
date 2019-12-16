package fragments;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lazymessanger.HomeActivity;
import com.example.lazymessanger.R;

import broadcasts.InternetBroadcastReceiver;
import controlers.ForumLoader;

public class HomeFragment extends Fragment implements LifecycleOwner {

    private HomeActivity activity;
    private InternetBroadcastReceiver broadcastReceiver;
    private IntentFilter intentFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(10);

        ProgressBar progressBar = view.findViewById(R.id.loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        SwipeRefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);

        activity = (HomeActivity) getActivity();

        ForumLoader forumLoader = new ForumLoader(this, activity, savedInstanceState, recyclerView, refreshLayout, progressBar);
        forumLoader.loadForumFromDatabase();

        refreshLayout.setOnRefreshListener(forumLoader::loadForumFromDatabase);

        FrameLayout frameLayout = view.findViewById(R.id.info);
        broadcastReceiver = new InternetBroadcastReceiver(forumLoader, frameLayout, inflater);
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        activity.registerReceiver(broadcastReceiver, intentFilter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        activity.unregisterReceiver(broadcastReceiver);
    }
}
