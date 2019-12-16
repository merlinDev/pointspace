package com.example.lazymessanger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import adapters.TabAdapter;
import controlers.ImageManager;
import controlers.UserManager;
import de.hdodenhof.circleimageview.CircleImageView;
import fragments.BookmarkFragment;
import fragments.DraftForumsFragment;
import fragments.HomeFragment;
import models.User;


public class HomeActivity extends AppCompatActivity implements SensorEventListener {

    //Sensors
    SensorManager sensorManager;
    Sensor light;

    //home
    HomeFragment homeFragment;
    BookmarkFragment bookmarkFragment;
    DraftForumsFragment draftForumsFragment;

    //tab adapter
    TabAdapter tabAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;

    //fragmentManager
    private static final String HOME = "home";
    private static final String PROFILE = "profile";
    private static final String SEARCH = "search";
    private static final String SETTINGS = "settings";
    private static final String DRAFTS = "drafts";

    private static final int HOME_INDEX = 0;
    private static final int PROFILE_INDEX = 1;
    private static final int SEARCH_INDEX = 2;
    private static final int SETTINGS_INDEX = 3;

    private static int CURRENT = R.id.home;

    private FirebaseAuth auth;
    private CircleImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        profile = findViewById(R.id.profile);

        FirebaseStorage.getInstance()
                .getReference(ImageManager.THUMBNAILS)
                .child(auth.getUid())
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Picasso.get().load(uri).into(profile);
                });


        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tab_layout);
        tabAdapter = new TabAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        homeFragment = new HomeFragment();
        bookmarkFragment = new BookmarkFragment();
        draftForumsFragment = new DraftForumsFragment();

        tabAdapter.addFragment(homeFragment, TabAdapter.HOME);
        tabAdapter.addFragment(bookmarkFragment, TabAdapter.BOOKMARKS);
        tabAdapter.addFragment(draftForumsFragment, TabAdapter.DRAFTS);
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void addForum(MenuItem menuItem) {
        Intent intent = new Intent(this, ThreadRegisterActivity.class);
        startActivity(intent);
    }

    public void search(MenuItem menuItem) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.bottom_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        System.out.println("light ::::::::: " + event.values[0]);

        // changing the theme {shared preference, sensors}
        if (CustomApplication.THEME_NOT_SELECTED) {

            CustomApplication.THEME_NOT_SELECTED = false;

            final SharedPreferences sharedPreferences = getSharedPreferences(CustomApplication.THEME_SHARED_PREFERENCE, MODE_PRIVATE);
            final int theme = sharedPreferences.getInt(CustomApplication.THEME_SPECIFIED, AppCompatDelegate.MODE_NIGHT_UNSPECIFIED);

            float lightValue = event.values[0];


            if ((theme != AppCompatDelegate.MODE_NIGHT_YES) && lightValue <= CustomApplication.LIGHT_VALUE) {

                final androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);


                final AlertDialog alertDialog = builder.create();

                View view = getLayoutInflater()
                        .inflate(R.layout.thme_change_dialog, null);

                final Button negative = view.findViewById(R.id.negative);
                final Button positive = view.findViewById(R.id.positive);
                final CheckBox checkBox = view.findViewById(R.id.checkbox);

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        positive.setEnabled(false);
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            positive.setEnabled(true);
                        }
                    }
                });

                negative.setOnClickListener(v -> {

                    if (checkBox.isChecked()) {
                        sharedPreferences
                                .edit()
                                .putInt(CustomApplication.THEME_SPECIFIED, AppCompatDelegate.MODE_NIGHT_NO)
                                .apply();

                    }
                    alertDialog.dismiss();
                });

                positive.setOnClickListener(v -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    alertDialog.dismiss();
                });

                alertDialog.setView(view);
                alertDialog.show();
                sensorManager.unregisterListener(HomeActivity.this);
            }
        } else {
            sensorManager.unregisterListener(HomeActivity.this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void changeTheme(MenuItem menuItem) {
        if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void goToProfile(View view) {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        FirebaseFirestore.getInstance()
                .collection(UserManager.COLLECTION_USER)
                .whereEqualTo("uid", auth.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                   if (!queryDocumentSnapshots.isEmpty()){
                       User user = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                       intent.putExtra("user", user);
                       startActivity(intent);
                   }
                });
    }
}
