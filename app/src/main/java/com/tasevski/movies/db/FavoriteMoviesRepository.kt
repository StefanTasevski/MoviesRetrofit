package com.tasevski.movies.db

import com.tasevski.movies.model.MovieData
import kotlinx.coroutines.flow.Flow

class FavoriteMoviesRepository(private val favoriteMoviesDao: FavoriteMoviesDao) {
    val getAllMovies = favoriteMoviesDao.getAllMovies()

    suspend fun addMovie(movie: MovieData) {
        favoriteMoviesDao.insertMovies(movie)
    }

    suspend fun deleteMovie(movie: MovieData){
        favoriteMoviesDao.deleteMovies(movie)
    }

    fun contains(movieId: Int): Flow<Boolean> {
        return favoriteMoviesDao.contains(movieId)
    }
}