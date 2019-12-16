package customAnimators;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.view.View;

import controlers.asyncTasks.TransitionActivity;


public class Transitions {
    private Transitions() {
    }

    public static void startActivity(final Activity activity, final Intent intent, View... views) {

        final Pair<View, String> pairs[] = new Pair[views.length];

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            for (int x = 0; x < views.length; x++) {
                pairs[x] = new Pair<>(views[x], views[x].getTransitionName());
            }
            new TransitionActivity(activity,intent).execute(pairs);
        }
    }

}
