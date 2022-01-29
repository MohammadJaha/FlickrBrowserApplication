package com.example.flickrbrowserapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Favorite")
data class Data(
    @PrimaryKey(autoGenerate = true) val pk: Int,
    val title: String,
    val server_id: String,
    val photo_id: String,
    val secretNumber: String,
    var checkBox: Boolean
    )
