package com.vorp.reachit;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapScreen extends AppCompatActivity {

    private MapView mapView;
    private LatLng location = new LatLng(34.052235,-118.243683);//TODO:doesnt need to be a global variable CHANGE THE COORDINATES
    private String TAG="TESTTEST";
    private MapboxMap map;
    private CameraPosition cameraPosition;
    private static String NAME_QUERY ="name";
    private static String LOCATION_QUERY ="latlong";
    private static String EMOJI_QUERY="emoji";
    private static DrawerLayout drawerLayout;
    private FirebaseFirestore db;
    private BottomSheetBehavior eventDrawerBehavior;
    private NestedScrollView eventScroller;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);
        drawerLayout = findViewById(R.id.drawer_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        LinearLayout eventDrawer=findViewById(R.id.eventSheet);
        eventDrawerBehavior = BottomSheetBehavior.from(eventDrawer);
        eventScroller = findViewById(R.id.eventScroller);
        Mapbox.getInstance(this, getString(R.string.map_token));

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        final LinearLayout eventContainer = (LinearLayout) findViewById(R.id.eventContainer);
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Location lastKnownLocation = null;
        drawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));

        /*
            TODO: ASK USER FOR MAP PERMISSIONS DIRECTLY
            gets permission to use gps
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                location.setLatitude(lastKnownLocation.getLatitude());
                location.setLongitude(lastKnownLocation.getLongitude());
            }
        }

        /*
            An asynchronous thread to setup map
         */
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;
                cameraPosition = new CameraPosition.Builder()
                        .target(location) // Sets the new camera position
                        .zoom(14) // Sets the zoom
                        .build();
                map.setCameraPosition(cameraPosition);
                map.addOnMapLongClickListener(eventCreateListener);

                db = FirebaseFirestore.getInstance();
                db.collection("Events")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            Context context = MapScreen.this;
                            Resources resources = context.getResources();
                            float dpHieght = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, resources.getDisplayMetrics());

                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String name = (String) document.get(NAME_QUERY);
                                        String emoji = (String) document.get(EMOJI_QUERY);
                                        GeoPoint geoPoint = (GeoPoint) document.get(LOCATION_QUERY);
                                        LatLng location = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

                                        EventView capturedEvent = new EventView(context,name,location,emoji);
                                        Space padding = new Space(context);
                                        padding.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 60));
                                        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int)dpHieght);
                                        layoutParams.setMargins(60,60,60,60);
                                        capturedEvent.setLayoutParams(layoutParams);
                                        eventContainer.addView(padding);
                                        eventContainer.addView(capturedEvent);
                                        capturedEvent.setBackgroundResource(R.drawable.event_card);
                                        capturedEvent.setOnClickListener(jumpEvent);

                                        map.addMarker(new MarkerOptions()
                                                .position(location)
                                                .title("\t"+emoji+"\n"+name)
                                        );
                                    }
                                } else {
                            /*
                                TODO: HANDLE EXCEPTION WITH USER
                             */
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
            }
        });
        }
    });


}

    private MapboxMap.OnMapLongClickListener eventCreateListener = new MapboxMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(@NonNull LatLng point) {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses=null;
            try {
                addresses = geocoder.getFromLocation(
                        point.getLatitude(),
                        point.getLongitude(),
                        1);
            }catch(IOException ioe){
                Toast.makeText(context,"Something went wrong",Toast.LENGTH_LONG).show();
            }
            String string = addresses.get(0).getAddressLine(0);
            Log.d("geocode",string);
            //Toast.makeText(context, string,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MapScreen.this, EventCreateActivity.class);
            intent.putExtra("LAT",point.getLatitude());
            intent.putExtra("LONG",point.getLongitude());
            intent.putExtra("ADDRESS",addresses.get(0).getAddressLine(0));
            startActivity(intent);
        }
    };

    private View.OnClickListener jumpEvent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            resetEventContainer();
            drawerLayout.closeDrawer(Gravity.START, false);
            EventView view = (EventView)v;
            CameraPosition newPosition = new CameraPosition.Builder()
                    .target(view.getLocation())
                    .zoom(14)
                    .build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(newPosition);
            map.animateCamera(cameraUpdate,2000);

        }
    };


    private void resetEventContainer() {
        eventDrawerBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        eventScroller.scrollTo(0,0);

    }

    @Override
    protected void onStart(){
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop(){
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        mapView.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mapView.onDestroy();
    }

    public GeoPoint getLocation()
    {
        return new GeoPoint(location.getLatitude(),location.getLongitude());
    }

}
