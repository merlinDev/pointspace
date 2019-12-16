package controlers;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.lazymessanger.ProfilePictureActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import models.NotificationToken;
import models.User;

public class UserManager {

    private static final String TAG = "UserManager";

    public static final String COLLECTION_USER = "users";
    static final String COLLECTION_NOTIFICATIONS = "notification_tokens";

    private FirebaseFirestore database;
    private static FirebaseAuth auth;

    public static final String OK = "ok";
    public static final String USERNAME_F = "Please enter a valid username";
    public static final String FNAME_F = "first name";
    public static final String LNAME_F = "last name";
    public static final String MOBILE_F = "please enter a valid mobile number";
    public static final String PASSWORD_F = "please enter a valid password";

    public final static String USER_OBJECT = "userData";

    public UserManager() {
        this.database = FirebaseFirestore.getInstance();
    }

    public void addUser(final Activity activity, String email, String password, final String name) {

        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "onComplete auth: user saved.");

                if (task.getResult() != null) {
                    String uid = task.getResult().getUser().getUid();

                    final User user = new User(uid, name, User.USER_ACTIVATED);

                    Task<Void> userTask = database.
                            collection(COLLECTION_USER)
                            .document(auth.getUid())
                            .set(user);

                    userTask.addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "onComplete: user : " + user.getUid() + " registered");
                        registerMessagingToken(auth);
                        Intent intent = new Intent(activity, ProfilePictureActivity.class);
                        activity.startActivity(intent);
                        activity.finish();
                    });


                    userTask.addOnFailureListener(e -> {
                        Log.d(TAG, "onComplete: something went wrong while registering user : " + user.getUid());
                        e.printStackTrace();
                    });
                }

            } else {
                task.getException().printStackTrace();
                Toast.makeText(activity, "something is wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void registerMessagingToken(FirebaseAuth firebaseAuth) {
        FirebaseFirestore.getInstance().collection(COLLECTION_NOTIFICATIONS)
                .whereEqualTo("uid", firebaseAuth.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    // first time token
                    if (queryDocumentSnapshots.isEmpty()) {
                        FirebaseInstanceId.getInstance().getInstanceId()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        NotificationToken token = new NotificationToken(firebaseAuth.getUid(), task.getResult().getToken());
                                        database.collection(COLLECTION_NOTIFICATIONS)
                                                .add(token);
                                    }
                                });
                    } else {
                        DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String id = snapshot.getId();

                        FirebaseInstanceId.getInstance().getInstanceId()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        database.collection(COLLECTION_NOTIFICATIONS)
                                                .document(id)
                                                .update("token", task.getResult().getToken())
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d(TAG, "registerMessagingToken: token updated.");
                                                });
                                    }
                                });
                    }
                });


    }

}
