package com.tasevski.movies.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class MovieResponse(
    var page: Int,
    var results: List<MovieData>
)

@Entity(tableName = "movies")
data class MovieData(
    @PrimaryKey var id: Int,
    var original_title: String,
    var overview: String,
    var poster_path: String
)
