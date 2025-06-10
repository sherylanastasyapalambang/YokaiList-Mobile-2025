package com.example.yokailist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yokailist.R;
import com.example.yokailist.adapters.AnimeAdapter;
import com.example.yokailist.adapters.GenreAdapter;
import com.example.yokailist.data.network.ApiConfig;
import com.example.yokailist.data.network.ApiService;
import com.example.yokailist.data.response.AnimeResponse;
import com.example.yokailist.data.response.animedetail.Anime;
import com.example.yokailist.data.response.genre.Genre;
import com.example.yokailist.data.response.genre.GenreResponse;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscoverFragment extends Fragment {
    private static long lastApiCallTime = 0;
    private static final long MIN_API_CALL_INTERVAL = 200;
    private RecyclerView rvAnime, rvGenre;
    private AnimeAdapter animeAdapter;
    private GenreAdapter genreAdapter;
    private List<Anime> animeList = new ArrayList<>();
    private List<Genre> genreList = new ArrayList<>();
    private ProgressBar progressBar;
    private LinearLayout ll_failedToLoad, ll_noResults;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private Call<AnimeResponse> animeCall;
    private Call<GenreResponse> genreCall;
    private boolean isLoadingInProgress = false;
    private boolean isDataLoaded = false;
    private boolean isLoadingFailed = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable loadingRunnable;
    private Genre selectedGenre = null;
    private String selectedGenres = null;
    private String searchedTitle = null;
    private int currentPage = 1;
    private int pendingPage = -1;
    private int totalPages = Integer.MAX_VALUE;
    private boolean hasNextPage = true;
    private Button btn_loadMore, btn_clearHistory;
    private EditText et_search;
    private ImageButton ib_empty;
    private ChipGroup cg_history;
    private static final String PREF_SEARCH_HISTORY = "search_history";
    private static final int MAX_HISTORY_ITEMS = 5;
    private SharedPreferences sharedPreferences;
    private Set<String> searchHistory;


    public DiscoverFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        rvAnime = view.findViewById(R.id.rv_anime);
        rvGenre = view.findViewById(R.id.rv_genre);
        progressBar = view.findViewById(R.id.progressBar);
        ll_failedToLoad = view.findViewById(R.id.ll_failedToLoad);
        ll_noResults = view.findViewById(R.id.ll_noResult);
        btn_loadMore = view.findViewById(R.id.btn_loadMore);
        et_search = view.findViewById(R.id.et_search);
        ib_empty = view.findViewById(R.id.ib_empty);
        cg_history = view.findViewById(R.id.cg_history);
        btn_clearHistory = view.findViewById(R.id.btn_clearHistory);

        sharedPreferences = requireContext().getSharedPreferences("YokaiListPrefs", Context.MODE_PRIVATE);
        searchHistory = new HashSet<>(sharedPreferences.getStringSet(PREF_SEARCH_HISTORY, new HashSet<>()));

        btn_clearHistory.setOnClickListener(v -> {
            searchHistory.clear();

            sharedPreferences.edit()
                    .putStringSet(PREF_SEARCH_HISTORY, searchHistory)
                    .apply();

            loadSearchHistoryChips();

        });

        rvAnime.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        animeAdapter = new AnimeAdapter(getContext(), animeList);
        rvAnime.setAdapter(animeAdapter);

        genreAdapter = new GenreAdapter(getContext(), genreList, genreIds -> {
            selectedGenres = genreIds;


            if (genreIds != null) {
                searchedTitle = null;
                et_search.setText("");

                currentPage = 1;
                scheduleDataLoading();
            }
        });
        rvGenre.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvGenre.setAdapter(genreAdapter);

        loadSearchHistoryChips();

        connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        setupNetworkCallback();

        et_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchedTitle = et_search.getText().toString().trim();
                if (!searchedTitle.isEmpty()) {
                    selectedGenres = null;
                    genreAdapter.clearSelections();

                    rvGenre.setVisibility(View.GONE);

                    addToSearchHistory(searchedTitle);

                    currentPage = 1;
                    scheduleDataLoading();
                }
                return true;
            }
            return false;
        });

        ib_empty.setOnClickListener(v -> {
            searchedTitle = null;
            selectedGenres = null;
            et_search.setText("");

            genreAdapter.clearSelections();

            rvGenre.setVisibility(View.VISIBLE);

            currentPage = 1;
            scheduleDataLoading();
        });

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

    private void loadSearchHistoryChips() {
        cg_history.removeAllViews();

        List<String> sortedHistory = new ArrayList<>(searchHistory);

        for (String query : sortedHistory) {
            Chip chip = new Chip(requireContext());
            chip.setText(query);
            chip.setClickable(true);
            chip.setCheckable(false);
            chip.setCloseIconVisible(true);

            chip.setOnClickListener(v -> {
                et_search.setText(query);
                searchedTitle = query;

                selectedGenres = null;
                genreAdapter.clearSelections();

                rvGenre.setVisibility(View.GONE);

                currentPage = 1;
                scheduleDataLoading();
            });

            chip.setOnCloseIconClickListener(v -> {
                searchHistory.remove(query);

                sharedPreferences.edit()
                        .putStringSet(PREF_SEARCH_HISTORY, searchHistory)
                        .apply();
                cg_history.removeView(chip);
            });

            cg_history.addView(chip);
        }
    }

    private void addToSearchHistory(String query) {
        searchHistory.add(query);

        if (searchHistory.size() > MAX_HISTORY_ITEMS) {
            Iterator<String> iterator = searchHistory.iterator();
            iterator.next();
            iterator.remove();
        }

        sharedPreferences.edit()
                .putStringSet(PREF_SEARCH_HISTORY, searchHistory)
                .apply();

        loadSearchHistoryChips();
    }

    private void scheduleDataLoading() {
        if (isLoadingInProgress) {
            return;
        }

        if (!isInternetAvailable()) {
            showNoInternetMessage();
            return;
        }

        isLoadingInProgress = true;
        progressBar.setVisibility(View.VISIBLE);
        btn_loadMore.setVisibility(View.GONE);
        btn_loadMore.setEnabled(false);
        et_search.setEnabled(false);
        ib_empty.setEnabled(false);

        long currentTime = System.currentTimeMillis();
        long timeElapsedSinceLastCall = currentTime - lastApiCallTime;
        long delayNeeded = Math.max(300, MIN_API_CALL_INTERVAL - timeElapsedSinceLastCall);

        if (loadingRunnable != null) {
            handler.removeCallbacks(loadingRunnable);
        }

        loadingRunnable = () -> {
            if (isAdded()) {
                loadGenre();
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

    private void loadGenre() {
        if (!isInternetAvailable() || !isAdded()) {
            isLoadingInProgress = false;
            showNoInternetMessage();
            return;
        }

        lastApiCallTime = System.currentTimeMillis();

        if (genreCall != null) {
            genreCall.cancel();
        }

        progressBar.setVisibility(View.VISIBLE);
        btn_loadMore.setVisibility(View.GONE);
        btn_loadMore.setEnabled(false);
        et_search.setEnabled(false);
        ib_empty.setEnabled(false);
        ll_failedToLoad.setVisibility(View.GONE);
        rvGenre.setVisibility(View.GONE);
        isLoadingFailed = false;

        ApiService apiService = ApiConfig.getApiService();
        genreCall = apiService.getGenres();
        genreCall.enqueue(new Callback<GenreResponse>() {
            @Override
            public void onResponse(Call<GenreResponse> call, Response<GenreResponse> response) {
                if (!isAdded()) {
                    isLoadingInProgress = false;
                    return;
                }

                isLoadingInProgress = false;
                progressBar.setVisibility(View.GONE);
                btn_loadMore.setEnabled(true);
                btn_loadMore.setVisibility(View.VISIBLE);
                et_search.setEnabled(true);
                ib_empty.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    List<Genre> originalList = response.body().getData();

                    hideNoInternetMessage();
                    ll_failedToLoad.setVisibility(View.GONE);
                    rvGenre.setVisibility(View.VISIBLE);

                    isLoadingFailed = false;
                    isDataLoaded = true;

                    Set<Integer> uniqueIds = new HashSet<>();
                    List<Genre> uniqueGenreList = new ArrayList<>();

                    for (Genre genre : originalList) {
                        if (!uniqueIds.contains(genre.getId())) {
                            uniqueIds.add(genre.getId());
                            uniqueGenreList.add(genre);
                        }
                    }

                    genreList.clear();
                    genreList.addAll(uniqueGenreList);
                    genreAdapter.notifyDataSetChanged();

                    syncGenreSelections();

                } else {
                    isLoadingFailed = true;
                    showNoInternetMessage();
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GenreResponse> call, Throwable t) {
                if (!isAdded()) {
                    isLoadingInProgress = false;
                    return;
                }

                isLoadingInProgress = false;
                progressBar.setVisibility(View.GONE);
                btn_loadMore.setEnabled(true);
                btn_loadMore.setVisibility(View.VISIBLE);
                et_search.setEnabled(true);
                ib_empty.setEnabled(true);

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

        if (animeCall != null) {
            animeCall.cancel();
        }

        progressBar.setVisibility(View.VISIBLE);
        btn_loadMore.setVisibility(View.GONE);
        btn_loadMore.setEnabled(false);
        et_search.setEnabled(false);
        ib_empty.setEnabled(false);
        btn_loadMore.setEnabled(false);
        ll_failedToLoad.setVisibility(View.GONE);
        isLoadingFailed = false;

        if (page == 1) {
            rvAnime.setVisibility(View.GONE);
        }

        ApiService apiService = ApiConfig.getApiService();
        animeCall = apiService.getAnimeSearch(searchedTitle, selectedGenres, page);
        animeCall.enqueue(new Callback<AnimeResponse>() {
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
                et_search.setEnabled(true);
                ib_empty.setEnabled(true);
                btn_loadMore.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    AnimeResponse animeResponse = response.body();
                    List<Anime> originalList = animeResponse.getData();

                    hideNoInternetMessage();
                    ll_failedToLoad.setVisibility(View.GONE);
                    ll_noResults.setVisibility(View.GONE);
                    rvAnime.setVisibility(View.VISIBLE);
                    isLoadingFailed = false;

                    if (animeResponse.getPagination() != null) {
                        hasNextPage = animeResponse.getPagination().isHasNextPage();
                        totalPages = animeResponse.getPagination().getLastVisiblePage();
                    }

                    Set<Integer> uniqueIds = new HashSet<>();
                    List<Anime> uniqueAnimeList = new ArrayList<>();

                    for (Anime anime : originalList) {
                        if (!uniqueIds.contains(anime.getId()) && anime.isApproved()) {
                            uniqueIds.add(anime.getId());
                            uniqueAnimeList.add(anime);
                        }
                    }

                    if (page == 1) {
                        animeList.clear();
                    }

                    animeList.addAll(uniqueAnimeList);
                    animeAdapter.notifyDataSetChanged();
                    rvAnime.setVisibility(View.VISIBLE);
                    ll_noResults.setVisibility(View.GONE);


                    if (animeList.isEmpty() && page == 1) {
                        ll_noResults.setVisibility(View.VISIBLE);
                    }

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
                et_search.setEnabled(true);
                ib_empty.setEnabled(true);
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

    private void syncGenreSelections() {
        if (selectedGenres != null && !selectedGenres.isEmpty()) {
            String[] genreIds = selectedGenres.split(",");
            genreAdapter.clearSelections();

            for (String idStr : genreIds) {
                try {
                    int id = Integer.parseInt(idStr);
                    for (Genre genre : genreList) {
                        if (genre.getId() == id) {
                            genreAdapter.selectGenre(genre);
                            break;
                        }
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        searchHistory = new HashSet<>(sharedPreferences.getStringSet(PREF_SEARCH_HISTORY, new HashSet<>()));
        loadSearchHistoryChips();

        if (isDataLoaded && !animeList.isEmpty() && !genreList.isEmpty()) {
            hideNoInternetMessage();
            rvAnime.setVisibility(View.VISIBLE);
            rvGenre.setVisibility(View.VISIBLE);
            ll_noResults.setVisibility(View.GONE);
        }
        else if (!isLoadingInProgress && !isDataLoaded) {
            scheduleDataLoading();
        }

        if (isInternetAvailable() && isLoadingFailed) {
            isLoadingFailed = false;
            if (animeList.isEmpty() && genreList.isEmpty()) {
                scheduleDataLoading();
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (animeCall != null) {
            animeCall.cancel();
            animeCall = null;
        }
        if (genreCall != null) {
            genreCall.cancel();
            genreCall = null;
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
                        } else if (!isDataLoaded && animeList.isEmpty() && genreList.isEmpty() && !isLoadingInProgress) {
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
                        if (animeList.isEmpty() && genreList.isEmpty()) {
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
        btn_loadMore.setVisibility(View.GONE);
        rvAnime.setVisibility(View.GONE);
        rvGenre.setVisibility(View.GONE);
        ll_failedToLoad.setVisibility(View.VISIBLE);
        isLoadingFailed = true;
    }

    private void hideNoInternetMessage() {
        ll_failedToLoad.setVisibility(View.GONE);
    }

}