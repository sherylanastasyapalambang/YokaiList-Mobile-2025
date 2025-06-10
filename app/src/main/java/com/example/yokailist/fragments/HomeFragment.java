package com.example.yokailist.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yokailist.R;
import com.example.yokailist.adapters.TopAiringAnimeAdapter;
import com.example.yokailist.adapters.TopScoreAnimeAdapter;
import com.example.yokailist.data.network.ApiConfig;
import com.example.yokailist.data.network.ApiService;
import com.example.yokailist.data.response.AnimeResponse;
import com.example.yokailist.data.response.animedetail.Anime;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static long lastApiCallTime = 0;
    private static final long MIN_API_CALL_INTERVAL = 200;
    private TextView tvTitle1, tvTitle2;
    private RecyclerView rvTopAiringAnime, rvTopScoreAnime;
    private TopAiringAnimeAdapter topAiringAnimeAdapter;
    private TopScoreAnimeAdapter topScoreAnimeAdapter;
    private List<Anime> topAiringAnimeList = new ArrayList<>();
    private List<Anime> topScoreAnimeList = new ArrayList<>();
    private ProgressBar progressBar;
    private LinearLayout ll_failedToLoad;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private Call<AnimeResponse> airingAnimeCall;
    private Call<AnimeResponse> topAnimeCall;
    private boolean isLoadingInProgress = false;
    private boolean isDataLoaded = false;
    private boolean isLoadingFailed = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable loadingRunnable;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        ll_failedToLoad = view.findViewById(R.id.ll_failedToLoad);
        tvTitle1 = view.findViewById(R.id.tv_title1);
        tvTitle2 = view.findViewById(R.id.tv_title2);
        rvTopAiringAnime = view.findViewById(R.id.rv_topAiring);
        rvTopScoreAnime = view.findViewById(R.id.rv_topScore);

        rvTopAiringAnime.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvTopScoreAnime.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        topAiringAnimeAdapter = new TopAiringAnimeAdapter(getContext(), topAiringAnimeList);
        rvTopAiringAnime.setAdapter(topAiringAnimeAdapter);

        topScoreAnimeAdapter = new TopScoreAnimeAdapter(getContext(), topScoreAnimeList);
        rvTopScoreAnime.setAdapter(topScoreAnimeAdapter);

        connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        setupNetworkCallback();

        progressBar.setVisibility(View.VISIBLE);
        ll_failedToLoad.setVisibility(View.GONE);
        rvTopAiringAnime.setVisibility(View.GONE);
        rvTopScoreAnime.setVisibility(View.GONE);
        tvTitle1.setVisibility(View.GONE);
        tvTitle2.setVisibility(View.GONE);

        scheduleDataLoading();

        return view;
    }

    private void scheduleDataLoading() {
        if (isLoadingInProgress || isDataLoaded) {
            return;
        }

        if (!isInternetAvailable()) {
            showNoInternetMessage();
            return;
        }

        isLoadingInProgress = true;
        progressBar.setVisibility(View.VISIBLE);

        long currentTime = System.currentTimeMillis();
        long timeElapsedSinceLastCall = currentTime - lastApiCallTime;
        long delayNeeded = Math.max(300, MIN_API_CALL_INTERVAL - timeElapsedSinceLastCall);

        if (loadingRunnable != null) {
            handler.removeCallbacks(loadingRunnable);
        }

        loadingRunnable = () -> {
            if (isAdded()) {
                loadTopAiringAnime();
                loadTopScoreAnime();
            } else {
                isLoadingInProgress = false;
            }
        };

        handler.postDelayed(loadingRunnable, delayNeeded);
    }

    private void loadTopAiringAnime() {
        if (!isInternetAvailable() || !isAdded()) {
            isLoadingInProgress = false;
            showNoInternetMessage();
            return;
        }

        lastApiCallTime = System.currentTimeMillis();


        progressBar.setVisibility(View.VISIBLE);
        ll_failedToLoad.setVisibility(View.GONE);
        tvTitle1.setVisibility(View.GONE);
        tvTitle2.setVisibility(View.GONE);
        rvTopAiringAnime.setVisibility(View.GONE);
        rvTopScoreAnime.setVisibility(View.GONE);
        isLoadingFailed = false;

        if (airingAnimeCall != null) {
            airingAnimeCall.cancel();
        }

        ApiService apiService = ApiConfig.getApiService();
        airingAnimeCall = apiService.getAiringAnime("TV", "airing", "score", "desc", 15);
        airingAnimeCall.enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(Call<AnimeResponse> call, Response<AnimeResponse> response) {
                if (!isAdded()) {
                    isLoadingInProgress = false;
                    return;
                }

                isLoadingInProgress = false;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Anime> originalList = response.body().getData();

                    hideNoInternetMessage();
                    ll_failedToLoad.setVisibility(View.GONE);
                    rvTopAiringAnime.setVisibility(View.VISIBLE);
                    rvTopScoreAnime.setVisibility(View.VISIBLE);
                    tvTitle1.setVisibility(View.VISIBLE);
                    tvTitle2.setVisibility(View.VISIBLE);

                    isLoadingFailed = false;
                    isDataLoaded = true;

                    Set<Integer> uniqueIds = new HashSet<>();
                    List<Anime> uniqueAnimeList = new ArrayList<>();

                    int count = 0;
                    for (Anime anime : originalList) {
                        if (!uniqueIds.contains(anime.getId())) {
                            uniqueIds.add(anime.getId());
                            uniqueAnimeList.add(anime);
                            count++;
                            if (count == 10) {
                                break;
                            }
                        }
                    }

                    topAiringAnimeList.clear();
                    topAiringAnimeList.addAll(uniqueAnimeList);
                    topAiringAnimeAdapter.notifyDataSetChanged();

                } else {
                    isLoadingFailed = true;
                    showNoInternetMessage();
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AnimeResponse> call, Throwable t) {
                if (!isAdded()) {
                    isLoadingInProgress = false;
                    return;
                }

                isLoadingInProgress = false;
                progressBar.setVisibility(View.GONE);

                if (!call.isCanceled()) {
                    isLoadingFailed = true;
                    if (!isInternetAvailable()) {
                        showNoInternetMessage();
                    } else {
                        showNoInternetMessage();
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    private void loadTopScoreAnime() {
        if (!isInternetAvailable() || !isAdded()) {
            isLoadingInProgress = false;
            showNoInternetMessage();
            return;
        }

        lastApiCallTime = System.currentTimeMillis();

        if (topAnimeCall != null) {
            topAnimeCall.cancel();
        }

        progressBar.setVisibility(View.VISIBLE);
        ll_failedToLoad.setVisibility(View.GONE);
        tvTitle1.setVisibility(View.GONE);
        tvTitle2.setVisibility(View.GONE);
        rvTopAiringAnime.setVisibility(View.GONE);
        rvTopScoreAnime.setVisibility(View.GONE);
        isLoadingFailed = false;

        ApiService apiService = ApiConfig.getApiService();
        topAnimeCall = apiService.getTopAnime();
        topAnimeCall.enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(Call<AnimeResponse> call, Response<AnimeResponse> response) {
                if (!isAdded()) {
                    isLoadingInProgress = false;
                    return;
                }

                isLoadingInProgress = false;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    List<Anime> originalList = response.body().getData();

                    hideNoInternetMessage();
                    ll_failedToLoad.setVisibility(View.GONE);
                    rvTopAiringAnime.setVisibility(View.VISIBLE);
                    rvTopScoreAnime.setVisibility(View.VISIBLE);
                    tvTitle1.setVisibility(View.VISIBLE);
                    tvTitle2.setVisibility(View.VISIBLE);

                    isLoadingFailed = false;
                    isDataLoaded = true;

                    Set<Integer> uniqueIds = new HashSet<>();
                    List<Anime> uniqueAnimeList = new ArrayList<>();

                    int count = 0;
                    for (Anime anime : originalList) {
                        if (!uniqueIds.contains(anime.getId())) {
                            uniqueIds.add(anime.getId());
                            uniqueAnimeList.add(anime);
                            count++;
                            if (count == 25) {
                                break;
                            }
                        }
                    }

                    topScoreAnimeList.clear();
                    topScoreAnimeList.addAll(uniqueAnimeList);
                    topScoreAnimeAdapter.notifyDataSetChanged();
                } else {
                    isLoadingFailed = true;
                    showNoInternetMessage();
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<AnimeResponse> call, Throwable t) {
                if (!isAdded()) {
                    isLoadingInProgress = false;
                    return;
                }

                isLoadingInProgress = false;
                progressBar.setVisibility(View.GONE);

                if (!call.isCanceled()) {
                    isLoadingFailed = true;
                    if (!isInternetAvailable()) {
                        showNoInternetMessage();
                    } else {
                        showNoInternetMessage();
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isDataLoaded && !topAiringAnimeList.isEmpty() && !topScoreAnimeList.isEmpty()) {
            hideNoInternetMessage();
            rvTopAiringAnime.setVisibility(View.VISIBLE);
            rvTopScoreAnime.setVisibility(View.VISIBLE);
            tvTitle1.setVisibility(View.VISIBLE);
            tvTitle2.setVisibility(View.VISIBLE);
        }
        else if (!isLoadingInProgress && !isDataLoaded) {
            scheduleDataLoading();
        }

        if (isInternetAvailable() && isLoadingFailed) {
            isLoadingFailed = false;
            if (topAiringAnimeList.isEmpty() && topScoreAnimeList.isEmpty()) {
                scheduleDataLoading();
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (airingAnimeCall != null) {
            airingAnimeCall.cancel();
            airingAnimeCall = null;
        }
        if (topAnimeCall != null) {
            topAnimeCall.cancel();
            topAnimeCall = null;
        }

        if (loadingRunnable != null) {
            handler.removeCallbacks(loadingRunnable);
        }

        isLoadingInProgress = false;

        super.onDestroyView();
        if (networkCallback != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    requireActivity().runOnUiThread(() -> {
                        hideNoInternetMessage();

                        if (!isDataLoaded && topAiringAnimeList.isEmpty() && topScoreAnimeList.isEmpty() && !isLoadingInProgress) {
                            scheduleDataLoading();
                        } else if (isLoadingFailed) {
                            isLoadingFailed = false;
                        }
                    });
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    requireActivity().runOnUiThread(() -> {
                        if (topAiringAnimeList.isEmpty() && topScoreAnimeList.isEmpty()) {
                            showNoInternetMessage();
                        } else {
                            isLoadingFailed = true;
                        }
                    });
                }
            };

            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connectivityManager.registerNetworkCallback(builder.build(), networkCallback);
        }
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

    private void showNoInternetMessage() {
        progressBar.setVisibility(View.GONE);
        rvTopAiringAnime.setVisibility(View.GONE);
        rvTopScoreAnime.setVisibility(View.GONE);
        ll_failedToLoad.setVisibility(View.VISIBLE);
        tvTitle1.setVisibility(View.GONE);
        tvTitle2.setVisibility(View.GONE);
        isLoadingFailed = true;
    }

    private void hideNoInternetMessage() {
        ll_failedToLoad.setVisibility(View.GONE);
    }
}