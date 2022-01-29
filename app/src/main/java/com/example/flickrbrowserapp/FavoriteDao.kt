package com.example.flickrbrowserapp

import androidx.room.*

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM Favorite")
    fun gettingAllData(): List<Data>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addNewFavorite(favorite: Data)

    @Delete
    fun deleteFavorite(favorite: Data)
}