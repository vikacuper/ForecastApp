package com.example.vikaguskaya.forecastapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<Weather> weatherForecast = new ArrayList<Weather>();
    WeatherAdapter forecastAdapter;
    ListView forecastListView;
    Place currentPlace;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forecastAdapter = new WeatherAdapter(this, weatherForecast);
        forecastListView = (ListView) findViewById(R.id.listForecast);
        forecastListView.setAdapter(forecastAdapter);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                URL url = createURL(
                        new Double(place.getLatLng().latitude).toString(),
                        new Double(place.getLatLng().longitude).toString());

                if (url != null) {
                    TextView view= findViewById(R.id.cityTextView);
                    view.setText(place.getAddress());
                    GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                    getLocalWeatherTask.execute(url);


                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                // Log.i(TAG, "An error occurred: " + status);
            }
        });
    }
    private URL createURL(String lat, String lon) {
        String apiKey = getString(R.string.api_key);
        String baseUrl = getString(R.string.web_service_url);

        try {

           // String urlString="http://api.openweathermap.org/data/2.5/forecast?lat="+ lat + "&lon=" + lon +"&units=metric&appid=1741e6988f79605331e54b75bdbaa7d2";
            String urlString=baseUrl+"lat="+ lat + "&lon=" + lon +"&units=metric&appid="+apiKey;
            return new URL(urlString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null; // URL was malformed
    }
    private class GetWeatherTask
            extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) params[0].openConnection();
                int response = connection.getResponseCode();

                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();

                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()))) {

                        String line;

                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    catch (IOException e) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                R.string.read_error, Toast.LENGTH_SHORT);
                        toast.show();
                        e.printStackTrace();
                    }

                    return new JSONObject(builder.toString());
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            R.string.connect_error, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            catch (Exception e) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.connect_error, Toast.LENGTH_SHORT);
                toast.show();

                e.printStackTrace();
            }
            finally {
                connection.disconnect(); // close the HttpURLConnection
            }

            return null;
        }

        // process JSON response and update ListView
        @Override
        protected void onPostExecute(JSONObject weather) {
            convertJSONtoArrayList(weather); // repopulate weatherList
            forecastAdapter.notifyDataSetChanged();
            forecastListView.smoothScrollToPosition(0);

        }
    }
    private void convertJSONtoArrayList(JSONObject forecast) {
        weatherForecast.clear(); // clear old weather data

        try {
            // get forecast's "list" JSONArray
            JSONArray list = forecast.getJSONArray("list");

            // convert each element of list to a Weather object
            for (int i = 0; i < list.length(); ++i)//5 days forecast
            {
                JSONObject day = list.getJSONObject(i); // get one day's data

                JSONObject main = day.getJSONObject("main");

                // get day's "weather" JSONObject for the description and icon
                JSONObject weather =
                        day.getJSONArray("weather").getJSONObject(0);

                // add new Weather object to weatherList

                weatherForecast.add(new Weather(
                        day.getLong("dt"), // date/time timestamp
                        main.getDouble("temp_min"), // minimum temperature
                        main.getDouble("temp_max"), // maximum temperature
                        main.getDouble("humidity"), // percent humidity
                        weather.getString("description"), // weather conditions
                        weather.getString("icon"),
                        forecast.getJSONObject("city").getString("id")
                )); // icon name
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "error", Toast.LENGTH_SHORT);
            toast.show();

        }
    }



}
