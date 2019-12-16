package com.example.lazymessanger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

import alerts.ImageSelectDialog;
import controlers.ImageManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePictureActivity extends AppCompatActivity {

    private static final String TAG = "ProfilePictureActivity";
    private Bitmap bitmap;

    private PushbackInputStream originalImageStream;
    private PushbackInputStream thumbnailStream;

    CircleImageView imageView;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);

        imageView = findViewById(R.id.profile_pic);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    public void showDialog(View view) {
        new ImageSelectDialog(this).show(getSupportFragmentManager(), "select method");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: called. request code : " + requestCode);

        if (requestCode == ImageSelectDialog.IMAGE_CAPTURE_REQUEST || requestCode == ImageSelectDialog.GALLERY_CAPTURE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "onActivityResult: capturing image. ");
                if (data != null) {
                    Log.d(TAG, "onActivityResult: data exists.");

                    switch (requestCode) {
                        case ImageSelectDialog.IMAGE_CAPTURE_REQUEST: {

                            Bundle extras = data.getExtras();
                            if (extras != null) {
                                bitmap = (Bitmap) extras.get("data");
                                break;
                            }
                        }

                        case ImageSelectDialog.GALLERY_CAPTURE_REQUEST: {
                            if (data.getData() != null) {
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        }
                    }

                    try {
                        if (bitmap != null) {
                            FileInputStream inputStream = ImageManager.getBitmapStream(this, bitmap, "image");
                            originalImageStream = new PushbackInputStream(inputStream);

                            // setting image
                            imageView.setImageBitmap(bitmap);

                            Bitmap bitmapThumb = Bitmap.createScaledBitmap(bitmap, 250, 250, false);
                            FileInputStream thumbFileStream = ImageManager.getBitmapStream(this, bitmapThumb, "thumbImage");
                            thumbnailStream = new PushbackInputStream(thumbFileStream);

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        }
    }

    public void completeProfile(View view) {
        if (auth.getCurrentUser() != null && originalImageStream != null && thumbnailStream != null) {
            ImageManager.uploadProfileImage(auth.getUid(), originalImageStream, thumbnailStream);
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    /**
     * @throws NullPointerException by not uploading image, the Firebase storage returns an exception. (404)
     * although, this not affects the app.
     */
    public void skipImage(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
