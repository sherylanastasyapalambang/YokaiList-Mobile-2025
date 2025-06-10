package com.example.yokailist.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yokailist.R;
import com.example.yokailist.adapters.SeasonalAnimeAdapter;
import com.example.yokailist.adapters.TopAiringAnimeAdapter;
import com.example.yokailist.data.db.AnimeStatusHelper;
import com.example.yokailist.data.db.MappingHelper;
import com.example.yokailist.data.models.AnimeStatus;
import com.example.yokailist.data.network.ApiConfig;
import com.example.yokailist.data.network.ApiService;
import com.example.yokailist.data.response.AnimeResponse;
import com.example.yokailist.data.response.animedetail.Anime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ThisSeasonFragment extends Fragment {
    private static long lastApiCallTime = 0;
    private static final long MIN_API_CALL_INTERVAL = 200;
    private List<Anime> animeList = new ArrayList<>();
    private SeasonalAnimeAdapter adapter;
    private int currentPage = 1;
    private int pendingPage = -1;
    private boolean isLoadingFailed = false;
    private int totalPages = Integer.MAX_VALUE;
    private boolean isDataLoaded = false;
    private boolean hasNextPage = true;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private Call<AnimeResponse> activeCall;
    private boolean isLoadingInProgress = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable loadingRunnable;

    private RecyclerView rvAnime;
    private ProgressBar progressBar;
    private LinearLayout ll_failedToLoad;
    private Button btn_loadMore;

    public ThisSeasonFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_this_season, container, false);

        rvAnime = view.findViewById(R.id.rv_anime);
        progressBar = view.findViewById(R.id.progressBar);
        ll_failedToLoad = view.findViewById(R.id.ll_failedToLoad);
        btn_loadMore = view.findViewById(R.id.btn_loadMore);

        AnimeStatusHelper helper = AnimeStatusHelper.getInstance(getContext());
        helper.open();

        List<AnimeStatus> animeStatusList = MappingHelper.mapCursorToArrayList(helper.queryAll());
        Map<Integer, AnimeStatus> animeStatusMap = new HashMap<>();
        for (AnimeStatus status : animeStatusList) {
            animeStatusMap.put(status.getAnimeId(), status);
        }

        helper.close();

        rvAnime.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new SeasonalAnimeAdapter(getContext(), animeList, animeStatusMap);
        rvAnime.setAdapter(adapter);

        connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        setupNetworkCallback();

        progressBar.setVisibility(View.VISIBLE);
        btn_loadMore.setVisibility(View.GONE);
        ll_failedToLoad.setVisibility(View.GONE);
        rvAnime.setVisibility(View.GONE);

        scheduleDataLoading();

        btn_loadMore.setOnClickListener(v -> {
            if (hasNextPage && currentPage < totalPages && !isLoadingInProgress) {
                int nextPage = currentPage + 1;
                if (!isInternetAvailable()) {
                    pendingPage = nextPage;
                    btn_loadMore.setVisibility(View.GONE);
                    showNoInternetMessage();
                    return;
                }

                scheduleLoadMore(nextPage);
            }
        });

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
        btn_loadMore.setVisibility(View.GONE);

        long currentTime = System.currentTimeMillis();
        long timeElapsedSinceLastCall = currentTime - lastApiCallTime;
        long delayNeeded = Math.max(300, MIN_API_CALL_INTERVAL - timeElapsedSinceLastCall);

        if (loadingRunnable != null) {
            handler.removeCallbacks(loadingRunnable);
        }

        loadingRunnable = () -> {
            if (isAdded()) {
                loadAnime(currentPage);
            } else {
                isLoadingInProgress = false;
            }
        };

        handler.postDelayed(loadingRunnable, delayNeeded);
    }

    private void scheduleLoadMore(int nextPage) {
        if (isLoadingInProgress) {
            return;
        }

        isLoadingInProgress = true;
        btn_loadMore.setEnabled(false);

        long currentTime = System.currentTimeMillis();
        long timeElapsedSinceLastCall = currentTime - lastApiCallTime;
        long delayNeeded = Math.max(1000, MIN_API_CALL_INTERVAL - timeElapsedSinceLastCall);

        if (loadingRunnable != null) {
            handler.removeCallbacks(loadingRunnable);
        }

        loadingRunnable = () -> {
            if (isAdded()) {
                loadAnime(nextPage);
            } else {
                isLoadingInProgress = false;
                btn_loadMore.setEnabled(true);
            }
        };

        handler.postDelayed(loadingRunnable, delayNeeded);
    }

    private void loadAnime(int page) {
        if (!isInternetAvailable() || !isAdded()) {
            isLoadingInProgress = false;
            if (page > 1) {
                pendingPage = page;
                btn_loadMore.setVisibility(View.GONE);
            }
            showNoInternetMessage();
            return;
        }

        lastApiCallTime = System.currentTimeMillis();

        if (activeCall != null) {
            activeCall.cancel();
        }

        progressBar.setVisibility(View.VISIBLE);
        btn_loadMore.setVisibility(View.GONE);
        btn_loadMore.setEnabled(false);
        ll_failedToLoad.setVisibility(View.GONE);
        isLoadingFailed = false;

        if (page == 1) {
            rvAnime.setVisibility(View.GONE);
        }

        ApiService apiService = ApiConfig.getApiService();
        activeCall = apiService.getNowSeasonAnime(page);
        activeCall.enqueue(new Callback<AnimeResponse>() {
            @Override
            public void onResponse(Call<AnimeResponse> call, Response<AnimeResponse> response) {
                if (!isAdded()) {
                    isLoadingInProgress = false;
                    return;
                }

                isLoadingInProgress = false;
                progressBar.setVisibility(View.GONE);
                btn_loadMore.setVisibility(View.VISIBLE);
                btn_loadMore.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    AnimeResponse animeResponse = response.body();
                    List<Anime> originalList = animeResponse.getData();

                    hideNoInternetMessage();
                    ll_failedToLoad.setVisibility(View.GONE);
                    rvAnime.setVisibility(View.VISIBLE);
                    isLoadingFailed = false;

                    if (animeResponse.getPagination() != null) {
                        hasNextPage = animeResponse.getPagination().isHasNextPage();
                        totalPages = animeResponse.getPagination().getLastVisiblePage();
                    }

                    Set<Integer> uniqueIds = new HashSet<>();
                    List<Anime> filteredAnimeList = new ArrayList<>();

                    for (Anime anime : originalList) {
                        if (!uniqueIds.contains(anime.getId()) && anime.isAiring()) {
                            uniqueIds.add(anime.getId());
                            filteredAnimeList.add(anime);
                        }
                    }

                    if (page == 1) {
                        animeList.clear();
                    }

                    animeList.addAll(filteredAnimeList);
                    adapter.notifyDataSetChanged();
                    rvAnime.setVisibility(View.VISIBLE);

                    currentPage = page;
                    isDataLoaded = true;
                    pendingPage = -1;

                    if (!hasNextPage || currentPage >= totalPages) {
                        btn_loadMore.setVisibility(View.GONE);
                    } else {
                        btn_loadMore.setVisibility(View.VISIBLE);
                    }

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
                btn_loadMore.setVisibility(View.VISIBLE);
                btn_loadMore.setEnabled(true);

                if (!call.isCanceled()) {
                    if (!isInternetAvailable()) {
                        if (page > 1) {
                            pendingPage = page;
                            btn_loadMore.setVisibility(View.GONE);
                        }
                        showNoInternetMessage();
                    } else {
                        if (hasNextPage && currentPage < totalPages) {
                            btn_loadMore.setVisibility(View.VISIBLE);
                        }
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                        }
                    }
                    isLoadingFailed = true;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isDataLoaded && !animeList.isEmpty()) {
            hideNoInternetMessage();
            rvAnime.setVisibility(View.VISIBLE);
        }
        else if (!isLoadingInProgress && !isDataLoaded) {
            scheduleDataLoading();
        }

        if (isInternetAvailable() && isLoadingFailed) {
            isLoadingFailed = false;
            if (animeList.isEmpty()) {
                scheduleDataLoading();
            } else if (hasNextPage && currentPage < totalPages) {
                btn_loadMore.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (activeCall != null) {
            activeCall.cancel();
            activeCall = null;
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

                        if (pendingPage != -1) {
                            scheduleLoadMore(pendingPage);
                        } else if (!isDataLoaded && animeList.isEmpty() && !isLoadingInProgress) {
                            scheduleDataLoading();
                        } else if (isLoadingFailed) {
                            isLoadingFailed = false;
                            if (hasNextPage && currentPage < totalPages) {
                                btn_loadMore.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    requireActivity().runOnUiThread(() -> {
                        if (animeList.isEmpty()) {
                            showNoInternetMessage();
                        } else {
                            isLoadingFailed = true;
                            btn_loadMore.setVisibility(View.GONE);
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
        rvAnime.setVisibility(View.GONE);
        ll_failedToLoad.setVisibility(View.VISIBLE);
        btn_loadMore.setVisibility(View.GONE);
        isLoadingFailed = true;
    }

    private void hideNoInternetMessage() {
        ll_failedToLoad.setVisibility(View.GONE);
    }
}