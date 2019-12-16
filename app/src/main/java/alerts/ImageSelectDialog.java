package alerts;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.lazymessanger.R;

import controlers.PermissionManager;

public class ImageSelectDialog extends DialogFragment {

    private static final String TAG = "ImageSelectDialog";

    private Activity activity;

    private final static int PERMISSION_CAMERA = 111;
    private final static int PERMISSION_GALLERY = 222;
    public final static int IMAGE_CAPTURE_REQUEST = 1;
    public final static int GALLERY_CAPTURE_REQUEST = 2;


    public ImageSelectDialog(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.image_choose_array, (dialog, which) -> {
            switch (which) {
                case 0: {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!PermissionManager.checkPermission(getActivity(), Manifest.permission.CAMERA)) {
                            activity.requestPermissions(
                                    new String[]{Manifest.permission.CAMERA},
                                    PERMISSION_CAMERA
                            );
                        } else {
                            takePicture();
                        }
                        Log.d(TAG, "onClick: requesting permission camera");
                    } else {
                        takePicture();
                    }
                    break;
                }

                case 1: {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (!PermissionManager.checkPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            activity.requestPermissions(
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PERMISSION_GALLERY
                            );
                        } else {
                            getGallery();
                        }

                        Log.d(TAG, "onClick: requesting permission storage");
                    } else {
                        getGallery();
                    }
                    break;
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(TAG, "onRequestPermissionsResult: called.");

        switch (requestCode) {
            case PERMISSION_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                    Log.d(TAG, "onRequestPermissionsResult: takePicture() called.");
                }
            }

            case PERMISSION_GALLERY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getGallery();
                    Log.d(TAG, "onRequestPermissionsResult: getGallery() called.");
                }
            }
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getActivity() != null) {
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                activity.startActivityForResult(intent, IMAGE_CAPTURE_REQUEST);
                Log.d(TAG, "takePicture: camera activity started.");
            }
        }
    }

    private void getGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (getActivity() != null) {
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                activity.startActivityForResult(Intent.createChooser(intent, "select an image."), GALLERY_CAPTURE_REQUEST);
                Log.d(TAG, "getGallery: gallery activity started.");
            }
        }
    }
}
