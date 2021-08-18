package com.example.moviedekho.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.moviedekho.R;
import com.example.moviedekho.adapters.TVShowsAdapter;
import com.example.moviedekho.databinding.ActivitySearchBinding;
import com.example.moviedekho.listeners.TVShowsListener;
import com.example.moviedekho.models.TVShow;
import com.example.moviedekho.viewmodels.SearchTVShowViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements TVShowsListener {

    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int currentPage = 0;
    private int totalPages = 1;
    private ActivitySearchBinding activitySearchBinding;
    private SearchTVShowViewModel searchTVShowViewModel;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySearchBinding = DataBindingUtil.setContentView(this,R.layout.activity_search);
        doInitialization();
    }

    private void doInitialization() {
        activitySearchBinding.imageBack.setOnClickListener(view -> onBackPressed());
        activitySearchBinding.tvShowRecyclerView.setHasFixedSize(true);
        searchTVShowViewModel = new ViewModelProvider(this).get(SearchTVShowViewModel.class);
        tvShowsAdapter = new TVShowsAdapter(tvShows,this);
        activitySearchBinding.tvShowRecyclerView.setAdapter(tvShowsAdapter);
        activitySearchBinding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(timer!=null){
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().trim().isEmpty()){
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                currentPage=1;
                                totalPages=1;
                                searchTVShow(editable.toString());
                            });
                        }
                    },800);
                }else {
                    tvShows.clear();
                    tvShowsAdapter.notifyDataSetChanged();
                }
            }
        });
        activitySearchBinding.tvShowRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!activitySearchBinding.tvShowRecyclerView.canScrollVertically(1)){
                   if(!activitySearchBinding.inputSearch.getText().toString().isEmpty()){
                       if(currentPage < totalPages){
                           currentPage+=1;
                           searchTVShow(activitySearchBinding.inputSearch.getText().toString());
                       }
                   }
                }
            }
        });
        activitySearchBinding.inputSearch.requestFocus();
    }

    private void searchTVShow(String query) {
        toggleLoading();
        searchTVShowViewModel.searchTVShow(query,currentPage).observe(this, searchTVShowResponses -> {
            toggleLoading();
            if (searchTVShowResponses != null) {
                totalPages = searchTVShowResponses.getPages();
                if (searchTVShowResponses.getTvShows() != null) {
                    int oldCount = tvShows.size();
                    tvShows.addAll(searchTVShowResponses.getTvShows());
                    tvShowsAdapter.notifyItemRangeInserted(oldCount,tvShows.size());
                }
            }
        });
    }

    private void toggleLoading(){
        if(currentPage == 1){
            if(activitySearchBinding.getIsLoading()!=null && activitySearchBinding.getIsLoading()){
                activitySearchBinding.setIsLoading(false);
            }else {
                activitySearchBinding.setIsLoading(true);
            }
        }else{
            if(activitySearchBinding.getIsLoadingMore()!=null && activitySearchBinding.getIsLoadingMore()){
                activitySearchBinding.setIsLoadingMore(false);
            }else {
                activitySearchBinding.setIsLoadingMore(true);
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