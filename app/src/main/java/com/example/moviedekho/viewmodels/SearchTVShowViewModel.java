package com.example.moviedekho.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.moviedekho.repositories.MostPopularTvShowsRepository;
import com.example.moviedekho.repositories.SearchTVShowRepository;
import com.example.moviedekho.responses.TVShowsResponse;

public class SearchTVShowViewModel extends ViewModel {
    private SearchTVShowRepository searchTVShowRepository;

    public SearchTVShowViewModel(){
        searchTVShowRepository = new SearchTVShowRepository();
    }

    public LiveData<TVShowsResponse> searchTVShow(String query,int page){
         return searchTVShowRepository.searchTVShow(query,page);
    }
}
