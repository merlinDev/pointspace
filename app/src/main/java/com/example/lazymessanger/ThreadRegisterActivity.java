package com.example.lazymessanger;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hootsuite.nachos.NachoTextView;
import com.hootsuite.nachos.terminator.ChipTerminatorHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import alerts.ToolBoxAlert;
import controlers.DraftForumController;
import controlers.ImageManager;
import controlers.PermissionManager;
import controlers.ViewMaker;
import controlers.asyncTasks.CategorySuggest;
import controlers.asyncTasks.ForumLayoutBuilder;
import models.Content;
import models.DraftForum;
import models.Forum;
import models.Tag;

public class ThreadRegisterActivity extends AppCompatActivity {

    private static final String TAG = "ThreadRegisterActivity";

    private static final int GALLERY_REQUEST_CODE = 985;
    private static final int CAMERA_REQUEST_CODE = 756;
    private static final int PLACE_REQUEST_CODE = 545;

    private static final int IMAGE_HEIGHT = 700;

    RelativeLayout forum_content;
    FrameLayout loading_content;
    Bundle savedInstanceState;

    FrameLayout description_parent;
    ImageButton close;

    EditText header;
    Button submit;
    NachoTextView chipView;
    ArrayAdapter<String> suggestAdapter;

    LinearLayout description_layout;

    private int privacy = 0;

    FirebaseAuth auth;
    CollectionReference collection;
    private String forumId;

    private ToolBoxAlert toolBoxAlert;

    int index = 0;

    //arrays
    ArrayList<Content> contents;
    private boolean isExpanded;

    private Forum editForum;
    private DraftForum draftForum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;
        toolBoxAlert = new ToolBoxAlert();

        setContentView(R.layout.activity_thread_register);
        collection = FirebaseFirestore.getInstance().collection(Forum.FORUMS);
        forumId = collection.document().getId();

        contents = new ArrayList<>();
        forum_content = findViewById(R.id.forum_content);
        loading_content = findViewById(R.id.complete_loading);

        description_parent = findViewById(R.id.description_parent);
        close = findViewById(R.id.collapseDesc);
        close.setOnClickListener(v -> collapseView());

        header = findViewById(R.id.forum_header);
        submit = findViewById(R.id.submit);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter
                .createFromResource(this,
                        R.array.privacy_choices,
                        R.layout.support_simple_spinner_dropdown_item);

        suggestAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);

        chipView = findViewById(R.id.chip_view);
        chipView.addChipTerminator(' ', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN);
        addCategoriesToAdapter();
        chipView.setAdapter(spinnerAdapter);

        auth = FirebaseAuth.getInstance();
        description_layout = findViewById(R.id.description_container);

        Intent intent = getIntent();

        Bundle data = intent.getExtras();
        if (data != null) {

            // if draft
            draftForum = (DraftForum) data.get("draft");
            if (draftForum != null) {
                checkDraft(draftForum);
            } else {
                // if editable
                editForum = (Forum) data.get("edit-forum");
                if (editForum != null) {

                    submit.setText(R.string.done);

                    header.setText(editForum.getHeader());
                    chipView.setText(editForum.getAbout());

                    new ForumLayoutBuilder(
                            this,
                            savedInstanceState,
                            description_layout,
                            editForum.getContents(),
                            ForumLayoutBuilder.FOR_EDIT
                    ).execute();
                }

            }

        } else addTextField();
    }

    private void checkDraft(DraftForum draftForum) {

        header.setText(draftForum.getHeader());

        Gson gson = new Gson();

        // setting draft tags
        List<String> tags = gson.fromJson(draftForum.getAboutList(), new TypeToken<List<String>>() {
        }.getType());
        chipView.setText(tags);

        // setting contents
        List<Content> draftContents = gson.fromJson(draftForum.getContentList(), new TypeToken<List<Content>>() {
        }.getType());
        new ForumLayoutBuilder(
                this,
                savedInstanceState,
                description_layout,
                draftContents,
                ForumLayoutBuilder.FOR_EDIT
        ).execute();

    }


    public void addEditText(View view) {
        addTextField();
        toolBoxAlert.dismiss();
    }

    // add new Text
    private void addTextField() {
        View lastChild = description_layout.getChildAt(description_layout.getChildCount() - 1);
        if (!(lastChild instanceof EditText)) {
            ViewMaker.addEditText(this, description_layout);
        }
    }


    // add new Image
    private void addImageField(Intent data) {

        View lastChild = description_layout.getChildAt(description_layout.getChildCount() - 1);
        if (lastChild instanceof EditText && ((EditText) lastChild).getText().toString().isEmpty()) {
            description_layout.removeView(lastChild);
        }

        FrameLayout imageLayout = ViewMaker.addImageContent(this, description_layout);
        ImageView imageView = imageLayout.findViewById(R.id.image);

        submit.setEnabled(false);
        submit.setText(R.string.wait);

        final Uri uri = data.getData();

        ImageButton removeButton = imageLayout.findViewById(R.id.remove);

        try {
            FileInputStream imageStream;

            if (uri != null) {
                imageStream = ImageManager.getImageStream(this, uri, "image");
                imageView.setImageURI(uri);
            } else {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageStream = ImageManager.getBitmapStream(this, bitmap, "image");
                imageView.setImageBitmap(bitmap);
            }

            final StorageReference storageReference = FirebaseStorage.getInstance()
                    .getReference(Forum.FORUM_IMAGES)
                    .child(forumId)
                    .child(String.valueOf(index));


            storageReference.putStream(imageStream)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                            .addOnSuccessListener(imageUri -> {
                                imageView.setTag(imageUri.toString());
                                index++;

                                Log.d(TAG, "onSuccess: image uploaded.");
                                submit.setText(R.string.submit_forum);
                                submit.setEnabled(true);

                                removeButton.setOnClickListener(v -> removeContent(imageLayout));

                            }));

        } catch (IOException e) {
            e.printStackTrace();
        }
        addTextField();
    }

    // add mapView
    private void addMapField(Intent data) {
        FrameLayout mapLayout = ViewMaker.addMapContent(this, description_layout);

        double lat = data.getDoubleExtra("place-latitude", 0);
        double lng = data.getDoubleExtra("place-longitude", 0);

        String latlngString = lat + "," + lng;
        LatLng latLng = new LatLng(lat, lng);

        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
        markerOptions.position(latLng);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 650);

        MapView mapView = mapLayout.findViewById(R.id.mapView);
        mapView.setLayoutParams(layoutParams);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(map -> {
            map.getUiSettings().setAllGesturesEnabled(false);
            try {
                MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json);
            } catch (Resources.NotFoundException e) {
                Log.d(TAG, "addMapField: " + e);
            }
            CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(15).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

            map.moveCamera(cameraUpdate);
            map.addMarker(markerOptions);

            mapView.onResume();
        });
        mapView.setTag(latlngString);

        ImageButton removeButton = mapLayout.findViewById(R.id.remove);
        removeButton.setVisibility(View.VISIBLE);
        removeButton.setOnClickListener(v -> removeContent(mapLayout));

        addTextField();
    }

    public void addImage(View view) {
        if (PermissionManager.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            getGallery();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PermissionManager.STORAGE_PERMISSION_REQUEST

                );
            }
        }
        toolBoxAlert.dismiss();
    }

    public void openCamera(View view) {

        if (PermissionManager.checkPermission(this, Manifest.permission.CAMERA)) {
            getCameraImage();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.CAMERA},
                        PermissionManager.CAMERA_PERMISSION_REQUEST

                );
            }
        }
        toolBoxAlert.dismiss();
    }

    // load trending categories
    private void addCategoriesToAdapter() {
        new CategorySuggest(this, chipView).execute();
    }


    // submit
    public void submitThread(View view) {

        if (auth.getCurrentUser() != null) {
            addContents();

            if (header.getText().toString().isEmpty()) {
                Toast.makeText(this, "please enter a header", Toast.LENGTH_SHORT).show();
                return;
            } else if (contents.isEmpty()) {
                Toast.makeText(this, "there are no any contents.", Toast.LENGTH_SHORT).show();
                return;
            }

            forum_content.setVisibility(View.GONE);
            loading_content.setVisibility(View.VISIBLE);
            submit.setEnabled(false);

            final List<String> aboutValues = chipView.getChipAndTokenValues();

            final CollectionReference aboutCollection = FirebaseFirestore.getInstance()
                    .collection(Forum.ABOUT_LIST);

            // adding about fields to database.
            new Thread(() -> {
                for (final String about : aboutValues) {
                    final Tag tag = new Tag(about);
                    aboutCollection
                            .whereEqualTo("about", tag.getAbout())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (queryDocumentSnapshots.isEmpty()) {

                                    aboutCollection.add(tag)
                                            .addOnSuccessListener(documentReference ->
                                                    Log.d(TAG, "onSuccess: tag field :" + about + " added to database."));
                                } else {
                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots
                                            .getDocuments()
                                            .get(0);

                                    Tag existTag = documentSnapshot.toObject(Tag.class);
                                    long count = existTag.getCount();

                                    aboutCollection
                                            .document(documentSnapshot.getId())
                                            .update("count", ++count)
                                            .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: tag count increased..."));
                                }
                            });
                }
            }).start();


            if (editForum != null) {

                this.collection
                        .whereEqualTo("id", editForum.getId())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot snapshot = queryDocumentSnapshots.getDocuments().get(0);
                                String forumId = snapshot.getId();

                                collection.document(forumId)
                                        .update(
                                                "header", header.getText().toString(),
                                                "contents", contents,
                                                "about", aboutValues,
                                                "privacy", privacy,
                                                "timeStamp", new Date().getTime()
                                        ).addOnSuccessListener(aVoid -> redirectToHome());
                            }
                        });

            } else {
                Forum forum = new Forum(
                        auth.getUid(),
                        header.getText().toString(),
                        contents,
                        aboutValues,
                        Forum.ANYONE
                );

                this.collection.add(forum)
                        .addOnSuccessListener(documentReference -> documentReference.update("id", forumId)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "onSuccess: forum id updated and saved.");
                                    redirectToHome();
                                }));
            }
        }

    }

    private void addContents() {
        int childCount = description_layout.getChildCount();

        for (int i = 0; i < childCount; i++) {

            Content content = null;

            View child = description_layout.getChildAt(i);
            if (child instanceof EditText) {
                String text = ((EditText) child).getText().toString();
                if (!text.isEmpty()) {
                    content = new Content(Content.CONTENT_TEXT, text);
                }
            } else if (child instanceof FrameLayout) {
                View view = ((FrameLayout) child).getChildAt(0);
                if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    String uri = (String) imageView.getTag();
                    if (uri != null) {
                        content = new Content(Content.CONTENT_IMAGE, uri);
                    }
                } else if (view instanceof MapView) {
                    MapView mapView = (MapView) view;

                    String latlngString = (String) mapView.getTag();
                    content = new Content(Content.CONTENT_LOCATION, latlngString);
                }
            }

            if (content != null) {
                contents.add(content);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionManager.STORAGE_PERMISSION_REQUEST: {
                getGallery();
                break;
            }

            case PermissionManager.CAMERA_PERMISSION_REQUEST: {
                getCameraImage();
                break;
            }
        }
    }

    private void getCameraImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    private boolean isLocationEnabled() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Snackbar snackbar = Snackbar.make(
                    findViewById(R.id.thread_register_parent),
                    "location services disabled.", Snackbar.LENGTH_LONG);

            snackbar.setAction("enable", v -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            });
            snackbar.show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                case CAMERA_REQUEST_CODE: {
                    if (data != null) {
                        addImageField(data);
                    }
                    break;
                }

                case PLACE_REQUEST_CODE: {
                    if (data != null) {
                        addMapField(data);
                    }
                    break;
                }
            }
        }

    }

    private void removeContent(View view) {


        int thisIndex = description_layout.indexOfChild(view);

        View aboveView = description_layout.getChildAt(thisIndex - 1);
        View belowView = description_layout.getChildAt(thisIndex + 1);

        if (aboveView instanceof EditText && belowView instanceof EditText) {
            String aboveText = ((EditText) aboveView).getText().toString();
            String belowText = ((EditText) belowView).getText().toString();

            aboveText += "\n" + belowText;
            ((EditText) aboveView).setText(aboveText);
            description_layout.removeView(belowView);
        }

        description_layout.removeView(view);
    }

    private void getGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    public void getLocation(View view) {
        Intent intent = new Intent(this, PlaceActivity.class);
        startActivityForResult(intent, PLACE_REQUEST_CODE);
        toolBoxAlert.dismiss();
    }

    private void redirectToHome() {
        Log.d(TAG, "onSuccess: forum submitted....");
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!isExpanded) {
            final String headerText = header.getText().toString();
            int contentSize = contents.size();

            int chipsSize = chipView.getAllChips().size();

            if (!headerText.isEmpty() || contentSize > 0 || chipsSize != 0) {
                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                View view = getLayoutInflater()
                        .inflate(R.layout.discard_alert, null);

                Button yes = view.findViewById(R.id.yes);
                Button no = view.findViewById(R.id.no);
                Button draft = view.findViewById(R.id.save_draft);

                yes.setOnClickListener(v -> {
                    alertDialog.dismiss();
                    ThreadRegisterActivity.super.onBackPressed();
                });

                no.setOnClickListener(v -> alertDialog.dismiss());

                draft.setOnClickListener(v -> {
                    addContents();
                    List<String> tags = chipView.getChipAndTokenValues();
                    DraftForumController.saveToDrafts(this, headerText, tags, contents);
                    alertDialog.dismiss();
                    ThreadRegisterActivity.super.onBackPressed();
                });

                alertDialog.setView(view);
                alertDialog.show();
            } else {
                super.onBackPressed();
            }

        } else {
            collapseView();
        }
    }

    public void expandView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout
                    .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            layoutParams.addRule(RelativeLayout.ABOVE, R.id.toolbox);
            layoutParams.removeRule(RelativeLayout.BELOW);
            description_parent.setLayoutParams(layoutParams);
            close.setVisibility(View.VISIBLE);
        }
    }

    public void collapseView() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        layoutParams.addRule(RelativeLayout.ABOVE, R.id.toolbox);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.header_box);
        description_parent.setLayoutParams(layoutParams);
        close.setVisibility(View.INVISIBLE);
        isExpanded = false;
    }

    public void openToolbox(View view) {
        toolBoxAlert.show(getSupportFragmentManager(), "toolbox");
    }
}
