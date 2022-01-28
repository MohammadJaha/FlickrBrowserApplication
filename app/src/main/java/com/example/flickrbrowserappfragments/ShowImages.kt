package com.example.flickrbrowserappfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import com.bumptech.glide.Glide

class ShowImages : Fragment() {

    private lateinit var imageShow: ImageView
    private lateinit var exitImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewUI = inflater.inflate(R.layout.fragment_show_images, container, false)
        imageShow = viewUI.findViewById(R.id.imageShow)
        exitImage = viewUI.findViewById(R.id.exitImage)

        activity?.title = arguments?.getString("title")!!
        val serverID = arguments?.getString("serverID")!!
        val photoID = arguments?.getString("photoID")!!
        val secretNumber = arguments?.getString("secretNumber")!!

        Glide.with(requireContext())
            .load("https://live.staticflickr.com/${serverID}/${photoID}_${secretNumber}.jpg")
            .into(imageShow)

        exitImage.setOnClickListener {
            Navigation.findNavController(viewUI).navigate(R.id.action_showImages_to_searchImages)
        }
        return viewUI
    }


}