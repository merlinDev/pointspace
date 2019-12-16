package controlers.asyncTasks;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lazymessanger.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.lang.ref.WeakReference;

import controlers.ImageBuilder;
import controlers.MapBuilder;
import controlers.TextBuilder;
import models.Content;

public class LayoutBuilder extends AsyncTask<Void, View, Void> {

    private WeakReference<View> layoutWeakReference;
    private WeakReference<Activity> activityWeakReference;
    private Content[] contents;
    private Bundle savedInstanceState;

    private WeakReference<TextView> textViewWeakReference;
    private WeakReference<FrameLayout> frameLayoutWeakReference;

    public LayoutBuilder(Activity activity, Bundle savedInstanceState, View container, Content... contents) {
        layoutWeakReference = new WeakReference<>(container);
        activityWeakReference = new WeakReference<>(activity);
        this.contents = contents;
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        View view = layoutWeakReference.get();
        textViewWeakReference = new WeakReference<>(view.findViewById(R.id.first_text));
        frameLayoutWeakReference = new WeakReference<>(view.findViewById(R.id.content_media));

    }

    @Override
    protected Void doInBackground(Void... voids) {

        Activity activity = activityWeakReference.get();

        TextView firstText = textViewWeakReference.get();
        FrameLayout layout = frameLayoutWeakReference.get();

        for (Content content : contents) {
            String type = content.getType();

            switch (type) {
                case Content.CONTENT_TEXT:
                    String text = content.getContent();
                    activity.runOnUiThread(() -> new TextBuilder(text, ForumLayoutBuilder.FOR_FORUM).attachView(firstText));
                    break;
                case Content.CONTENT_IMAGE:

                    ImageView imageView = (ImageView) LayoutInflater.from(activity).inflate(R.layout.image_view, null);

                    String uriString = content.getContent();
                    Uri uri = Uri.parse(uriString);
                    activity.runOnUiThread(() -> new ImageBuilder(uri).attachView(imageView));
                    publishProgress(layout, imageView);

                    break;
                case Content.CONTENT_LOCATION:

                    FrameLayout mapFrame = (FrameLayout) LayoutInflater.from(activity).inflate(R.layout.map_view_layout, null);

                    String[] latlngString = content.getContent().split(",");

                    double lat = Double.parseDouble(latlngString[0]);
                    double lng = Double.parseDouble(latlngString[1]);

                    LatLng latLng = new LatLng(lat, lng);

                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    builder.include(latLng);

                    activity.runOnUiThread(() -> new MapBuilder(savedInstanceState, latLng).attachView(mapFrame));
                    publishProgress(layout, mapFrame);

                    break;
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(View... values) {
        super.onProgressUpdate(values);
        FrameLayout layout = (FrameLayout) values[0];
        layout.addView(values[1]);
    }
}
