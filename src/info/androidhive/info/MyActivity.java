package info.androidhive.info;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MyActivity extends Activity {

    GPSTracker gps;
    // Google Map
    private GoogleMap googleMap;
    private MapView mapView;
    private RelativeLayout mapLayout;
    private LatLng currentLocation;
    private String address;
    private String city;
    private String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        checkGooglePlayService();

        mapLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        showMap(savedInstanceState);


    }

    private void checkGooglePlayService() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if (result == ConnectionResult.SUCCESS) {
            Log.d("result:", "success");
        } else {
            GooglePlayServicesUtil.getErrorDialog(result, this, 1).show();
            Log.d("result:", "fail");
        }
    }

    private void showMap(Bundle savedInstanceState) {
        try {
            MapsInitializer.initialize(getBaseContext());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL);
        options.zoomControlsEnabled(true);
        mapView = new MapView(this, options);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.setEnabled(true);
        gps = new GPSTracker(MyActivity.this);

        googleMap = mapView.getMap();
        if (googleMap != null) {
           /* MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Hello Maps ");
            googleMap.addMarker(marker);*/

            googleMap.setMyLocationEnabled(true);

            // check if GPS enabled
            if (gps.canGetLocation()) {
                Location location = gps.getLocation();
                Log.d("location", "Location: " + location);
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                Log.d("Position", "latitude: " + latitude);
                Log.d("Position", "longitude: " + longitude);
                focusCurrentLocation(latitude, longitude);
                // \n is for new line
                // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
            //focusCurrentLocation();
        }
        mapLayout.addView(mapView);
    }

    private void focusCurrentLocation(double latitude, double longitude) {
        try {
            getAddress(latitude, longitude);
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentLocation = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .title(address)
                        .snippet(city)
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }

    private void getAddress(double latitude, double longitude) throws IOException {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        addresses = geocoder.getFromLocation(latitude, longitude, 1);
        Log.d("test", "address: " + addresses.toString());
        Log.d("test", "address!: " + addresses.get(0).toString());
        address = addresses.get(0).getAddressLine(0);
        city = addresses.get(0).getAddressLine(1);
        country = addresses.get(0).getAddressLine(2);
    }

}
