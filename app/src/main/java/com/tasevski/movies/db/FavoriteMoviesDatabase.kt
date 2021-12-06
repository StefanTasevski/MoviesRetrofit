package com.tasevski.movies.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tasevski.movies.model.MovieData

@Database(entities = [MovieData::class], version = 1, exportSchema = false)
abstract class FavoriteMoviesDatabase : RoomDatabase() {
    abstract fun favoriteMoviesDao(): FavoriteMoviesDao

    companion object {
        @Volatile
        private var INSTANCE: FavoriteMoviesDatabase? = null

        fun getDatabase(context: Context): FavoriteMoviesDatabase{
            val tempInstance = INSTANCE
            if(tempInstance!= null) {
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteMoviesDatabase::class.java,
                    "database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}