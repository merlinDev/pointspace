package controlers;

import android.content.Context;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import models.NotificationPayload;

public class NotificationManager {

    private static final String TAG = "NotificationManager";


    private static final String TO = "to";
    private static final String PRIORITY = "priority";
    private static final String DATA = "data";

    //notification body
    private static final String TITLE = "title";
    private static final String BODY = "body";

    private static final String url = "https://fcm.googleapis.com/fcm/send";
    private Context context;
    private String uid;

    public NotificationManager(Context context, String uid) {
        this.context = context;
        this.uid = uid;
    }

    public void sendNotification(NotificationPayload notificationPayload) {

        String title = notificationPayload.getTitle();
        String text = notificationPayload.getText();
        String priority = notificationPayload.getPriority();

        FirebaseFirestore.getInstance()
                .collection("api_keys")
                .document("server")
                .get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String key = (String) documentSnapshot.get("key");

                FirebaseFirestore.getInstance().collection(UserManager.COLLECTION_NOTIFICATIONS)
                        .whereEqualTo("uid", uid)
                        .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty() && queryDocumentSnapshots.size() == 1) {
                        DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String token = (String) snapshot.get("token");

                        try {
                            JSONObject messageBody = new JSONObject();
                            messageBody.put(TITLE, title);
                            messageBody.put(BODY, text);

                            JSONObject payload = new JSONObject();
                            payload.put(TO, token);
                            payload.put(PRIORITY, priority);
                            payload.put(DATA, messageBody);

                            JsonObjectRequest request = new JsonObjectRequest(
                                    url,
                                    payload,
                                    response -> Log.d(TAG, "sendNotification: " + response), error -> {

                            }) {
                                @Override
                                public Map<String, String> getHeaders() {

                                    HashMap<String, String> data = new HashMap<>();
                                    data.put("Authorization", "key=" + key);
                                    data.put("Content-Type", "application/json");

                                    return data;
                                }
                            };

                            Volley.newRequestQueue(context).add(request);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }
}
