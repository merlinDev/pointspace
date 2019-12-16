package controlers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class PermissionManager {

    public static final int STORAGE_PERMISSION_REQUEST = 355;
    public static final int LOCATION_PERMISSION_REQUEST = 986;
    public static final int CAMERA_PERMISSION_REQUEST = 643;
    private static final String TAG = "PermissionManager";

    public static boolean checkPermission(Context context, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }
}
