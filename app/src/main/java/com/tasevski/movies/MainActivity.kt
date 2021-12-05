package com.tasevski.movies

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.coil.rememberCoilPainter
import com.tasevski.movies.model.MovieData
import com.tasevski.movies.ui.theme.moviePaginationTheme
import com.tasevski.movies.ui.theme.Purple500
import com.tasevski.movies.viewmodel.MovieViewModel
import kotlinx.coroutines.flow.Flow

class MainActivity : ComponentActivity() {
    private val movieViewModel: MovieViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            moviePaginationTheme {
                Surface(color = MaterialTheme.colors.background) {
                    movieList(
                        movieViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun movieList(movieViewModel: MovieViewModel) {
    movieListDetail(movieViewModel.movie)
}

@Composable
fun movieListDetail(movie: Flow<PagingData<MovieData>>) {
    val movieListItem: LazyPagingItems<MovieData> = movie.collectAsLazyPagingItems()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Purple500)
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Movies List",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if(movieListItem.itemCount!=0) LazyColumn {
            items(movieListItem) { item ->
                item?.let {
                    movieLists(it)
                }
            }
        }
        else Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun movieLists(movieData: MovieData) {
    val imageUrl = "http://image.tmdb.org/t/p/w500"
    Card(
        modifier = Modifier
            .padding(10.dp, 5.dp, 10.dp, 5.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        elevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
        ) {
            Surface(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                //Coil Image
                val image = rememberCoilPainter(
                    request = imageUrl+movieData.poster_path,
                    fadeIn = true
                )

                Image(
                    painter = image,
                    contentDescription = "Movie Image",
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 15.dp)
                    .align(CenterVertically)
            ) {
                Text(
                    text = movieData.original_title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Text(
                    text = movieData.overview,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}

























