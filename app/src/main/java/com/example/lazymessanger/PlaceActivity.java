package com.example.lazymessanger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

public class PlaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private MapView mapView;
    private Place place;

    //fields
    private TextView placeText;
    private TextView addressText;
    private TextView telephoneText;
    private TextView websiteText;

    public static final int PLACE_REQUEST = 654;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        placeText = findViewById(R.id.place);
        addressText = findViewById(R.id.address);
        telephoneText = findViewById(R.id.telephone);
        websiteText = findViewById(R.id.website);

        if (!Places.isInitialized()) {
            FirebaseFirestore.getInstance()
                    .collection("api_keys")
                    .document("place_api")
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String key = (String) documentSnapshot.get("key");
                    Places.initialize(this, key);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_REQUEST) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    place = Autocomplete.getPlaceFromIntent(data);
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        map.clear();
                        CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(14).build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

                        map.animateCamera(cameraUpdate);

                        MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
                        markerOptions.position(latLng);
                        map.addMarker(markerOptions);

                        String placeName = place.getName();
                        placeText.setText(placeName);

                        if (place.getAddress() != null) {
                            String placeAddress = "Address : " + place.getAddress();
                            addressText.setText(placeAddress);
                        }

                        if (place.getPhoneNumber() != null) {
                            String placeTelephone = "Telephone : " + place.getPhoneNumber();
                            telephoneText.setText(placeTelephone);
                        }

                        if (place.getWebsiteUri() != null) {
                            String website = "Website : " + place.getWebsiteUri().toString();
                            websiteText.setText(website);
                        }
                    }
                }
            }
        }
    }

    public void showDialog(View view) {
        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.WEBSITE_URI,
                Place.Field.PHONE_NUMBER,
                Place.Field.LAT_LNG

        );

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
        startActivityForResult(intent, PLACE_REQUEST);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        mapView.onResume();
    }

    public void sendData(View view) {
        Intent intent = new Intent(this, ThreadRegisterActivity.class);
        intent.putExtra("place-id", place.getId());
        intent.putExtra("place-name", place.getName());
        intent.putExtra("place-latitude", place.getLatLng().latitude);
        intent.putExtra("place-longitude", place.getLatLng().longitude);
        intent.putExtra("place-address", place.getAddress());
        setResult(RESULT_OK, intent);
        finish();
    }
}
