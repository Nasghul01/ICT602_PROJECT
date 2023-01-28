package com.example.ict602_project.ui.map;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ict602_project.AddMarker;
import com.example.ict602_project.R;
import com.example.ict602_project.databinding.FragmentMapBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.example.ict602_project.HazardMarker;

import java.util.Vector;

public class MapFragment extends Fragment {

    private MapViewModel mapViewModel;
    private FragmentMapBinding binding;

    //initialize variable
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    Location currentLocation;

    RequestQueue queue;
    final String URL = "https://bright-commas.000webhostapp.com/hazard_marker.php";
    Gson gson;
    HazardMarker[] hazard_markers;
    MarkerOptions marker;
    Vector<MarkerOptions> markerOptions;
    private GoogleMap mMap;

    FloatingActionButton btnAdd, btnRefresh;

    boolean doubleBackToExitPressedOnce = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        gson = new GsonBuilder().create();

        btnAdd = (FloatingActionButton) binding.btnAdd;
        btnRefresh = (FloatingActionButton) binding.btnRefresh;

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                getCurrentLocation();
                sendRequest();

            }
        });


        //start of map
        supportMapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);

        markerOptions = new Vector<>();

        client = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();
        } else {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        //end of map

        //Addmarker button
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), AddMarker.class);

                //pass the current location to the add marker class through intent
                i.putExtra("currentLocation", currentLocation);
                if(currentLocation != null)
                    startActivity(i);
                else
                    Toast.makeText(getContext(), "Unable to fetch GPS location.", Toast.LENGTH_LONG).show();

            }
        });

        return root;
    }

    //getcurrentlocation
    private void getCurrentLocation() {

//Initialize task location
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
//When success
                if (location != null) {
//sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
//Initialize Lat Lng
                            mMap = googleMap;
                            LatLng latLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
//Create marker options
                            MarkerOptions options = new MarkerOptions()
                                    .position(latLng)
                                    .title("I am here");
//zoom map scale 15
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            googleMap.addMarker(options);
                            currentLocation = location;
                            sendRequest();
                        }
                    });
                }
            }
        });
    }

    //getpermission
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                //Get current location when permission granted
                getCurrentLocation();
            }
        }
    }

    //sendrequest
    public void sendRequest(){
        queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, onSuccess, onError);
        queue.add(stringRequest);

    }

    //onsucess
    public Response. Listener<String> onSuccess = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            Log.d("HazardMarker","Number of Hazard_Marker Data Point: " + hazard_markers.length );

            hazard_markers = gson.fromJson(response, HazardMarker[].class);

            if(hazard_markers.length < 1){
                Toast.makeText(getContext(), "No Hazards Recorded!", Toast.LENGTH_LONG).show();
                return;
            }

            BitmapDrawable bitmapDrawable;

            for(HazardMarker info: hazard_markers){
                Double lat = Double.parseDouble(info.getLat());
                Double lng = Double.parseDouble(info.getLng());
                String title = info.getHazardname();
                String snippet = "Reported on " + info.getTime() + " by " + info.getReportedBy();

                //Toast.makeText(getContext(), "Snippet: " + snippet, Toast.LENGTH_LONG).show();

                marker = new MarkerOptions().title(title)
                        .position(new LatLng(lat,lng))
                        .title(title)
                        .snippet(snippet);
                //.icon(bitmapDescriptorFromVector(getContext(), R.drawable.ic_hazardicon));

                if(marker.getTitle().equals("Car Accident")){
                    bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.accident);
                }else if(marker.getTitle().equals("Car Breakdown")){
                    bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.car_breakdown);
                }else if(marker.getTitle().equals("Police")){
                    bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.police);
                }else if (marker.getTitle().equals("Landslide")){
                    bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.landslide);
                }else if (marker.getTitle().equals("Flood")){
                    bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.flood2);
                }else {
                    bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.under_construction);
                }
                Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapDrawable.getBitmap(), 150, 150, false);
                marker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                mMap.addMarker(marker);
                //Toast.makeText(getContext(), "marker: " + map.get(marker.getPosition()), Toast.LENGTH_LONG).show();

            } // end of for
//

        }//end of onresponse

    }; //end of sendrequest

    //error
    public Response.ErrorListener onError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    //ni untuk marker punya graphic
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectoriID){
        Drawable vectorDrawable  = ContextCompat.getDrawable(context, vectoriID);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap=Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //popup about who submit, date, type of hazard
    //popup untuk tau siapa submit , bila and apa
//    private void showPopup(String hazard, String user, String time){
//        Dialog dialog = new Dialog(getContext(), R.style.DialogStyle);
//        dialog.setContentView(R.layout.map_popup);
//
//        Ptitle      = (TextView)dialog.findViewById(R.id.txtTitle11);
//        Pusername   = (TextView)dialog.findViewById(R.id.txtUsername);
//        Pdatetime   = (TextView)dialog.findViewById(R.id.txtDateTime);
//        PbtnClose   = (Button)dialog.findViewById(R.id.btnOkay);
//
//        ImageView icon = (ImageView)dialog.findViewById(R.id.imgIcon1);
//
//        switch (hazard){
//            case "1" :
//                Ptitle.setText("Road Obstruction");
//                icon.setImageResource(R.drawable.c1_roadobstructioncircle);
//                break;
//            case "2" :
//                Ptitle.setText("Slippery Road");
//                icon.setImageResource(R.drawable.c2_slipperyroadcircle);
//                break;
//            case "3" :
//                Ptitle.setText("Dangerous Pothole");
//                icon.setImageResource(R.drawable.c3_potholecircle);
//                break;
//            case "4" :
//                Ptitle.setText("Traffic Accident");
//                icon.setImageResource(R.drawable.c4_trafficaccidentcircle);
//                break;
//        }
//
//        Pusername.setText("Reported By: " + user);
//        Pdatetime.setText("Timestamp: \n" + time);
//
//        PbtnClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }

    //@Override
//    public void onBackPressed() {
//        if (doubleBackToExitPressedOnce) {
//            this.onBackPressed();
//            return;
//        }
//
//        this.doubleBackToExitPressedOnce = true;
//        Toast.makeText(getContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
//
//        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                doubleBackToExitPressedOnce=false;
//            }
//        }, 2000);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}