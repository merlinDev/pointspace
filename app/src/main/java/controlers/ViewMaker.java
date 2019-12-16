package controlers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lazymessanger.R;

public class ViewMaker {

    public static final int MAP_SIZE = 620;
    private static final String TAG = "ViewMaker";

    public static final String TEXT = "text";
    public static final String IMAGE = "image";
    public static final String LOCATION = "location";

    public static final int FORUM_CARD_LIMIT = 1;
    private static final int MAX_LINES = 3;

    public static TextView createTextView(Context context, String text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final int margin = context.getResources()
                .getDimensionPixelSize(R.dimen.layout_margin);

        layoutParams.setMargins(margin, margin, margin, margin);
        textView.setLayoutParams(layoutParams);

        return textView;
    }

    public static ImageView createImageView(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final int margin = context.getResources()
                .getDimensionPixelSize(R.dimen.layout_margin);

        layoutParams.setMargins(margin, margin, margin, margin);
        imageView.setLayoutParams(layoutParams);

        return imageView;
    }

    private static ImageView generateImageView(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setAdjustViewBounds(true);
        imageView.setLayoutParams(layoutParams);

        return imageView;
    }


    public static FrameLayout addImageContent(Context context, LinearLayout descriptionLayout) {

        FrameLayout layout = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.image_view_layout, null, true);
        descriptionLayout.addView(layout);

        return layout;
    }

    public static FrameLayout addMapContent(Context context, LinearLayout descriptionLayout) {

        FrameLayout layout = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.map_view_layout, null, true);
        descriptionLayout.addView(layout);

        return layout;
    }

    public static void addEditText(Context context, LinearLayout descriptionLayout) {

        EditText layout = (EditText) LayoutInflater.from(context).inflate(R.layout.text_only_layout, null, true);
        descriptionLayout.addView(layout);
    }
}
