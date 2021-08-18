package com.example.moviedekho.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviedekho.databases.TVShowsDatabase;
import com.example.moviedekho.models.TVShow;
import com.example.moviedekho.repositories.TVShowDetailsRepository;
import com.example.moviedekho.responses.TVShowDetailsResponse;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class TVShowDetailsViewModel extends AndroidViewModel {
    private TVShowDetailsRepository tvShowDetailsRepository;
    private TVShowsDatabase tvShowsDatabase;

    public TVShowDetailsViewModel(Application application){
        super(application);
        tvShowDetailsRepository = new TVShowDetailsRepository();
        tvShowsDatabase = TVShowsDatabase.getTvShowsDatabase(application);
    }

    public LiveData<TVShowDetailsResponse> getTVShowDetails(String tvShowId){
        return tvShowDetailsRepository.getTVShowDetails(tvShowId);
    }

    public Completable addToWatchList(TVShow tvShow){
        return tvShowsDatabase.tvShowsDao().addToWatchList(tvShow);
    }

    public Flowable<TVShow> getTVShowFromWatchList(String tvShowId){
        return tvShowsDatabase.tvShowsDao().getTVShowFromWatchList(tvShowId);
    }

    public Completable removeTVShowFromWatchList(TVShow tvShow){
        return tvShowsDatabase.tvShowsDao().removeFromWatchList(tvShow);
    }
}
