package com.ouz.myartbookkotlin.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ouz.myartbookkotlin.data.ImagesEntity

import com.ouz.myartbookkotlin.databinding.ItemCardBinding
import com.ouz.myartbookkotlin.fragments.Converters

class Adapter(private val imagesList: List<ImagesEntity?>) :
    RecyclerView.Adapter<Adapter.MainDesign>() {
    class MainDesign(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    var userInfoTransfer: (ImagesEntity) -> Unit = {}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainDesign {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCardBinding.inflate(inflater, parent, false)
        return MainDesign(binding)
    }

    override fun onBindViewHolder(holder: MainDesign, position: Int) {
        val images = imagesList[position]
        holder.binding.apply {
            if (images != null) {
                val bitmapImage = Converters().toBitmap(images.image)
                textCard.text = images.artName
                imageView.setImageBitmap(bitmapImage)

                cardView.setOnClickListener {
                    userInfoTransfer(images)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }

}