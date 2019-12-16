package services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class NotificationService extends JobIntentService {

    private static final String TAG = "NotificationService";

    public static void enqueueWork(Context context, Intent intent, int jobId) {
        enqueueWork(context, NotificationService.class, jobId, intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "onHandleWork");
        new Thread(() -> {
            while (true) {
                Log.d(TAG, "onHandleWork: in background running................");
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onStopCurrentWork() {
        Log.d(TAG, "onStopCurrentWork");
        return super.onStopCurrentWork();
    }
}
