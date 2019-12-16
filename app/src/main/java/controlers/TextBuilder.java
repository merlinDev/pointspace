package controlers;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import controlers.asyncTasks.ForumLayoutBuilder;
import customAnimators.CustomAnimator;

public class TextBuilder {

    private String text;
    private int request;

    public TextBuilder(String text, int request) {
        this.text = text;
        this.request = request;
    }

    public void attachView(View child) {
        if (request == ForumLayoutBuilder.FOR_FORUM) {
            TextView textView = (TextView) child;
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
            CustomAnimator.fadeAnimate(textView);
        } else if (request == ForumLayoutBuilder.FOR_EDIT) {
            EditText editText = (EditText) child;
            editText.setText(text);
        }
    }
}
