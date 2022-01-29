package com.example.flickrbrowserapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Data::class],version = 1, exportSchema = false)
abstract class FavoriteDatabase: RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object{
        @Volatile
        var instance: FavoriteDatabase? =null

        fun getInstance(context: Context): FavoriteDatabase{
            if (instance!=null)
                return instance!!
            synchronized(this){
                instance= Room.databaseBuilder(context.applicationContext,
                FavoriteDatabase::class.java,
                "Favorites")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance!!
        }
    }
}