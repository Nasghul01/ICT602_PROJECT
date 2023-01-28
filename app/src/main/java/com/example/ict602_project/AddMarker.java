package com.example.ict602_project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddMarker extends AppCompatActivity implements OnMapReadyCallback {

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    Button btnSubmit;
    EditText name, type, desc;

    RequestQueue queue;
    final String URL = "https://bright-commas.000webhostapp.com/insert_marker.php";

    RadioGroup radGrp;
    RadioButton radBtn1, radBtn2, radBtn3, radBtn4,radBtn5,radBtn6;
    EditText editText;
    String checkedHazardName = "";

    MapView mMapView;
    Location userLocation;
    LatLng globalUseLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        getSupportActionBar().setTitle("Add Marker");
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        radGrp = (RadioGroup) findViewById(R.id.grpRadio);
        radBtn1 = (RadioButton) findViewById(R.id.radID1);
        radBtn2 = (RadioButton) findViewById(R.id.radID2);
        radBtn3 = (RadioButton) findViewById(R.id.radID3);
        radBtn4 = (RadioButton) findViewById(R.id.radID4);
        radBtn5 = (RadioButton) findViewById(R.id.radID5);
        radBtn6 = (RadioButton) findViewById(R.id.radID6);
        editText = (EditText) findViewById(R.id.reporterName);

        radGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radID1:
                        checkedHazardName = "Car Accident";
                        break;
                    case R.id.radID2:
                        checkedHazardName = "Car Breakdown";
                        break;
                    case R.id.radID3:
                        checkedHazardName = "Police";
                        break;
                    case R.id.radID4:
                        checkedHazardName = "Landslide";
                        break;
                    case R.id.radID5:
                        checkedHazardName = "Flood";
                        break;
                    case R.id.radID6:
                        checkedHazardName = "Under Construction";
                        break;
                }
            }
        });

        queue = Volley.newRequestQueue(getApplicationContext());

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        //data passing
        Bundle extras = getIntent().getExtras();
        //if so, set all the edittext's and textview's based on values retrieved
        Location currentLocation = extras.getParcelable("currentLocation");
        userLocation = currentLocation;

        globalUseLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//        String userID = extras.getString("userID");
//        //remark: nanti pass user type sekali
//        String userType = extras.getString("userType");
        //String userType = "2";

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                //content of the edittext's

                //String hazardType = type.getText().toString();
                //String hazardDesc = desc.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = sdf.format(new Date());

                Date currentTime = Calendar.getInstance().getTime();

                String reportedBy = String.valueOf(editText);

                Time dummyTime = new Time(currentTime.getTime());

                // Marker newMarker = new Marker(currentLocation, hazardName, hazardType, hazardDesc, userID, dummyTime);

                //hold jap, buat read dulu
                makeRequest(checkedHazardName,currentDateandTime,currentLocation,reportedBy);

                Intent i = new Intent(AddMarker.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    //basically POST method
    public void makeRequest(String checkedHazardName, String currentDateandTime,Location currentLocation, String reportedBy ){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), "Connection OK!", Toast.LENGTH_SHORT).show();
            }
        }, errorListener){
            @Override
            protected Map<String, String> getParams (){
                //POST key and values
                Map<String, String> params = new HashMap<>();
                params.put("hazardname", checkedHazardName);
                params.put("time", String.valueOf(currentDateandTime));
                params.put("lat", String.valueOf(globalUseLocation.latitude));
                params.put("lng", String.valueOf(globalUseLocation.longitude));
                params.put("reportedBy",reportedBy);
                return params;
            }
        };

        queue.add(stringRequest);
        /* Toast.makeText(getApplicationContext(),
                "Hazard Reported!, Coor: " + String.valueOf(currentLocation.getLatitude()) + ", " +
                        String.valueOf(currentLocation.getLongitude())
                , Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),
                "\nHazard Reported!, hazard: " + hazardID
                , Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),
                "\nHazard Reported!, datetime: " + currentTime
                , Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),
                "\nHazard Reported!, userid: " + userID
                , Toast.LENGTH_LONG).show(); */

    }

    public Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                .title("Sini")
                .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_add_location_24)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 17));

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                googleMap.clear(); // Clear markers if there is a selection previously. note: ALSO DELETES CURRENT USER LOCATION MARKER
                googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_baseline_map_24)));

                globalUseLocation = latLng;
                //Toast.makeText(getApplicationContext(), "Map location: " + latLng, Toast.LENGTH_LONG).show();

                /*(AlertDialog.Builder builder = new AlertDialog.Builder(AddMarker.this);
                builder.setCancelable(true);
                builder.setTitle("Hazard Location");
                builder.setMessage("Set Location?");
                builder.setPositiveButton("Set",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                googleMap.clear(); // Clear markers if there is a selection previously. note: ALSO DELETES CURRENT USER LOCATION MARKER
                                googleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_hazardicon)));

                                globalUseLocation = latLng;

                                //Toast.makeText(getApplicationContext(), "Hazard location confirmed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do nothing bitch
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();*/
            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectoriID){
        Drawable vectorDrawable  = ContextCompat.getDrawable(context, vectoriID);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap= Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}