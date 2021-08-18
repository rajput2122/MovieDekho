package com.example.moviedekho.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviedekho.repositories.MostPopularTvShowsRepository;
import com.example.moviedekho.responses.TVShowsResponse;

public class MostPopularTvShowsViewModel extends ViewModel {
    private MostPopularTvShowsRepository mostPopularTvShowsRepository;

    public MostPopularTvShowsViewModel(){
        mostPopularTvShowsRepository = new MostPopularTvShowsRepository();
    }

    public LiveData<TVShowsResponse> getMostPopularTvShows(int page){
         return mostPopularTvShowsRepository.getMostPopularTvShows(page);
    }
}
