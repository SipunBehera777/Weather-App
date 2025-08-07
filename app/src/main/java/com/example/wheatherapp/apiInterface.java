package com.example.wheatherapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface apiInterface {
    @GET("weather")
    Call<weather> getWeatherdata(
            @Query("q") String city,
            @Query("appid") String apiKey
    );
}
