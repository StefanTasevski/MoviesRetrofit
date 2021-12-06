package com.tasevski.movies

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.snapshots.Snapshot.Companion.observe
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.navArgument
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.gson.Gson
import com.tasevski.movies.model.MovieData
import com.tasevski.movies.ui.theme.moviePaginationTheme
import com.tasevski.movies.ui.theme.Purple500
import com.tasevski.movies.viewmodel.MovieViewModel
import com.tasevski.movies.viewmodel.MovieViewModelFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toCollection

class MainActivity : ComponentActivity() {

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            moviePaginationTheme {
                Surface(color = MaterialTheme.colors.background) {
                    NavAnimation()
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun NavAnimation() {
    val context = LocalContext.current
    val movieViewModel: MovieViewModel = viewModel(
        factory = MovieViewModelFactory(context.applicationContext as Application)
    )
    val navController = rememberAnimatedNavController()
    AnimatedNavHost(
        navController = navController,
        startDestination = "movieList"
    ) {
        composable(
            "movieList",
            enterTransition = { initial, _ ->
                when (initial.destination.route) {
                    "Favorite" ->
                        slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(800))
                    else -> null
                }
            },
            exitTransition = { _, target ->
                when (target.destination.route) {
                    "Favorite" ->
                        slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(800))
                    else -> null
                }
            },
            popEnterTransition = { initial, _ ->
                when (initial.destination.route) {
                    "Favorite" ->
                        slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(800))
                    else -> null
                }
            },
            popExitTransition = { _, target ->
                when (target.destination.route) {
                    "Favorite" ->
                        slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(800))
                    else -> null
                }
            }
        ) {
            movieListDetail(movieViewModel, navController)
        }

        //Second Screen
        composable(
            "Favorite",
            enterTransition = { initial, _ ->
                when (initial.destination.route) {
                    "movieList" ->
                        slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(800))
                    else -> null
                }
            },
            exitTransition = { _, target ->
                when (target.destination.route) {
                    "movieList" ->
                        slideOutOfContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(800))
                    else -> null
                }
            },
            popEnterTransition = { initial, _ ->
                when (initial.destination.route) {
                    "movieList" ->
                        slideIntoContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(800))
                    else -> null
                }
            },
            popExitTransition = { _, target ->
                when (target.destination.route) {
                    "movieList" ->
                        slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(800))
                    else -> null
                }
            }
        ) {
            favoriteMovieListDetail(movieViewModel, navController)
        }

        composable(
            "detailsView?movie={movie}",
            arguments = listOf(
                navArgument("movie") { defaultValue = "" }
            )
        ) { backStackEntry ->
            backStackEntry.arguments?.getString("movie")?.let { json ->
                val movie = Gson().fromJson(json, MovieData::class.java)
                detailsItem(movie, movieViewModel)
            }
        }
    }
}


@Composable
fun movieListDetail(movieViewModel: MovieViewModel, navController: NavHostController) {
    val movieListItem: LazyPagingItems<MovieData> = movieViewModel.movie.collectAsLazyPagingItems()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Purple500)
                .absolutePadding(15.dp, 5.dp, 15.dp, 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Icon(
                Icons.Filled.Star,
                contentDescription = "",
                tint = Purple500
            )
            Text(
                text = "Movies List",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
            IconButton(
                onClick = { navController.navigate("Favorite") },
            ){
                Icon(
                    Icons.Filled.Star,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if(movieListItem.itemCount!=0) LazyColumn {
            items(movieListItem) { item ->
                item?.let {
                    listItem(it, navController)
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
fun listItem(movieData: MovieData, navController: NavHostController) {
    val imageUrl = "http://image.tmdb.org/t/p/w500"
    fun navigateToMovie(movie: MovieData) {
        val movieJson = Gson().toJson(movie)
        navController.navigate("detailsView?movie=$movieJson")
    }

    Card(
        modifier = Modifier
            .padding(10.dp, 5.dp, 10.dp, 5.dp)
            .fillMaxWidth()
            .clickable(onClick = { navigateToMovie(movieData) }),
        shape = RoundedCornerShape(10.dp),
        elevation = 12.dp
    ) {
        Row(
            modifier = Modifier
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

@Composable
fun detailsItem(movieData: MovieData, movieViewModel: MovieViewModel) {
    val imageUrl = "http://image.tmdb.org/t/p/w500"
    val contains = movieViewModel.contains(movieData).collectAsState(null, movieViewModel.viewModelScope.coroutineContext)
    Box(
        modifier = Modifier
            .background(Color.White),
        contentAlignment = Alignment.BottomCenter
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
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color(0xCCFFFFFF))
                .padding(5.dp)) {

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
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 10.dp)
            )

            Button(
                modifier = Modifier.padding(10.dp),
                onClick= {if(contains.value == true)
                    movieViewModel.deleteMovie(movieData)
                else if(contains.value == false)
                    movieViewModel.addMovie(movieData)}
            ) {
                if(contains.value == true)
                    Text(
                        text = "Remove from favorites"
                    )
                else if(contains.value == false)
                    Text(
                        text = "Add to favorites"
                    )
            }
        }
    }
}

@Composable
fun favoriteMovieListDetail(movieViewModel: MovieViewModel, navController: NavHostController) {
    val movie=movieViewModel.getAllMovies.collectAsState(listOf(), movieViewModel.viewModelScope.coroutineContext)
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Purple500)
                .absolutePadding(15.dp, 5.dp, 15.dp, 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Icon(
                Icons.Filled.Star,
                contentDescription = "",
                tint = Purple500
            )
            Text(
                text = "Favorites",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
            IconButton(
                onClick = { navController.navigate("movieList") },
            ){
                Icon(
                    Icons.Filled.List,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (movie.value.isNotEmpty()) Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(weight =1f, fill = false)
        ) {
            for (item in movie.value) {
                listItem(item, navController)
            }
        }
        else Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "There are no movies in this list."
            )
        }
    }
}

























