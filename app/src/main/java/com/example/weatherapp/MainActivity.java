package com.example.weatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private TextView tempText, cityNameText, humText, windyText, descripText;
    private EditText cityNameInput;
    private Button refreshButton;
    private static final String API_KEY = "031efa5a6c98539005ac72f714794d39";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cityNameText = findViewById(R.id.CityText);
        refreshButton = findViewById(R.id.search_button);
        cityNameInput = findViewById(R.id.location_input);
        tempText = findViewById(R.id.temperature);
        humText = findViewById(R.id.weather_humidity);
        windyText = findViewById(R.id.weather_wind);
        descripText = findViewById(R.id.weather_description);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = cityNameInput.getText().toString();
                if(!cityName.isEmpty()){
                    FetchWeatherData(cityName);
                }else{
                    cityNameInput.setError("enter a city name");
                }
            }
        });

    }
    private void FetchWeatherData(String cityName){
        String url = "https:api.openweathermap.org/data/2.5/weather?q="+cityName + "&appid="+API_KEY+"&units=metric";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(()-> {

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                runOnUiThread(() -> updateUI(result));
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    private void updateUI(String result){

        if(result != null){

            try{
                JSONObject jsonObject = new JSONObject(result);
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double humidity = main.getDouble("humidity");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                cityNameText.setText(jsonObject.getString("name"));
                tempText.setText(String.format("Temperature: %.0f°C", temperature));
                humText.setText(String.format("Humidity: %.0f%%", humidity));
                windyText.setText(String.format("Wind Speed: %.0f km/h", windSpeed));
                descripText.setText(description);


            }catch(JSONException e){
                e.printStackTrace();
            }

        }
    }

}