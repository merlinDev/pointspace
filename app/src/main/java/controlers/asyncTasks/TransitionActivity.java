package controlers.asyncTasks;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Pair;
import android.view.View;

import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;

public class TransitionActivity extends AsyncTask<Pair<View, String>, Pair<View, String>, String> {


    private WeakReference<Activity> weakReference;
    private Intent intent;

    public TransitionActivity(Activity activity, Intent intent) {
        weakReference = new WeakReference<>(activity);
        this.intent = intent;
    }


    @SafeVarargs
    @Override
    protected final String doInBackground(Pair<View, String>... pairs) {
        publishProgress(pairs);
        return null;
    }

    @SafeVarargs
    @Override
    protected final void onProgressUpdate(Pair<View, String>... values) {
        super.onProgressUpdate(values);
        Activity activity = weakReference.get();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(activity, values);
            activity.startActivity(intent, activityOptions.toBundle());
            activity.getWindow().setExitTransition(null);
        } else {
            activity.startActivity(intent);
        }
    }
}
