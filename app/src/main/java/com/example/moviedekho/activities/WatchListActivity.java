package com.example.moviedekho.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.moviedekho.R;
import com.example.moviedekho.adapters.WatchListAdapter;
import com.example.moviedekho.databinding.ActivityWatchListBinding;
import com.example.moviedekho.listeners.WatchListListener;
import com.example.moviedekho.models.TVShow;
import com.example.moviedekho.utilities.TempDataHolder;
import com.example.moviedekho.viewmodels.WatchListViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchListActivity extends AppCompatActivity implements WatchListListener {

    private ActivityWatchListBinding activityWatchListBinding;
    private WatchListViewModel viewModel;
    private WatchListAdapter watchListAdapter;
    private List<TVShow> watchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWatchListBinding = DataBindingUtil.setContentView(this,R.layout.activity_watch_list);
        doInitialization();
    }

    private void doInitialization() {
        viewModel = new ViewModelProvider(this).get(WatchListViewModel.class);
        activityWatchListBinding.imageBack.setOnClickListener(view -> onBackPressed());
        watchList = new ArrayList<>();
        loadWatchlist();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(TempDataHolder.IS_WATCHLIST_UPDATED){
            loadWatchlist();
            TempDataHolder.IS_WATCHLIST_UPDATED = false;
        }
        loadWatchlist();
    }

    private void loadWatchlist() {
        activityWatchListBinding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.loadWatchList().subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(tvShows -> {
            activityWatchListBinding.setIsLoading(false);
            if (watchList.size()>0){
                watchList.clear();
            }
            watchList.addAll(tvShows);
            watchListAdapter = new WatchListAdapter(watchList,this);
            activityWatchListBinding.watchListRecyclerView.setAdapter(watchListAdapter);
            activityWatchListBinding.watchListRecyclerView.setVisibility(View.VISIBLE);
            compositeDisposable.dispose();
        }));
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(),TVShowDetailsActivity.class);
        intent.putExtra("tvShow",tvShow);
        startActivity(intent);
    }

    @Override
    public void removeTVShowFromWatchList(TVShow tvShow, int position) {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.removeTVShowFromWatchList(tvShow).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    watchList.remove(position);
                    watchListAdapter.notifyItemRemoved(position);
                    watchListAdapter.notifyItemRangeChanged(position,watchListAdapter.getItemCount());
                    compositeDisposable.dispose();
                }));
    }
}