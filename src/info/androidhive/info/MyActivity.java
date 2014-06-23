package info.androidhive.info;

import android.app.Activity;
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

public class MyActivity extends Activity {

    GPSTracker gps;
    // Google Map
    private GoogleMap googleMap;
    private MapView mapView;
    private RelativeLayout mapLayout;
    private LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        checkGooglePlayService();

        mapLayout= (RelativeLayout) findViewById(R.id.relativeLayout);
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
        mapView = new MapView(this,options);
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
            if(gps.canGetLocation()){
                Location location=gps.getLocation();
                Log.d("location","Location: "+location);
                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                Log.d("Position","latitude: "+latitude);
                Log.d("Position","longitude: "+longitude);
                focusCurrentLocation(latitude, longitude);
                // \n is for new line
               // Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            }else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }
            //focusCurrentLocation();
        }
        mapLayout.addView(mapView);
    }

    private void focusCurrentLocation(double latitude,double longitude) {
        currentLocation = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("You are Here")
        .snippet("location is cool")
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }

}
