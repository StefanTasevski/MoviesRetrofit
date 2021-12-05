package com.tasevski.movies.utils

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tasevski.movies.BuildConfig
import com.tasevski.movies.model.MovieData
import com.tasevski.movies.network.ApiClient
import retrofit2.HttpException
import java.io.IOException

class MovieSource: PagingSource<Int, MovieData>() {

    override fun getRefreshKey(state: PagingState<Int, MovieData>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieData> {

        return try {
            val nextPage = params.key ?: 1
            val movieList = ApiClient.apiService.getMovieList(BuildConfig.API_KEY,nextPage)
            LoadResult.Page(
                data = movieList.results,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (movieList.results.isEmpty()) null else movieList.page + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}