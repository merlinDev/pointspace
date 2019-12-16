package customAnimators;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

public class CustomAnimator {
    private CustomAnimator() {
    }

    public static synchronized void hideAnimate(View view) {

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, .0f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY, alpha);
        animator.setDuration(2000);
        animator.setInterpolator(new OvershootInterpolator());

        animator.start();
        view.setVisibility(View.INVISIBLE);
    }

    public static synchronized void popupAnimate(View view) {

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, .0f, 1f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY, alpha);
        animator.setDuration(300);
        animator.setInterpolator(new OvershootInterpolator());

        animator.start();
    }

    public static synchronized void popupHideAnimate(View view) {

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f, 0f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY, alpha);
        animator.setDuration(300);
        animator.setInterpolator(new OvershootInterpolator());

        animator.start();
    }

    public static synchronized void fadeAnimate(View view) {
        view.setVisibility(View.VISIBLE);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
        animator.setDuration(300);

        animator.start();
    }

    public static synchronized void moveX(View view) {
        float fromX = view.getX();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.X, 0f, fromX);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());

        animator.start();

    }

    public static synchronized void moveY(View view) {
        float fromY = view.getY();
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, View.Y, 0f, fromY);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();

    }
}
