package controlers;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import customAnimators.CustomAnimator;

public class ImageBuilder {

    private Uri uri;

    public ImageBuilder(Uri uri) {
        this.uri = uri;
    }

    public void attachView(ImageView imageView) {
        Picasso.get().load(uri).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                imageView.setVisibility(View.VISIBLE);
                CustomAnimator.fadeAnimate(imageView);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }
}
