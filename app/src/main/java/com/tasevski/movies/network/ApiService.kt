package com.tasevski.movies.network

import com.tasevski.movies.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("movie")
    suspend fun getMovieList(@Query("api_key") api_key: String, @Query("page") page: Int): MovieResponse
}