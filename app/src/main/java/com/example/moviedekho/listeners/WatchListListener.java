package com.example.moviedekho.listeners;

import com.example.moviedekho.models.TVShow;

public interface WatchListListener {

    void onTVShowClicked(TVShow tvShow);

    void removeTVShowFromWatchList(TVShow tvShow,int position);
}
