package controlers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

import id.zelory.compressor.Compressor;

public class ImageManager {

    private static final String TAG = "ImageManager";

    public static final String PROFILE_IMAGES = "profileImages";
    public static final String THUMBNAILS = "thumbnails";

    public static void uploadProfileImage(final String uid, final PushbackInputStream originalStream, final PushbackInputStream thumbnailStream) {

        // uploading
        FirebaseStorage.getInstance()
                .getReference(PROFILE_IMAGES)
                .child(uid)
                .putStream(originalStream)
                .addOnSuccessListener(taskSnapshot -> FirebaseStorage.getInstance()
                        .getReference(THUMBNAILS)
                        .child(uid)
                        .putStream(thumbnailStream)
                        .addOnSuccessListener(taskSnapshot1 -> {
                            try {
                                originalStream.close();
                                thumbnailStream.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }));


        Log.d(TAG, "uploadImage: image uploaded.");
    }

    public static FileInputStream getImageStream(Context context, Uri uri, String name) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

        File file = new Compressor(context).compressToFile(imageFile);
        fileOutputStream.flush();
        fileOutputStream.close();

        filesDir.delete();
        // final image input stream
        return new FileInputStream(file);
    }

    public static FileInputStream getBitmapStream(Context context, Bitmap bitmap, String name) throws IOException {
        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);

        File file = new Compressor(context).compressToFile(imageFile);
        fileOutputStream.flush();
        fileOutputStream.close();
        filesDir.delete();

        // final image input stream
        return new FileInputStream(file);
    }
}
