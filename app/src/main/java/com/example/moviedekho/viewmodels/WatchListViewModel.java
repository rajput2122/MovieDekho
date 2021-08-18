package com.example.moviedekho.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.moviedekho.databases.TVShowsDatabase;
import com.example.moviedekho.models.TVShow;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class WatchListViewModel extends AndroidViewModel {

    private TVShowsDatabase tvShowsDatabase;

    public WatchListViewModel(@NonNull Application application){
        super(application);
        tvShowsDatabase = TVShowsDatabase.getTvShowsDatabase(application);
    }

    public Flowable<List<TVShow>> loadWatchList(){
        return tvShowsDatabase.tvShowsDao().getWatchList();
    }

    public Completable removeTVShowFromWatchList(TVShow tvShow){
        return tvShowsDatabase.tvShowsDao().removeFromWatchList(tvShow);
    }

}
