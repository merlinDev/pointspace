package fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lazymessanger.HomeActivity;
import com.example.lazymessanger.R;
import com.google.firebase.auth.FirebaseAuth;

import controlers.ForumLoader;

public class ProfileFragment extends Fragment implements LifecycleOwner {

    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        auth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView_profile);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ProgressBar progressBar = view.findViewById(R.id.loading_profile);

        HomeActivity activity = (HomeActivity) getActivity();
        ForumLoader forumLoader = new ForumLoader(this, activity, savedInstanceState, recyclerView, null, progressBar);

        forumLoader.loadUsersForums(auth.getUid());

        return view;
    }
}
