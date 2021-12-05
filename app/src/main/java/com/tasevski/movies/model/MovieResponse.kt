package com.tasevski.movies.model

data class MovieResponse(
    var page: Int,
    var results: List<MovieData>
)

data class MovieData(
    var id: Int,
    var original_title: String,
    var overview: String,
    var poster_path: String
)
