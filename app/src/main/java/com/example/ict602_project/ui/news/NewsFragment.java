package com.example.ict602_project.ui.news;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ict602_project.R;
import com.example.ict602_project.databinding.FragmentNewsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewsFragment extends Fragment {

    private FragmentNewsBinding binding;

    ListView list;

    Geocoder geocoder;

    List<Address> addresses;
    String address, URL = "https://bright-commas.000webhostapp.com/hazard_marker.php";

    RequestQueue getHazards;

    Double lat,lng;

    SimpleDateFormat dateFormatIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat dateFormatOut = new SimpleDateFormat("d MMM yyyy hh:mm a");
    Date dateIn;

    ProgressBar progress;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NewsViewModel homeViewModel =
                new ViewModelProvider(this).get(NewsViewModel.class);

        binding = FragmentNewsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        getHazards = Volley.newRequestQueue(getContext());
        progress = (ProgressBar) binding.newsProgressBar;

        ((Activity) getContext()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progress.setVisibility(View.VISIBLE);

        makeRequest();

        return root;


    }

    public void makeRequest() {
        StringRequest send = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);

                    String[] subtitle = new String[jsonArray.length()];
                    String[] maintitle = new String[jsonArray.length()];
                    Integer[] imgid = new Integer[jsonArray.length()];

                    if(jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            if (jsonObject.getString("hazardname").equals("Car Accident")) {
                                imgid[i] = R.drawable.accident;
                            }
                            else if (jsonObject.getString("hazardname").equals("Car Breakdown")) {
                                imgid[i] = R.drawable.car_breakdown;
                            }
                            else if (jsonObject.getString("hazardname").equals("Police")) {
                                imgid[i] = R.drawable.police;
                            }
                            else if (jsonObject.getString("hazardname").equals("Landslide")) {
                                imgid[i] = R.drawable.landslide;
                            }
                            else if(jsonObject.getString("hazardname").equals("Flood")){
                                imgid[i] = R.drawable.flood2;
                            }else{
                                imgid[i] = R.drawable.under_construction;}

                            dateIn = dateFormatIn.parse(jsonObject.getString("time"));
                            subtitle[i] = String.valueOf(dateFormatOut.format(dateIn));

                            lat = jsonObject.getDouble("lat");
                            lng = jsonObject.getDouble("lng");
                            addresses = geocoder.getFromLocation(lat, lng, 1);
                            //Log.d("Address " + jsonObject.getString("ReportID"), addresses.get(0).getAddressLine(0));
                            maintitle[i] = addresses.get(0).getAddressLine(0);
                        }
                    }

                    ListViewNews adapter = new ListViewNews(getContext(), maintitle, subtitle, imgid);
                    list = (ListView) binding.newsDashboard;
                    list.setAdapter(adapter);

                    ((Activity) getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progress.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, errorListener);
        getHazards.add(send);
    }

    public Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}