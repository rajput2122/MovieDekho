package com.example.moviedekho.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.moviedekho.network.ApiClient;
import com.example.moviedekho.network.ApiService;
import com.example.moviedekho.responses.TVShowsResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MostPopularTvShowsRepository {
    private ApiService apiService;

    public MostPopularTvShowsRepository(){
        apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<TVShowsResponse> getMostPopularTvShows(int page){
        MutableLiveData<TVShowsResponse> data = new MutableLiveData<>();
        apiService.getMostPopularTvShows(page).enqueue(new Callback<TVShowsResponse>() {
            @Override
            public void onResponse(@NonNull Call<TVShowsResponse> call,@NonNull Response<TVShowsResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<TVShowsResponse> call,@NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
