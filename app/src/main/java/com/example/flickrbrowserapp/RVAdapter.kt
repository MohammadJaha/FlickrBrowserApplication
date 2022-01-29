package com.example.flickrbrowserapp

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flickrbrowserapp.databinding.ImagesViewBinding
import io.github.muddz.styleabletoast.StyleableToast

class RVAdapter (private val context: Context,private var list: ArrayList<Data>, private val classNumber: Int): RecyclerView.Adapter<RVAdapter.ItemViewHolder>() {
    class ItemViewHolder(val binding: ImagesViewBinding, listener: OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener{
                listener.onItemClick(adapterPosition)
            }
        }
    }

    private lateinit var hold: RecyclerView.ViewHolder
    private lateinit var myListener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener:OnItemClickListener ){
        myListener=listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ImagesViewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            myListener
        )
    }

    override fun onViewRecycled(holder: ItemViewHolder) {
        super.onViewRecycled(holder)
        holder.binding.checkBox.setOnCheckedChangeListener(null)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val photo = list[position]

        holder.binding.apply {
            checkBox.isChecked = photo.checkBox
            titleTV.text = photo.title
            Glide.with(mainLay)
                .load("https://live.staticflickr.com/${photo.server_id}/${photo.photo_id}_${photo.secretNumber}.jpg")
                .into(imageView)
        }
        holder.binding.checkBox.setOnCheckedChangeListener { _, checked ->
            if (classNumber==1) {
                when (checked) {
                    true -> {
                        photo.checkBox = true
                        StyleableToast.makeText(
                            context,
                            "Add Successfully!!",
                            R.style.myToast
                        ).show()
                    }
                    else -> {
                        photo.checkBox = false
                        StyleableToast.makeText(
                            context,
                            "Deleted Successfully!!",
                            R.style.myToast
                        ).show()
                    }
                }
            }
            else{
                when (checked) {
                    false -> {
                        list.removeAt(position)
                        StyleableToast.makeText(
                            context,
                            "Deleted Successfully!!",
                            R.style.myToast
                        ).show()
                        update()
                    }
                }
            }
        }
    }

    override fun getItemCount() = list.size

    fun update(){
        notifyDataSetChanged()
    }
}