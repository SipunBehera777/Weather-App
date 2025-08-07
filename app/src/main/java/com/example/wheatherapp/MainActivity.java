package com.example.wheatherapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.wheatherapp.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//a31197638d04551b39445b896bc87797

public class MainActivity extends AppCompatActivity {
ActivityMainBinding binding;
List<weather> weatherList;
    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LottieAnimationView animationView = findViewById(R.id.lottieAnimationView);
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MainActivity.this.fetchData(query);
                return true;
            }

            private void fetchData() {
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
    private String formatTime(long timestamp) {
        Date date = new Date(timestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
}
    private void fetchData(String cityName) {
        RetrofitInstance.getInstance().api
                .getWeatherdata(cityName, "a31197638d04551b39445b896bc87797")
                .enqueue(new Callback<weather>() {
                    @Override
                    public void onResponse(Call<weather> call, Response<weather> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            weather w=response.body();
                            Double temperature=w.getMain().getTemp();
                            Integer humidity=w.getMain().getHumidity();
                            Double wind=w.getWind().getSpeed();
                            binding.city.setText(w.getName());


                            Integer seaLevel=w.getMain().getSea_level();

                            String sunriseT= formatTime(w.getSys().getSunrise());
                            String sunsetT=formatTime(w.getSys().getSunset());

                            binding.sunrise.setText(sunriseT);
                            binding.sunset.setText(sunsetT);
                            binding.humidity.setText(humidity+"%");
                            binding.windSpeed.setText(wind+" m/s");
                            binding.seaLevel.setText(seaLevel+"hpa");
                            // Set min/max temps
                            Double max=w.getMain().getTemp_max();
                            double maxc = max- 273.15;
                            String displayText1 = String.format(" %.2f °C", maxc);
                            binding.textView3.setText("Max: " + displayText1);

                            Double min=w.getMain().getTemp_min();
                            double minc = min- 273.15;
                            String dis2 = String.format(" %.2f °C", minc);
                            binding.textView4.setText("Min: " + dis2);

                            String condition = w.getWeather().get(0).getMain();
                            binding.condition.setText(condition);
                            binding.textView2.setText(condition);

                            // Background changes here
                            setBackgroundBasedOnCondition(condition);


                            // Convert to Celsius
                            double tempCelsius = temperature- 273.15;

                            String displayText = String.format(" %.2f °C", tempCelsius);
                            binding.temp.setText(displayText);


                            long timestampMillis = w.getDt() * 1000L;

                            // Convert to Date
                            Date date = new Date(timestampMillis);

                            // Format date
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                            String dateString = dateFormat.format(date); // e.g., "06 Aug 2025"
                            // Format day
                            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
                            String dayName = dayFormat.format(date); // e.g., "Wednesday"

                            // Display in UI (Example)

                            binding.textView6.setText(dateString);
                            binding.textView5.setText(dayName);


                        }
                        else {

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
                        }

                    }

                    @Override
                    public void onFailure(Call<weather> call, Throwable t) {
                        Log.e("api", "API call failed: " + t.getMessage());
                    }
                });



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
            case "Thunderstorm":
                //binding.main.setBackgroundResource(R.drawable.bg_thunderstorm);
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