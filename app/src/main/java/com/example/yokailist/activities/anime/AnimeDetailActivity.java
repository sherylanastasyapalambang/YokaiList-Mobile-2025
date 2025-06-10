package com.example.yokailist.activities.anime;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.yokailist.R;
import com.example.yokailist.adapters.CharacterAdapter;
import com.example.yokailist.adapters.TopScoreAnimeAdapter;
import com.example.yokailist.data.network.ApiConfig;
import com.example.yokailist.data.network.ApiService;
import com.example.yokailist.data.response.AnimeDetailResponse;
import com.example.yokailist.data.response.CharacterResponse;
import com.example.yokailist.data.response.animedetail.Anime;
import com.example.yokailist.data.response.animedetail.GenresItem;
import com.example.yokailist.data.response.animedetail.LicensorsItem;
import com.example.yokailist.data.response.animedetail.StudiosItem;
import com.example.yokailist.data.response.character.DataItem;
import com.example.yokailist.databinding.ActivityAnimeDetailBinding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimeDetailActivity extends AppCompatActivity {
    ActivityAnimeDetailBinding binding;
    private boolean isSynopsisExpanded = false;
    private int animeId;
    private CharacterAdapter characterAdapter;
    private List<DataItem> characterList = new ArrayList<>();
    private String synopsis = "";
    private String synopsisFull = "";
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean isDataLoaded = false;
    private boolean isLoadingInProgress = false;
    private boolean isLoadingFailed = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable loadingRunnable;
    private static long lastApiCallTime = 0;
    private static final long MIN_API_CALL_INTERVAL = 200;
    private Call<AnimeDetailResponse> animeDetailCall;
    private Call<CharacterResponse> characterCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAnimeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        animeId = getIntent().getIntExtra("ANIME_ID", -1);

        binding.ibAddToMyList.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddToMyListActivity.class);
            intent.putExtra("ANIME_ID", animeId);
            this.startActivity(intent);
        });

        characterAdapter = new CharacterAdapter(this, characterList);
        binding.rvCharacters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvCharacters.setAdapter(characterAdapter);

        binding.ibBack.setOnClickListener(v -> {
            finish();
        });

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        setupNetworkCallback();

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.nestedScrollView.setVisibility(View.GONE);
        binding.llFailedToLoad.setVisibility(View.GONE);

        scheduleDataLoading();
    }

    private void loadAnimeDetail() {
        if (!isInternetAvailable()) {
            isLoadingInProgress = false;
            binding.progressBar.setVisibility(View.GONE);
            binding.llFailedToLoad.setVisibility(View.VISIBLE);
            return;
        }

        lastApiCallTime = System.currentTimeMillis();

        if (animeDetailCall != null) {
            animeDetailCall.cancel();
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.nestedScrollView.setVisibility(View.GONE);
        binding.llFailedToLoad.setVisibility(View.GONE);

        ApiService apiService = ApiConfig.getApiService();
        animeDetailCall = apiService.getAnimeDetail(animeId);

        animeDetailCall.enqueue(new Callback<AnimeDetailResponse>() {
            @Override
            public void onResponse(Call<AnimeDetailResponse> call, Response<AnimeDetailResponse> response) {
                binding.progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    binding.nestedScrollView.setVisibility(View.VISIBLE);
                    binding.llFailedToLoad.setVisibility(View.GONE);
                    isDataLoaded = true;
                    isLoadingFailed = false;

                    AnimeDetailResponse animeDetailResponse = response.body();
                    Anime anime = animeDetailResponse.getData();

                    String score;
                    String rank;
                    String popularity;
                    String members;
                    String title;
                    String type;
                    String status;
                    String epsDuration;
                    String genre;
                    String englishTitle;
                    String source;
                    String studio;
                    String rating;
                    String season;
                    String aired;
                    String licensor;


                    if (anime.getImages() != null) {
                        Glide.with(AnimeDetailActivity.this)
                                .load(anime.getImages().getWebp().getLargeImageUrl())
                                .into(binding.ivCover);
                    } else {
                        binding.ivCover.setImageResource(R.drawable.img_noimgpotrait);
                    }

                    if (anime.getScore() != 0.0 ) {
                        score = String.valueOf(anime.getScore());
                    } else {
                        score = "N/A";
                    }

                    if (anime.getRank() != 0 ) {
                        rank = "#" + anime.getRank();
                    } else {
                        rank = " - ";
                    }

                    if (anime.getPopularity() != 0 ) {
                        popularity = "#" + anime.getPopularity();
                    } else {
                        popularity = " - ";
                    }

                    if (anime.getMembers() != 0 ) {
                        members = NumberFormat.getNumberInstance(Locale.US).format(anime.getMembers());
                    } else {
                        members = " - ";
                    }

                    if (anime.getTitle() != null ) {
                        title = anime.getTitle();
                    } else {
                        title = " - ";
                    }

                    if (anime.getType() != null ) {
                        type = anime.getType();
                    } else {
                        type = " - ";
                    }

                    if (anime.getStatus() != null ) {
                        status = anime.getStatus();
                    } else {
                        status = " - ";
                    }

                    if (anime.getEpisodes() != 0 && anime.getDuration() != null ) {
                        epsDuration = anime.getEpisodes() + " ep, " + anime.getDuration();
                    } else {
                        if (anime.getEpisodes() != 0 && anime.getDuration() != null ) {
                            epsDuration = anime.getEpisodes() + " ep";
                        } else if (anime.getEpisodes() == 0 && anime.getDuration() != null) {
                            epsDuration = anime.getDuration();
                        } else {
                            epsDuration = " - ";
                        }
                    }

                    genre = formatGenres(anime.getGenres());

                    if (anime.getSynopsis() == null || anime.getSynopsis().isEmpty()) {
                        synopsis = " - ";
                        synopsisFull = " - ";
                    } else {
                        String getSynopsis = anime.getSynopsis()
                                .replace("\\n", "\n")
                                .replace("\\\"", "\"");

                        synopsisFull = getSynopsis;

                        if (getSynopsis.length() > 100) {
                            synopsis = getSynopsis.substring(0, 100).trim() + "...";
                        } else {
                            synopsis = getSynopsis;
                        }
                    }

                    binding.ibSynopsisDetail.setVisibility(View.VISIBLE);

                    if (synopsisFull.length() <= 100) {
                        binding.ibSynopsisDetail.setVisibility(View.GONE);
                    } else {
                        binding.ibSynopsisDetail.setVisibility(View.VISIBLE);
                    }

                    if (anime.getTrailer().getUrl() == null ) {
                        binding.flPlayTrailer.setVisibility(View.GONE);
                        binding.ivNoImage.setVisibility(View.GONE);
                        binding.ibTrailer.setVisibility(View.GONE);
                    } else {
                        binding.ibTrailer.setVisibility(View.VISIBLE);

                        String trailerUrl = anime.getTrailer().getUrl();

                        if (anime.getTrailer().getImages().getMaximumImageUrl() != null) {
                            Glide.with(AnimeDetailActivity.this)
                                    .load(anime.getTrailer().getImages().getMaximumImageUrl())
                                    .into(binding.ivTrailer);
                        } else if (anime.getTrailer().getImages().getImageUrl() != null) {
                            Glide.with(AnimeDetailActivity.this)
                                    .load(anime.getTrailer().getImages().getImageUrl())
                                    .into(binding.ivTrailer);
                        } else {
                            binding.ivTrailer.setImageResource(R.drawable.img_noimglandscape);
                        }

                        binding.ibTrailer.setOnClickListener(v -> {
                            String youtubeUrl = anime.getTrailer().getUrl();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl));
                            intent.setPackage("com.google.android.youtube");
                            try {
                                startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl)));
                            }
                        });
                    }

                    if (anime.getTitleEnglish() != null) {
                        englishTitle = anime.getTitleEnglish();
                    } else {
                        englishTitle = " - ";
                    }

                    if (anime.getSource() != null) {
                        source = anime.getSource();
                    } else {
                        source = " - ";
                    }

                    studio = formatStudio(anime.getStudios());

                    if (anime.getRating() != null) {
                        rating = anime.getRating();
                    } else {
                        rating = " - ";
                    }

                    if (anime.getSeason() != null && anime.getYear() != null){
                        season = anime.getSeason() + " " + anime.getYear();
                    } else {
                        if (anime.getSeason() != null && anime.getYear() == null ) {
                            season = anime.getSeason();
                        } else if (anime.getSeason() == null && anime.getYear() != null) {
                            season = String.valueOf(anime.getYear());
                        } else {
                            season = " - ";
                        }
                    }

                    if (anime.getAired().getFrom() != null) {
                        aired = anime.getAired().getDate();
                    } else {
                        aired = " - ";
                    }

                    licensor = formatLicensor(anime.getLicensors());

                    binding.tvScore.setText(score);
                    binding.tvRank.setText(rank);
                    binding.tvPopularity.setText(popularity);
                    binding.tvMembers.setText(members);
                    binding.tvTitle.setText(title);
                    binding.tvType.setText(type);
                    binding.tvStatus.setText(status);
                    binding.tvEpsDuration.setText(epsDuration);
                    binding.tvGenres.setText(genre);

                    binding.tvSynopsis.setText(synopsis);

                    binding.ibSynopsisDetail.setOnClickListener(v -> {
                        if (isSynopsisExpanded) {
                            binding.tvSynopsis.setText(synopsis);
                        } else {
                            binding.tvSynopsis.setText(synopsisFull);
                        }
                        isSynopsisExpanded = !isSynopsisExpanded;

                        if (isSynopsisExpanded) {
                            binding.ibSynopsisDetail.setImageResource(R.drawable.ic_up);
                        } else {
                            binding.ibSynopsisDetail.setImageResource(R.drawable.ic_down);
                        }
                    });



                    binding.tvEnglishTitle.setText(englishTitle);
                    binding.tvSource.setText(source);
                    binding.tvStudio.setText(studio);
                    binding.tvRating.setText(rating);
                    binding.tvSeason.setText(season);
                    binding.tvAired.setText(aired);
                    binding.tvLicensor.setText(licensor);
                } else {
                    binding.llFailedToLoad.setVisibility(View.VISIBLE);
                    isLoadingFailed = true;
                }
                isLoadingInProgress = false;
            }

            @Override
            public void onFailure(Call<AnimeDetailResponse> call, Throwable t) {
                isLoadingInProgress = false;

                if (!call.isCanceled()) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.llFailedToLoad.setVisibility(View.VISIBLE);
                    isLoadingFailed = true;
                }
            }
        });
    }

    private void loadCharacters() {
        if (!isInternetAvailable()) {
            return;
        }
        lastApiCallTime = System.currentTimeMillis();

        if (characterCall != null) {
            characterCall.cancel();
        }

        ApiService apiService = ApiConfig.getApiService();
        characterCall = apiService.getAnimeCharacters(animeId);

        characterCall.enqueue(new Callback<CharacterResponse>() {
            @Override
            public void onResponse(Call<CharacterResponse> call, Response<CharacterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<DataItem> dataItems = response.body().getData();

                    binding.rvCharacters.setVisibility(View.VISIBLE);

                    characterList.clear();
                    characterList.addAll(dataItems);
                    characterAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<CharacterResponse> call, Throwable t) {
                if (!call.isCanceled()) {
                    Toast.makeText(AnimeDetailActivity.this,
                            "Failed to load character data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    runOnUiThread(() -> {
                        binding.llFailedToLoad.setVisibility(View.GONE);

                        if (!isDataLoaded && !isLoadingInProgress) {
                            scheduleDataLoading();
                        } else if (isLoadingFailed) {
                            isLoadingFailed = false;
                        }
                    });
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    runOnUiThread(() -> {
                        if (!isDataLoaded) {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.llFailedToLoad.setVisibility(View.VISIBLE);
                        }
                        isLoadingFailed = true;
                    });
                }
            };

            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
        }
    }

    @Override
    protected void onDestroy() {
        if (animeDetailCall != null) {
            animeDetailCall.cancel();
            animeDetailCall = null;
        }

        if (characterCall != null) {
            characterCall.cancel();
            characterCall = null;
        }

        if (loadingRunnable != null) {
            handler.removeCallbacks(loadingRunnable);
        }

        if (networkCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onDestroy();
    }

    private boolean isInternetAvailable() {
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET));
            } else {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }

    private void scheduleDataLoading() {
        if (isLoadingInProgress || isDataLoaded) {
            return;
        }

        if (!isInternetAvailable()) {
            binding.progressBar.setVisibility(View.GONE);
            binding.llFailedToLoad.setVisibility(View.VISIBLE);
            return;
        }

        isLoadingInProgress = true;
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.llFailedToLoad.setVisibility(View.GONE);

        long currentTime = System.currentTimeMillis();
        long timeElapsedSinceLastCall = currentTime - lastApiCallTime;
        long delayNeeded = Math.max(300, MIN_API_CALL_INTERVAL - timeElapsedSinceLastCall);

        if (loadingRunnable != null) {
            handler.removeCallbacks(loadingRunnable);
        }

        loadingRunnable = () -> {
            loadAnimeDetail();
            loadCharacters();
        };

        handler.postDelayed(loadingRunnable, delayNeeded);
    }



    private String formatGenres(List<GenresItem> genres) {
        if (genres == null || genres.isEmpty()) return " - ";

        List<String> genreNames = new ArrayList<>();
        for (GenresItem genre : genres) {
            genreNames.add(genre.getName());
        }
        return TextUtils.join("       ", genreNames);
    }

    private String formatStudio(List<StudiosItem> studios) {
        if (studios == null || studios.isEmpty()) return "Unknown";

        List<String> studioNames = new ArrayList<>();
        for (StudiosItem studio : studios) {
            studioNames.add(studio.getName());
        }
        return TextUtils.join(", ", studioNames);
    }

    private String formatLicensor(List<LicensorsItem> licensors) {
        if (licensors == null || licensors.isEmpty()) return "Unknown";

        List<String> licensorNames = new ArrayList<>();
        for (LicensorsItem licensor : licensors) {
            licensorNames.add(licensor.getName());
        }
        return TextUtils.join(", ", licensorNames);
    }
}