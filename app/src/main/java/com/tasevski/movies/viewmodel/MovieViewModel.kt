package com.tasevski.movies.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tasevski.movies.db.FavoriteMoviesDatabase
import com.tasevski.movies.db.FavoriteMoviesRepository
import com.tasevski.movies.model.MovieData
import com.tasevski.movies.utils.MovieSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MovieViewModel(application: Application): AndroidViewModel(application) {

    val movie: Flow<PagingData<MovieData>> = Pager(PagingConfig(pageSize = 20)) {
        MovieSource()
    }.flow.cachedIn(viewModelScope)

    private val repository: FavoriteMoviesRepository
    val getAllMovies: Flow<List<MovieData>>

    init {
        val moviesDao = FavoriteMoviesDatabase.getDatabase(application).favoriteMoviesDao()
        repository = FavoriteMoviesRepository(moviesDao)
        getAllMovies = repository.getAllMovies
    }

    fun addMovie(movie: MovieData) {
        viewModelScope.launch {
            repository.addMovie(movie)
        }
    }

    fun deleteMovie(movie: MovieData) {
        viewModelScope.launch {
            repository.deleteMovie(movie)
        }
    }

    fun contains(movie: MovieData): Flow<Boolean> {
        return repository.contains(movie.id)
    }
}

class MovieViewModelFactory(
    private val application: Application
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("Unchecked cast")
        if(modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            return MovieViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}