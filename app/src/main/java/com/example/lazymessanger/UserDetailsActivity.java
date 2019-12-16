package com.example.lazymessanger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import controlers.ForumLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import models.User;

public class UserDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ForumLoader forumLoader;

    CircleImageView profileImage;
    TextView userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        profileImage = findViewById(R.id.user_image);
        userName = findViewById(R.id.user_name);

        Intent intent = getIntent();

        recyclerView = findViewById(R.id.forum_container);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        Bundle extras = intent.getExtras();
        if (extras != null) {
            User user = (User) extras.get("user");

            if (user != null) {

                FirebaseStorage.getInstance()
                        .getReference("thumbnails")
                        .child(user.getUid())
                        .getDownloadUrl()
                        .addOnSuccessListener(uri -> Picasso.get().load(uri).into(profileImage));

                userName.setText(user.getName());

                forumLoader = new ForumLoader(this, this, savedInstanceState, recyclerView, null, null);
                forumLoader.loadUsersForums(user.getUid());
            }
        }
    }
}
