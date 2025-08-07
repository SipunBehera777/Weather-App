package com.example.wheatherapp;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    public static RetrofitInstance instance;
    apiInterface api;
    RetrofitInstance(){
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api= retrofit.create(apiInterface.class);
    }
    public static RetrofitInstance getInstance(){
        if(instance==null){
            instance=new RetrofitInstance();
        }
        return instance;//passing retrofit instance if not create

    }
}
