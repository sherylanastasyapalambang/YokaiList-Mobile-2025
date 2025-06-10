package com.example.yokailist.data.network;

import com.example.yokailist.data.response.AnimeDetailResponse;
import com.example.yokailist.data.response.AnimeResponse;
import com.example.yokailist.data.response.CharacterResponse;
import com.example.yokailist.data.response.genre.GenreResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("anime/{id}")
    Call<AnimeDetailResponse> getAnimeDetail(@Path("id") int id);

    @GET("anime/{id}/characters")
    Call<CharacterResponse> getAnimeCharacters(@Path("id") int id);

    @GET("seasons/now")
    Call<AnimeResponse> getNowSeasonAnime(@Query("page") int page);

    @GET("seasons/upcoming")
    Call<AnimeResponse> getUpcomingSeasonAnime(@Query("page") int page);

    @GET("top/anime")
    Call<AnimeResponse> getTopAnime();

    @GET("anime")
    Call<AnimeResponse> getAiringAnime(
            @Query("type") String type,
            @Query("status") String status,
            @Query("order_by") String orderBy,
            @Query("sort") String sort,
            @Query("limit") int limit
    );

    @GET("genres/anime")
    Call<GenreResponse> getGenres();

    @GET("anime")
    Call<AnimeResponse> getAnimeSearch(
            @Query("q") String title,
            @Query("genres") String genres,
            @Query("page") int page
    );

}
