package controlers.asyncTasks;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lazymessanger.R;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.lang.ref.WeakReference;
import java.util.List;

import controlers.ImageBuilder;
import controlers.MapBuilder;
import controlers.TextBuilder;
import models.Content;

public class ForumLayoutBuilder extends AsyncTask<Void, View, Void> {

    public static final int FOR_FORUM = 0;
    public static final int FOR_EDIT = 1;

    private WeakReference<View> layoutWeakReference;
    private WeakReference<Activity> activityWeakReference;
    private List<Content> contents;
    private Bundle savedInstanceState;
    private int request;

    public ForumLayoutBuilder(Activity activity, Bundle savedInstanceState, View container, List<Content> contents, int request) {
        layoutWeakReference = new WeakReference<>(container);
        activityWeakReference = new WeakReference<>(activity);
        this.contents = contents;
        this.savedInstanceState = savedInstanceState;
        this.request = request;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        LinearLayout layout = (LinearLayout) layoutWeakReference.get();
        Activity activity = activityWeakReference.get();


        for (Content content : contents) {
            String type = content.getType();

            switch (type) {
                case Content.CONTENT_TEXT:
                    String text = content.getContent();

                    if (request == FOR_FORUM) {

                        TextView textView = (TextView) LayoutInflater.from(activity).inflate(R.layout.textview_layout, null);
                        activity.runOnUiThread(() -> new TextBuilder(text, FOR_FORUM).attachView(textView));
                        publishProgress(layout, textView);
                    } else if (request == FOR_EDIT) {

                        EditText editText = (EditText) LayoutInflater.from(activity).inflate(R.layout.edit_text_layout, null);
                        activity.runOnUiThread(() -> new TextBuilder(text, FOR_EDIT).attachView(editText));
                        publishProgress(layout, editText);
                    }


                    break;
                case Content.CONTENT_IMAGE:
                    String uriString = content.getContent();
                    Uri uri = Uri.parse(uriString);

                    if (request == FOR_FORUM) {
                        ImageView imageView = (ImageView) LayoutInflater.from(activityWeakReference.get()).inflate(R.layout.image_view, null);
                        activity.runOnUiThread(() -> new ImageBuilder(uri).attachView(imageView));
                        publishProgress(layout, imageView);

                    } else if (request == FOR_EDIT) {
                        FrameLayout frame = (FrameLayout) LayoutInflater.from(activity).inflate(R.layout.image_view_layout, null);
                        activity.runOnUiThread(() -> {
                            ImageView imageView = frame.findViewById(R.id.image);
                            imageView.setTag(uriString);
                            activity.runOnUiThread(() -> new ImageBuilder(uri).attachView(imageView));
                        });
                        publishProgress(layout, frame);

                        activity.runOnUiThread(() -> {
                            ImageButton remove = frame.findViewById(R.id.remove);
                            remove.setOnClickListener(view -> {

                                int thisIndex = layout.indexOfChild(frame);

                                View aboveView = layout.getChildAt(thisIndex - 1);
                                View belowView = layout.getChildAt(thisIndex + 1);

                                if (aboveView instanceof EditText && belowView instanceof EditText) {
                                    String aboveText = ((EditText) aboveView).getText().toString();
                                    String belowText = ((EditText) belowView).getText().toString();

                                    aboveText += "\n" + belowText;
                                    ((EditText) aboveView).setText(aboveText);
                                    layout.removeView(belowView);
                                }

                                layout.removeView(frame);
                            });
                        });
                    }

                    break;
                case Content.CONTENT_LOCATION:

                    if (request == FOR_FORUM) {
                        FrameLayout frame = (FrameLayout) LayoutInflater.from(activityWeakReference.get()).inflate(R.layout.map_view_layout, null);
                        activity.runOnUiThread(() -> {

                            MapView mapView = frame.findViewById(R.id.mapView);

                            String[] latlngString = content.getContent().split(",");

                            double lat = Double.parseDouble(latlngString[0]);
                            double lng = Double.parseDouble(latlngString[1]);
                            LatLng latLng = new LatLng(lat, lng);

                            LatLngBounds.Builder builder = LatLngBounds.builder();
                            builder.include(latLng);

                            new MapBuilder(savedInstanceState, latLng).attachView(mapView);
                        });
                        publishProgress(layout, frame);
                    }

                    break;
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(View... values) {
        super.onProgressUpdate(values);
        LinearLayout layout = (LinearLayout) values[0];
        layout.addView(values[1]);
    }
}
