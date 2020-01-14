package com.vorp.reachit;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;

/**
 * Created by asd on 2018-07-07.
 */

public class FirestoreHelper {

    private static ArrayList<Event> events = new ArrayList<Event>();

    private static String NAME_QUERY ="name";
    private static String LOCATION_QUERY ="latlong";
    private static String EMOJI_QUERY="emoji";
    private String TAG="TESTTEST";

    public boolean refreshEvents(final Context con)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    Context context = con;
                    Resources resources = context.getResources();
                    float dpHieght = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, resources.getDisplayMetrics());

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "task completed: "+task.getResult());
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = (String) document.get(NAME_QUERY);
                                String emoji = (String) document.get(EMOJI_QUERY);
                                GeoPoint geoPoint = (GeoPoint) document.get(LOCATION_QUERY);
                                LatLng location = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());

                                events.add(new Event(name,emoji,location));

                            }
                        } else {
                            /*
                                TODO: HANDLE EXCEPTION WITH USER
                             */
                            Log.d(TAG, "Error getting documents.", task.getException());
                        }
                    }

                });
        return true;
    }

    public ArrayList<Event>getEvents()
    {
        return events;
    }
}