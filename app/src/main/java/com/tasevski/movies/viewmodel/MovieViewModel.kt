package com.tasevski.movies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tasevski.movies.model.MovieData
import com.tasevski.movies.utils.MovieSource
import kotlinx.coroutines.flow.Flow

class MovieViewModel: ViewModel() {

    val movie: Flow<PagingData<MovieData>> = Pager(PagingConfig(pageSize = 20)) {
        MovieSource()
    }.flow.cachedIn(viewModelScope)
}