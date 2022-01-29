package com.example.flickrbrowserapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ImageShow : AppCompatActivity() {

    private lateinit var imageShow: ImageView
    private lateinit var exitImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_show)

        imageShow= findViewById(R.id.imageShow)
        exitImage= findViewById(R.id.exitImage)

        this.title= intent.extras?.getString("title")
        val serverID= intent.extras?.getString("serverID")
        val photoID= intent.extras?.getString("photoID")
        val secretNumber= intent.extras?.getString("secretNumber")

        Glide.with(this)
            .load("https://live.staticflickr.com/${serverID}/${photoID}_${secretNumber}.jpg")
            .into(imageShow)

        exitImage.setOnClickListener{
            finish()
            //startActivity(Intent(this,MainActivity::class.java))
        }

    }
}