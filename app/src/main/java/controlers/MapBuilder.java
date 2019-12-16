package controlers;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.lazymessanger.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import customAnimators.CustomAnimator;

public class MapBuilder {

    private Bundle savedInstanceState;
    private LatLng latLng;

    public MapBuilder(Bundle savedInstanceState, LatLng latLng) {
        this.savedInstanceState = savedInstanceState;
        this.latLng = latLng;
    }

    public void attachView(FrameLayout mapFrame) {

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 650);
        MapView mapView = mapFrame.findViewById(R.id.mapView);
        mapFrame.setLayoutParams(layoutParams);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(map -> {
            map.getUiSettings().setAllGesturesEnabled(false);

            MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.pin));
            markerOptions.position(latLng);

            mapView.setVisibility(View.VISIBLE);
            CustomAnimator.fadeAnimate(mapView);

            CameraPosition cameraPosition = CameraPosition.builder().target(latLng).zoom(15).build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

            map.addMarker(markerOptions);
            map.animateCamera(cameraUpdate);

            mapView.onResume();
        });
    }

}
