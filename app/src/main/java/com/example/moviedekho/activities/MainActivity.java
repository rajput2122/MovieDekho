package com.example.moviedekho.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.moviedekho.R;
import com.example.moviedekho.adapters.TVShowsAdapter;
import com.example.moviedekho.databinding.ActivityMainBinding;
import com.example.moviedekho.listeners.TVShowsListener;
import com.example.moviedekho.models.TVShow;
import com.example.moviedekho.viewmodels.MostPopularTvShowsViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TVShowsListener {

    private MostPopularTvShowsViewModel viewModel;
    private ActivityMainBinding activityMainBinding;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int currentPage = 0;
    private int totalPages = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            List<String> parameters = data.getPathSegments();
        }
        doInitialization();
    }

    private void doInitialization() {
        activityMainBinding.moviesRecyclerview.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(MostPopularTvShowsViewModel.class);
        tvShowsAdapter = new TVShowsAdapter(tvShows,this);
        activityMainBinding.moviesRecyclerview.setAdapter(tvShowsAdapter);
        activityMainBinding.moviesRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(activityMainBinding.moviesRecyclerview.canScrollVertically(1)){
                    if(currentPage<=totalPages){
                        currentPage+=1;
                        getMostPopularTvShows();
                    }
                }
            }
        });
        activityMainBinding.imageWatchlist.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),WatchListActivity.class)));
        activityMainBinding.imageSearch.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),SearchActivity.class)));
        getMostPopularTvShows();
    }

    private void getMostPopularTvShows() {
        toggleLoading();
        viewModel.getMostPopularTvShows(currentPage).observe(this, mostPopularTvShowsResponse -> {
            toggleLoading();
            if (mostPopularTvShowsResponse != null) {
                totalPages = mostPopularTvShowsResponse.getPages();
                if (mostPopularTvShowsResponse.getTvShows() != null) {
                    int oldCount = tvShows.size();
                    tvShows.addAll(mostPopularTvShowsResponse.getTvShows());
                    tvShowsAdapter.notifyItemRangeInserted(oldCount,tvShows.size());
                }
            }
        });
    }

    private void toggleLoading(){
        if(currentPage == 1){
            if(activityMainBinding.getIsLoading()!=null && activityMainBinding.getIsLoading()){
                activityMainBinding.setIsLoading(false);
            }else {
                activityMainBinding.setIsLoading(true);
            }
        }else{
            if(activityMainBinding.getIsLoadingMore()!=null && activityMainBinding.getIsLoadingMore()){
                activityMainBinding.setIsLoadingMore(false);
            }else {
                activityMainBinding.setIsLoadingMore(true);
            }
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(),TVShowDetailsActivity.class);
        intent.putExtra("tvShow",tvShow);
        startActivity(intent);
    }
}