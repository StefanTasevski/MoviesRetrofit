package com.tasevski.movies.db

import androidx.room.*
import com.tasevski.movies.model.MovieData
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMoviesDao {
    @Query("SELECT * FROM movies")
    fun getAllMovies(): Flow<List<MovieData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: MovieData) : Long

    @Delete
    suspend fun deleteMovies(movies: MovieData) : Int

    @Query("SELECT EXISTS(SELECT * FROM movies WHERE id = :id)")
    fun contains(id : Int) : Flow<Boolean>
}