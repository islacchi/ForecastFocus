package com.example.forecastfocus;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forecastfocus.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private RecyclerView recyclerView;
    private FloatingActionButton mFab;
    private boolean dataFetched = false;
    private String currentCity = "";
    ProgressBar pb;
    int counter = 0;
    EditText etCity, etCountry;
    TextView tvResult;
    private final String url = "https://api.weatherbit.io/v2.0/forecast/daily";
    private final String apiKey = "24d63c1605a246f6aa04f13095c3fa7e"; // Weather API key
    /*
    Reserve API key
    private final String apiKey = "24d63c1605a246f6aa04f13095c3fa7e";
     */
    DecimalFormat df = new DecimalFormat("#.##");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        tvResult = findViewById(R.id.tvResult);

        tvResult.setVisibility(View.GONE);
    }

    public void getWeatherDetails(View view) {

        String city = etCity.getText().toString().trim();
        String country = etCountry.getText().toString().trim();

        //CHANGES HERE
        if (!city.isEmpty() && !city.equals(currentCity)) {
            dataFetched = false;
            currentCity = city; // Update currentCity
        }
        if(city.isEmpty()){
            tvResult.setText("City field cannot be empty!");
        } else {
            String tempUrl = url + "?city=" + city + "&key=" + apiKey;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, tempUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String output = "";
                    Log.d("WeatherResponse", response);
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");

                        Calendar calendar = Calendar.getInstance();
                        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

                        String[] daysOfWeek = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};



                        for (int i = 0; i < jsonArray.length(); i++) {
                            int nextDayIndex = (currentDayOfWeek + i) % 7;
                            JSONObject dayData = jsonArray.getJSONObject(i);
                            JSONObject weather = dayData.getJSONObject("weather");
                            String description = weather.getString("description");
                            double temp = dayData.getDouble("temp");
                            double windSpeed = dayData.getDouble("wind_spd");
                            int humidity = dayData.getInt("rh");
                            double pressure = dayData.getDouble("pres");

                            output += "\n     "+daysOfWeek[nextDayIndex].toUpperCase() + ":"
                                    + "\n\n              Description:   " + description
                                    + "\n              Temperature:   " + df.format(temp) + "°C"
                                    + "\n              Wind Speed:   " + df.format(windSpeed) + " m/s"
                                    + "\n              Humidity:   " + humidity + "%"
                                    + "\n              Pressure:   " + df.format(pressure) + " hPa"
                                    + "\n\n";
                        }
                        if (jsonArray.length() > 0) {
                            // Data found, show tvResult and set text
                            tvResult.setVisibility(View.VISIBLE);
                            tvResult.setText(output);
                            dataFetched = true;
                        } else {
                            // No data found, hide tvResult and show message
                            tvResult.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "No weather data found for this location.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, error -> Toast.makeText(getApplicationContext(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show());

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
    //CHANGES HERE
    @Override
    protected void onResume() {
        super.onResume();
        if (dataFetched) {
            tvResult.setVisibility(View.VISIBLE);
        } else {
            // If data not fetched, hide tvResult
            tvResult.setVisibility(View.GONE);
        }
    }
}
