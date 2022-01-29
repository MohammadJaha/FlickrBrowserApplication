package com.example.flickrbrowserapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.ImageView
import com.bumptech.glide.Glide
import io.github.muddz.styleabletoast.StyleableToast


class GridAdapter (private val myClass : Context, private val list : ArrayList<Data>): BaseAdapter(){

    private val inflater: LayoutInflater = myClass.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(p0: Int): Any {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var view = p1
        if (view == null)
            view = inflater.inflate(R.layout.images_show_grid, p2, false)!!

        val showImage = view.findViewById<ImageView>(R.id.showImage)
        val favoriteCheck= view.findViewById<CheckBox>(R.id.favoriteCheck)

        favoriteCheck.setOnCheckedChangeListener(null)

        Glide.with(view)
            .load("https://live.staticflickr.com/${list[p0].server_id}/${list[p0].photo_id}_${list[p0].secretNumber}.jpg")
            .into(showImage!!)

        favoriteCheck.isChecked = list[p0].checkBox
        favoriteCheck?.setOnCheckedChangeListener{_, checked ->
            when (checked) {
                true -> {
                    list[p0].checkBox = true
                    StyleableToast.makeText(
                        myClass,
                        "Add Successfully!!",
                        R.style.myToast
                    ).show()
                }
                else -> {
                    list[p0].checkBox = false
                    StyleableToast.makeText(
                        myClass,
                        "Deleted Successfully!!",
                        R.style.myToast
                    ).show()
                }
            }
        }
        return view
    }
}