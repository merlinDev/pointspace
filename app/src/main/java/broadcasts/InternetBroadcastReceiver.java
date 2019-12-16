package broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.example.lazymessanger.R;

import controlers.ForumLoader;
import customAnimators.CustomAnimator;

public class InternetBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "InternetBCR";

    private boolean firstRun = true;

    private ForumLoader forumLoader;
    private FrameLayout frameLayout;
    private LayoutInflater inflater;

    public InternetBroadcastReceiver() {
    }

    public InternetBroadcastReceiver(ForumLoader forumLoader, FrameLayout frameLayout, LayoutInflater inflater) {
        this.forumLoader = forumLoader;
        this.frameLayout = frameLayout;
        this.inflater = inflater;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.d(TAG, "onReceive: action ::: " + action);

        if (action != null && action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean internet = checkInternet(context);

            Log.d(TAG, "onReceive: internet ::: " + internet);

            if (!internet) {
                View view = inflater.inflate(R.layout.no_internet_layout, null);
                frameLayout.addView(view);
                frameLayout.setVisibility(View.VISIBLE);
                CustomAnimator.popupAnimate(frameLayout);
                firstRun = false;
            } else {
                if (!firstRun) {
                    frameLayout.setVisibility(View.GONE);
                    forumLoader.loadForumFromDatabase();
                }
            }
        }
    }

    private boolean checkInternet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }

}
