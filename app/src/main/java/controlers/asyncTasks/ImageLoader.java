package controlers.asyncTasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import customAnimators.CustomAnimator;
import de.hdodenhof.circleimageview.CircleImageView;

public class ImageLoader extends AsyncTask<Uri, Bitmap, String> {

    private static final String TAG = "ImageLoader";
    private static final String PROFILE_IMAGES = "prifileImages";
    private static final String THUMBNAILS = "thumbnails";


    private WeakReference<CircleImageView> weakReference;

    public ImageLoader(CircleImageView imageView) {
        this.weakReference = new WeakReference<>(imageView);
    }

    @Override
    protected String doInBackground(Uri... uris) {

        Uri uri = uris[0];

        try {
            URL url = new URL(uri.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            publishProgress(bitmap);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Bitmap... values) {

        try {
            Bitmap bitmap = values[0];
            CircleImageView imageView = weakReference.get();
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            CustomAnimator.fadeAnimate(imageView);
        } catch (NullPointerException e) {
            Log.d(TAG, "onProgressUpdate: please wait... loading");
        }

        super.onProgressUpdate(values);
    }
}
