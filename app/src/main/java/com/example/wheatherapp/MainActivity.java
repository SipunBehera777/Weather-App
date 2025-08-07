package com.example.wheatherapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.wheatherapp.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Dialog dialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Fetch weather data for a given city
    private void fetchData(String cityName) {
        RetrofitInstance.getInstance().api
                .getWeatherdata(cityName, "a31197638d04551b39445b896bc87797")
                .enqueue(new Callback<weather>() {
                    @Override
                    public void onResponse(Call<weather> call, Response<weather> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            weather w = response.body();

                            Double temperature = w.getMain().getTemp();
                            Integer humidity = w.getMain().getHumidity();
                            Double wind = w.getWind().getSpeed();
                            Integer seaLevel = w.getMain().getSea_level();

                            String sunriseT = formatTime(w.getSys().getSunrise());
                            String sunsetT = formatTime(w.getSys().getSunset());

                            // UI Updates
                            binding.city.setText(w.getName());
                            binding.sunrise.setText(sunriseT);
                            binding.sunset.setText(sunsetT);
                            binding.humidity.setText(humidity + "%");
                            binding.windSpeed.setText(wind + " m/s");
                            binding.seaLevel.setText(seaLevel + " hPa");

                            // Min/Max temperature
                            Double max = w.getMain().getTemp_max();
                            Double min = w.getMain().getTemp_min();
                            String maxC = String.format("%.2f °C", max - 273.15);
                            String minC = String.format("%.2f °C", min - 273.15);

                            binding.textView3.setText("Max: " + maxC);
                            binding.textView4.setText("Min: " + minC);

                            // Condition
                            String condition = (w.getWeather() != null && !w.getWeather().isEmpty())
                                    ? w.getWeather().get(0).getMain()
                                    : "Unknown";
                            binding.condition.setText(condition);
                            binding.textView2.setText(condition);
                            setBackgroundBasedOnCondition(condition);

                            // Temperature
                            double tempCelsius = temperature - 273.15;
                            binding.temp.setText(String.format(" %.2f °C", tempCelsius));

                            // Date and Day
                            long timestampMillis = w.getDt() * 1000L;
                            Date date = new Date(timestampMillis);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                            binding.textView6.setText(dateFormat.format(date));
                            binding.textView5.setText(dayFormat.format(date));
                        } else {
                            clearWeatherData();




                        }
                    }

                    @Override
                    public void onFailure(Call<weather> call, Throwable t) {
                        Log.e("API_ERROR", "API call failed: " + t.getMessage());


                    }
                });
    }

    // Show custom dialog for city not found
    private void showCityNotFoundDialog() {
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_city_not_found);

        dialog.show();



    }


    private void clearWeatherData() {
        binding.city.setText("City not found");
        binding.temp.setText("");
        binding.textView2.setText("");
        binding.condition.setText("");
        binding.humidity.setText("");
        binding.windSpeed.setText("");
        binding.sunrise.setText("");
        binding.sunset.setText("");
        binding.seaLevel.setText("");
        binding.textView3.setText("");
        binding.textView4.setText("");
        binding.textView5.setText("");
        binding.textView6.setText("");
        showCityNotFoundDialog();
    }


    private String formatTime(long timestamp) {
        Date date = new Date(timestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    private void setBackgroundBasedOnCondition(String condition) {
        switch (condition) {
            case "Clear":
                binding.main.setBackgroundResource(R.drawable.bg_clear);
                binding.lottieAnimationView.setAnimation(R.raw.sun);
                break;
            case "Clouds":
                binding.main.setBackgroundResource(R.drawable.bg_clouds);
                binding.lottieAnimationView.setAnimation(R.raw.cloud);
                break;
            case "Rain":
            case "Drizzle":
                binding.main.setBackgroundResource(R.drawable.bg_rain);
                binding.lottieAnimationView.setAnimation(R.raw.rain);
                break;
            case "Snow":
                binding.main.setBackgroundResource(R.drawable.bg_snow);
                binding.lottieAnimationView.setAnimation(R.raw.snow);
                break;
            case "Mist":
            case "Haze":
            case "Fog":
                binding.main.setBackgroundResource(R.drawable.bg_mist);
                binding.lottieAnimationView.setAnimation(R.raw.mist);
                break;
            default:
                binding.main.setBackgroundResource(R.drawable.bg_default);
                binding.lottieAnimationView.setAnimation(R.raw.sunvibe);
                break;
        }
    }
}
