package com.example.moviedekho.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.moviedekho.R;
import com.example.moviedekho.adapters.ImageSliderAdapter;
import com.example.moviedekho.databinding.ActivityTVShowDetailsBinding;
import com.example.moviedekho.models.TVShow;
import com.example.moviedekho.utilities.TempDataHolder;
import com.example.moviedekho.viewmodels.TVShowDetailsViewModel;
import com.google.gson.Gson;

import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {
    private ActivityTVShowDetailsBinding tvShowDetailsBinding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;
    private TVShow tvShow;
    private boolean isTVShowAvailableInWatchList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvShowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_t_v_show_details);
        doInitialization();
    }

    private void doInitialization() {
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        tvShowDetailsBinding.imageBackButton.setOnClickListener(view -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        checkTVShowInWatchList();
        getTVShowDetails();
    }

    private void checkTVShowInWatchList(){
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowDetailsViewModel.getTVShowFromWatchList(String.valueOf(tvShow.getId()))
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(tvShow1 -> {
            isTVShowAvailableInWatchList = true;
            tvShowDetailsBinding.imageWatchList.setImageResource(R.drawable.ic_added);
            compositeDisposable.dispose();
        }));
    }

    private void getTVShowDetails() {
        tvShowDetailsBinding.setIsLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());
        tvShowDetailsViewModel.getTVShowDetails(tvShowId).observe(this, tvShowDetailsResponse -> {
            tvShowDetailsBinding.setIsLoading(false);
            if (tvShowDetailsResponse.getTvShowDetails() != null) {
                if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                    loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                }
                tvShowDetailsBinding.setTvShowImageURL(tvShowDetailsResponse.getTvShowDetails().getImagePath());
                tvShowDetailsBinding.imageTVShow.setVisibility(View.VISIBLE);
                tvShowDetailsBinding.setDescription(
                        String.valueOf(
                                HtmlCompat.fromHtml(tvShowDetailsResponse.getTvShowDetails().getDescription(),
                                        HtmlCompat.FROM_HTML_MODE_LEGACY)
                        )
                );
                tvShowDetailsBinding.textDescription.setVisibility(View.VISIBLE);
                tvShowDetailsBinding.textReadMore.setVisibility(View.VISIBLE);
                tvShowDetailsBinding.textReadMore.setOnClickListener(view -> {
                    Log.d("Clicked", "getTVShowDetails: " + "Read More Clicked");
                    if (tvShowDetailsBinding.textReadMore.getText().toString().equals("Read More")) {
                        tvShowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                        tvShowDetailsBinding.textDescription.setEllipsize(null);
                        tvShowDetailsBinding.textReadMore.setText(R.string.read_less);
                    } else {
                        tvShowDetailsBinding.textDescription.setMaxLines(4);
                        tvShowDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                        tvShowDetailsBinding.textReadMore.setText(R.string.read_more);
                    }
                });
                tvShowDetailsBinding.setRating(
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())
                        )
                );
                if (tvShowDetailsResponse.getTvShowDetails().getGenres() != null) {
                    tvShowDetailsBinding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                } else {
                    tvShowDetailsBinding.setGenre("N/A");
                }
                tvShowDetailsBinding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRuntime() + "Min");
                tvShowDetailsBinding.viewDivider1.setVisibility(View.VISIBLE);
                tvShowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                tvShowDetailsBinding.viewDivider2.setVisibility(View.VISIBLE);
                tvShowDetailsBinding.buttonWebsite.setOnClickListener(view -> {
                    Intent intent =new Intent();
                    intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                    startActivity(intent);
                });
                tvShowDetailsBinding.buttonEpisodes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tv_showId = String.valueOf(tvShow.getId());
                        Intent intent =new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MovieDekho App");
                        intent.putExtra(Intent.EXTRA_TEXT,"https://www.example.com/main/" + tv_showId);
                        intent.setType("text/plain");
                        startActivity(intent);
                    }
                });
                tvShowDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
                tvShowDetailsBinding.buttonEpisodes.setVisibility(View.VISIBLE);
                tvShowDetailsBinding.imageWatchList.setOnClickListener(view -> {
                    CompositeDisposable compositeDisposable = new CompositeDisposable();
                    if(isTVShowAvailableInWatchList){
                        compositeDisposable.add(tvShowDetailsViewModel.removeTVShowFromWatchList(tvShow)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    isTVShowAvailableInWatchList = false;
                                    TempDataHolder.IS_WATCHLIST_UPDATED = false;
                                    tvShowDetailsBinding.imageWatchList.setImageResource(R.drawable.ic_watchlist);
                                    Toast.makeText(getApplicationContext(), "Removed from WatchList", Toast.LENGTH_SHORT).show();
                                    compositeDisposable.dispose();
                                })
                        );
                    }else{
                        compositeDisposable.add(tvShowDetailsViewModel.addToWatchList(tvShow)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                    tvShowDetailsBinding.imageWatchList.setImageResource(R.drawable.ic_added);
                                    Toast.makeText(getApplicationContext(), "Added to WatchList", Toast.LENGTH_SHORT).show();
                                    compositeDisposable.dispose();
                                })
                        );
                    }
                });
                tvShowDetailsBinding.imageWatchList.setVisibility(View.VISIBLE);
                loadBasicTvShowDetails();
            }
        });
    }


    private void loadImageSlider(String[] sliderImages) {

        tvShowDetailsBinding.sliderViewPager.setOffscreenPageLimit(1);
        tvShowDetailsBinding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        tvShowDetailsBinding.sliderViewPager.setVisibility(View.VISIBLE);
        tvShowDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setUpSliderIndicator(sliderImages.length);
        tvShowDetailsBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });

    }

    private void setUpSliderIndicator(int count) {
        ImageView[] indicator = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicator.length; i++) {
            indicator[i] = new ImageView(getApplicationContext());
            indicator[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive));
            indicator[i].setLayoutParams(layoutParams);
            tvShowDetailsBinding.layoutSliderIndicators.addView(indicator[i]);
        }
        tvShowDetailsBinding.layoutSliderIndicators.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = tvShowDetailsBinding.layoutSliderIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) tvShowDetailsBinding.layoutSliderIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive));
            }
        }
    }

    private void loadBasicTvShowDetails() {
        tvShowDetailsBinding.setTvShowName(tvShow.getName());
        tvShowDetailsBinding.setNetworkCountry(tvShow.getNetwork() + "(" +
                tvShow.getCountry() + ")");
        tvShowDetailsBinding.setStatus(tvShow.getStatus());
        tvShowDetailsBinding.setStartedDate(tvShow.getStartDate());
        tvShowDetailsBinding.textName.setVisibility(View.VISIBLE);
        tvShowDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        tvShowDetailsBinding.textStatus.setVisibility(View.VISIBLE);
        tvShowDetailsBinding.textStarted.setVisibility(View.VISIBLE);
    }

}




























