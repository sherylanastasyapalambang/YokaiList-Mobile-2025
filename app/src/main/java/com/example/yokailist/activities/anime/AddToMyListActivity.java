package com.example.yokailist.activities.anime;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.yokailist.R;
import com.example.yokailist.adapters.CharacterAdapter;
import com.example.yokailist.data.db.AnimeStatusHelper;
import com.example.yokailist.data.db.DatabaseContract;
import com.example.yokailist.data.db.MappingHelper;
import com.example.yokailist.data.models.AnimeStatus;
import com.example.yokailist.data.network.ApiConfig;
import com.example.yokailist.data.network.ApiService;
import com.example.yokailist.data.response.AnimeDetailResponse;
import com.example.yokailist.data.response.CharacterResponse;
import com.example.yokailist.data.response.animedetail.Anime;
import com.example.yokailist.data.response.character.DataItem;
import com.example.yokailist.databinding.ActivityAddToMyListBinding;
import com.example.yokailist.databinding.ActivityAnimeDetailBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddToMyListActivity extends AppCompatActivity {
    ActivityAddToMyListBinding binding;
    private int animeId;
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
    private AnimeStatusHelper animeStatusHelper;
    private AnimeStatus animeStatus;
    private int animeStatusId;
    private boolean isEdit = false;
    private boolean isStartDate = false;
    private int status = 0;
    private String startDate;
    private String endDate;
    private String title, imageUrl, type, season;
    private int episodes;
    private boolean isNotAired = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAddToMyListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        animeId = getIntent().getIntExtra("ANIME_ID", -1);

        animeStatusHelper = AnimeStatusHelper.getInstance(this);
        animeStatusHelper.open();


        animeStatusId = animeStatusHelper.getAnimeStatusIdByAnimeId(animeId);
        isEdit = (animeStatusId != -1);

        binding.etProgress.setText("0");
        binding.etScore.setText("0");

        startDate = "";
        endDate = "";

        binding.btnWatching.setBackgroundResource(R.drawable.shape_status_no);
        binding.btnCompleted.setBackgroundResource(R.drawable.shape_status_no);
        binding.btnOnHold.setBackgroundResource(R.drawable.shape_status_no);
        binding.btnDropped.setBackgroundResource(R.drawable.shape_status_no);
        binding.btnPlanToWatch.setBackgroundResource(R.drawable.shape_status_no);

        if (isEdit) {
            Cursor cursor = animeStatusHelper.queryById(String.valueOf(animeStatusId));
            animeStatus = MappingHelper.mapCursorToObject(cursor);

            status = animeStatus.getStatus();
            if (animeStatus.getStartDate() != null) {
                startDate = animeStatus.getStartDate();
                binding.etStartDate.setText(startDate);
            }
            if (animeStatus.getEndDate() != null) {
                endDate = animeStatus.getEndDate();
                binding.etEndDate.setText(endDate);
            }

            if (status == 1) {
                binding.btnWatching.setBackgroundResource(R.drawable.shape_status_green);
            } else if (status == 2) {
                binding.btnCompleted.setBackgroundResource(R.drawable.shape_status_blue);
            } else if (status == 3) {
                binding.btnOnHold.setBackgroundResource(R.drawable.shape_status_yellow);
            } else if (status == 4) {
                binding.btnDropped.setBackgroundResource(R.drawable.shape_status_red);
            } else if (status == 5){
                binding.btnPlanToWatch.setBackgroundResource(R.drawable.shape_status_grey);
            } else {
                binding.btnWatching.setBackgroundResource(R.drawable.shape_status_no);
                binding.btnCompleted.setBackgroundResource(R.drawable.shape_status_no);
                binding.btnOnHold.setBackgroundResource(R.drawable.shape_status_no);
                binding.btnDropped.setBackgroundResource(R.drawable.shape_status_no);
                binding.btnPlanToWatch.setBackgroundResource(R.drawable.shape_status_no);
            }

            binding.etProgress.setText(String.valueOf(animeStatus.getProgress()));
            binding.etScore.setText(String.valueOf(animeStatus.getScore()));
            binding.etEndDate.setEnabled(false);
            binding.etEndDate.setClickable(false);
            binding.btnEndToday.setEnabled(false);


            if (!animeStatus.getStartDate().isEmpty() || animeStatus.getStartDate() != null) {
                binding.etStartDate.setText(animeStatus.getStartDate());
            }

            if (!animeStatus.getEndDate().isEmpty() || animeStatus.getEndDate() != null) {
                binding.etEndDate.setText(animeStatus.getEndDate());
            }

            binding.btnRemove.setEnabled(true);

        } else {
            animeStatus = new AnimeStatus();
            animeStatus.setAnimeId(animeId);

            binding.btnRemove.setEnabled(false);
            binding.btnWatching.setBackgroundResource(R.drawable.shape_status_no);
            binding.btnCompleted.setBackgroundResource(R.drawable.shape_status_no);
            binding.btnOnHold.setBackgroundResource(R.drawable.shape_status_no);
            binding.btnDropped.setBackgroundResource(R.drawable.shape_status_no);
            binding.btnPlanToWatch.setBackgroundResource(R.drawable.shape_status_no);

        }

        binding.btnRemove.setOnClickListener(v -> {
            deleteAnimeStatus();
        });

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

    private void loadAnime() {
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

                    binding.tvTitle.setText(anime.getTitle() != null ? anime.getTitle() : "-");

                    binding.tvStatus.setText(anime.getStatus());

                    String stringEps = String.valueOf(anime.getEpisodes());
                    if (stringEps.equals("null") && anime.isAiring()) {
                        episodes = -1;
                    } else {
                        episodes = anime.getEpisodes();
                    }

                    if (anime.getStatus().equals("Not yet aired") && animeStatus.getStatus() == 0) {
                        isNotAired = true;
                        binding.btnPlanToWatch.setBackgroundResource(R.drawable.shape_status_grey);
                        status = 5;

                        binding.btnWatching.setEnabled(false);
                        binding.btnCompleted.setEnabled(false);
                        binding.btnOnHold.setEnabled(false);
                        binding.btnDropped.setEnabled(false);
                        binding.btnPlanToWatch.setEnabled(true);

                        binding.ibProgressMin.setEnabled(false);
                        binding.ibProgressAdd.setEnabled(false);
                        binding.etProgress.setEnabled(false);
                        binding.ibScoreAdd.setEnabled(false);
                        binding.ibScoreMin.setEnabled(false);
                        binding.etScore.setEnabled(false);

                        binding.etProgress.setText("0");
                        binding.etScore.setText("0");

                    } else if (anime.isAiring()) {
                        isNotAired = false;
                        binding.btnCompleted.setEnabled(false);
                    } else if (!anime.getStatus().equals("Not yet aired") && animeStatus.getStatus() == 0) {
                        isNotAired = false;
                        binding.btnWatching.setBackgroundResource(R.drawable.shape_status_green);
                        status = 1;
                    }

                    binding.btnWatching.setOnClickListener(v -> {
                       if (status == 1) {
                           status = 0;
                           binding.btnWatching.setBackgroundResource(R.drawable.shape_status_no);
                       } else {
                           status = 1;
                           binding.btnWatching.setBackgroundResource(R.drawable.shape_status_green);

                           binding.btnCompleted.setBackgroundResource(R.drawable.shape_status_no);
                           binding.btnOnHold.setBackgroundResource(R.drawable.shape_status_no);
                           binding.btnDropped.setBackgroundResource(R.drawable.shape_status_no);
                           binding.btnPlanToWatch.setBackgroundResource(R.drawable.shape_status_no);

                       }
                    });
                    binding.btnCompleted.setOnClickListener(v -> {
                        if (status == 2) {
                            status = 0;
                            binding.btnCompleted.setBackgroundResource(R.drawable.shape_status_no);
                        } else {
                            status = 2;
                            binding.btnCompleted.setBackgroundResource(R.drawable.shape_status_blue);
                            binding.etProgress.setText(String.valueOf(episodes));

                            binding.btnWatching.setBackgroundResource(R.drawable.shape_status_no);
                            binding.btnOnHold.setBackgroundResource(R.drawable.shape_status_no);
                            binding.btnDropped.setBackgroundResource(R.drawable.shape_status_no);
                            binding.btnPlanToWatch.setBackgroundResource(R.drawable.shape_status_no);
                        }
                    });
                    binding.btnOnHold.setOnClickListener(v -> {
                        if (status == 3) {
                            status = 0;
                            binding.btnOnHold.setBackgroundResource(R.drawable.shape_status_no);
                        } else {
                            status = 3;
                            binding.btnOnHold.setBackgroundResource(R.drawable.shape_status_yellow);

                            binding.btnWatching.setBackgroundResource(R.drawable.shape_status_no);
                            binding.btnCompleted.setBackgroundResource(R.drawable.shape_status_no);
                            binding.btnDropped.setBackgroundResource(R.drawable.shape_status_no);
                            binding.btnPlanToWatch.setBackgroundResource(R.drawable.shape_status_no);
                        }
                    });
                    binding.btnDropped.setOnClickListener(v -> {
                        if (status == 4) {
                            status = 0;
                            binding.btnDropped.setBackgroundResource(R.drawable.shape_status_no);
                        } else {
                            status = 4;
                            binding.btnDropped.setBackgroundResource(R.drawable.shape_status_red);

                            binding.btnWatching.setBackgroundResource(R.drawable.shape_status_no);
                            binding.btnCompleted.setBackgroundResource(R.drawable.shape_status_no);
                            binding.btnOnHold.setBackgroundResource(R.drawable.shape_status_no);
                            binding.btnPlanToWatch.setBackgroundResource(R.drawable.shape_status_no);
                        }
                    });
                    binding.btnPlanToWatch.setOnClickListener(v -> {
                        if (status == 5) {
                            status = 0;
                            binding.btnPlanToWatch.setBackgroundResource(R.drawable.shape_status_no);
                        } else {
                            status = 5;
                            binding.btnPlanToWatch.setBackgroundResource(R.drawable.shape_status_grey);

                            binding.btnWatching.setBackgroundResource(R.drawable.shape_status_no);
                            binding.btnCompleted.setBackgroundResource(R.drawable.shape_status_no);
                            binding.btnOnHold.setBackgroundResource(R.drawable.shape_status_no);
                            binding.btnDropped.setBackgroundResource(R.drawable.shape_status_no);
                        }
                    });

                    if (episodes != 0) {
                        binding.tvEpisodes.setText(episodes + " ep");
                    } else {
                        binding.tvEpisodes.setText(" ? ");
                    }

                    binding.etStartDate.setOnClickListener(v -> {
                        isStartDate = true;
                        showDatePickerDialog(anime.getAired().getFrom());
                    });

                    binding.btnStartToday.setOnClickListener(v -> {
                        isStartDate = true;
                        setTodayDate();
                        binding.etStartDate.setText(startDate);

                        binding.etEndDate.setEnabled(true);
                        binding.btnEndToday.setEnabled(true);
                    });

                    if (startDate != null && !startDate.isEmpty()) {
                        binding.etEndDate.setEnabled(true);
                        binding.etEndDate.setClickable(true);
                        binding.btnEndToday.setEnabled(true);
                    } else {
                        binding.etEndDate.setEnabled(false);
                        binding.etEndDate.setClickable(false);
                        binding.btnEndToday.setEnabled(false);
                    }


                    binding.etEndDate.setOnClickListener(v -> {
                        isStartDate = false;
                        showDatePickerDialog(null);
                    });

                    binding.btnEndToday.setOnClickListener(v -> {
                        isStartDate = false;
                        setTodayDate();
                        binding.etEndDate.setText(endDate);
                    });

                    Runnable updateProgressButtons = () -> {
                        int val = Integer.parseInt(binding.etProgress.getText().toString());
                        binding.ibProgressAdd.setEnabled(episodes <= 0 || val < episodes);
                        binding.ibProgressMin.setEnabled(val > 0);
                    };

                    binding.ibProgressAdd.setOnClickListener(v -> {
                        int val = Integer.parseInt(binding.etProgress.getText().toString());
                        if (episodes <= 0 || val < episodes) {
                            val++;
                            binding.etProgress.setText(String.valueOf(val));
                            updateProgressButtons.run();
                        }
                    });

                    binding.ibProgressMin.setOnClickListener(v -> {
                        int val = Integer.parseInt(binding.etProgress.getText().toString());
                        if (val > 0) {
                            val--;
                            binding.etProgress.setText(String.valueOf(val));
                            updateProgressButtons.run();
                        }
                    });

                    binding.etProgress.addTextChangedListener(new TextWatcher() {
                        @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                        @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
                        @Override
                        public void afterTextChanged(Editable s) {
                            if (s.length() == 0) return;
                            int val = Integer.parseInt(s.toString());
                            if (episodes > 0 && val > episodes) {
                                binding.etProgress.setText(String.valueOf(episodes));
                            } else if (val < 0) {
                                binding.etProgress.setText("0");
                            }
                            updateProgressButtons.run();
                        }
                    });

                    Runnable updateScoreButtons = () -> {
                        int val = Integer.parseInt(binding.etScore.getText().toString());
                        binding.ibScoreAdd.setEnabled(val < 10);
                        binding.ibScoreMin.setEnabled(val > 0);
                    };

                    binding.ibScoreAdd.setOnClickListener(v -> {
                        int val = Integer.parseInt(binding.etScore.getText().toString());
                        if (val < 10) {
                            val++;
                            binding.etScore.setText(String.valueOf(val));
                            updateScoreButtons.run();
                        }
                    });

                    binding.ibScoreMin.setOnClickListener(v -> {
                        int val = Integer.parseInt(binding.etScore.getText().toString());
                        if (val > 0) {
                            val--;
                            binding.etScore.setText(String.valueOf(val));
                            updateScoreButtons.run();
                        }
                    });

                    binding.etScore.addTextChangedListener(new TextWatcher() {
                        @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
                        @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
                        @Override
                        public void afterTextChanged(Editable s) {
                            if (s.length() == 0) return;
                            int val = Integer.parseInt(s.toString());
                            if (val > 10) {
                                binding.etScore.setText(String.valueOf(10));
                            } else if (val < 0) {
                                binding.etScore.setText("0");
                            }
                            updateScoreButtons.run();
                        }
                    });

                    if (anime.getAired().getDate() != null) {
                        binding.tvDate.setText(anime.getAired().getDate());
                    } else {
                        binding.tvDate.setText(" - ");
                    }

                    title = anime.getTitle();
                    if (anime.getImages() != null) {
                        imageUrl = anime.getImages().getWebp().getLargeImageUrl();
                    } else {
                        imageUrl = anime.getImages().getWebp().getImageUrl();
                    }

                    if (anime.getType() != null) {
                        type = anime.getType();
                    } else {
                        type = " - ";
                    }

                    if (anime.getSeason() != null && anime.getYear() != null){
                        season = anime.getSeason() + " " + anime.getYear();
                    } else if (anime.getSeason() != null && anime.getYear() == null ) {
                        season = anime.getSeason() + " ?";
                    } else if (anime.getSeason() == null && anime.getYear() != null) {
                        season = String.valueOf(anime.getYear());
                    } else {
                        season = " - ";
                    }

                    if (anime.getEpisodes() != 0 ) {
                        episodes = anime.getEpisodes();
                    } else {
                        episodes = 0;
                    }





                    binding.btnSave.setOnClickListener(v -> saveAnimeStatus());


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



    private void saveAnimeStatus() {
        if (status == 0) {
            Toast.makeText(this, "Status cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }


        if ( isNotAired && status != 5) {
            Toast.makeText(this, "Not yet aired anime can only be set to Plan to Watch", Toast.LENGTH_SHORT).show();
            return;
        }

        animeStatus.setAnimeId(animeId);
        animeStatus.setStatus(status);

        try {
            int progress = Integer.parseInt(binding.etProgress.getText().toString().trim());
            animeStatus.setProgress(progress);
        } catch (NumberFormatException e) {
            animeStatus.setProgress(0);
        }

        try {
            int score = Integer.parseInt(binding.etScore.getText().toString().trim());
            animeStatus.setScore(score);
        } catch (NumberFormatException e) {
            animeStatus.setScore(0);
        }

        animeStatus.setStartDate(startDate);
        animeStatus.setEndDate(endDate);

        animeStatus.setTitle(title);
        animeStatus.setImageUrl(imageUrl);
        animeStatus.setType(type);
        animeStatus.setSeason(season);
        animeStatus.setEpisodes(episodes);

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.AnimeStatusColumns.ANIME_ID, animeStatus.getAnimeId());
        values.put(DatabaseContract.AnimeStatusColumns.SCORE, animeStatus.getScore());
        values.put(DatabaseContract.AnimeStatusColumns.PROGRESS, animeStatus.getProgress());
        values.put(DatabaseContract.AnimeStatusColumns.STATUS, animeStatus.getStatus());
        values.put(DatabaseContract.AnimeStatusColumns.START_DATE, animeStatus.getStartDate());
        values.put(DatabaseContract.AnimeStatusColumns.END_DATE, animeStatus.getEndDate());
        values.put(DatabaseContract.AnimeStatusColumns.TITLE, animeStatus.getTitle());
        values.put(DatabaseContract.AnimeStatusColumns.IMAGE_URL, animeStatus.getImageUrl());
        values.put(DatabaseContract.AnimeStatusColumns.TYPE, animeStatus.getType());
        values.put(DatabaseContract.AnimeStatusColumns.SEASON, animeStatus.getSeason());
        values.put(DatabaseContract.AnimeStatusColumns.EPISODES, animeStatus.getEpisodes());
        values.put(DatabaseContract.AnimeStatusColumns.UPDATED_DATE,
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        .format(new Date()));

        if (animeStatusHelper == null) {
            animeStatusHelper = AnimeStatusHelper.getInstance(this);
            animeStatusHelper.open();
        }

        long result;
        if (isEdit) {
            result = animeStatusHelper.update(String.valueOf(animeStatusId), values);
        } else {
            result = animeStatusHelper.insert(values);
        }

        if (result > 0) {
            Toast.makeText(this, "Anime successfully saved to list", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save anime to list", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteAnimeStatus() {
        if (animeStatus != null && animeStatus.getId() > 0) {
            long result = animeStatusHelper.deleteById(String.valueOf(animeStatus.getId()));

            if (result > 0) {
                finish();
            } else {
                Toast.makeText(this, "Failed to delete data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Failed to delete data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTodayDate() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
        String formattedDate = outputFormat.format(calendar.getTime());

        if (isStartDate) {
            startDate = formattedDate;
        } else {
            endDate = formattedDate;
        }
    }


    private void showDatePickerDialog(String minDateString) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);

                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                    String formattedDate = outputFormat.format(calendar.getTime());

                    if (isStartDate) {
                        startDate = formattedDate;
                        binding.etStartDate.setText(formattedDate);

                        binding.etEndDate.setEnabled(true);
                        binding.etEndDate.setClickable(true);
                        binding.btnEndToday.setEnabled(true);
                    } else {
                        endDate = formattedDate;
                        binding.etEndDate.setText(formattedDate);
                    }
                },
                year, month, day
        );

        if (minDateString != null && !minDateString.isEmpty()) {
            Date minDate = null;

            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US);
                minDate = isoFormat.parse(minDateString);
            } catch (ParseException e1) {
                try {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                    minDate = displayFormat.parse(minDateString);
                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }

            if (minDate != null && isStartDate) {
                datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
            }
        }

        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Clear", (dialog, which) -> {
            if (isStartDate) {
                startDate = null;
                binding.etStartDate.setText("");

                binding.etEndDate.setText("");
                binding.etEndDate.setEnabled(false);
                binding.etEndDate.setClickable(false);
                binding.btnEndToday.setEnabled(false);
            } else {
                endDate = null;
                binding.etEndDate.setText("");
            }
        });

        datePickerDialog.show();
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

    protected void onDestroy() {
        super.onDestroy();

        if (animeStatusHelper != null) {
            animeStatusHelper.close();
        }
        if (animeDetailCall != null) {
            animeDetailCall.cancel();
            animeDetailCall = null;
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
            loadAnime();
        };

        handler.postDelayed(loadingRunnable, delayNeeded);
    }
}