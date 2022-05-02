package com.example.pgr208_android_eksamen.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.pgr208_android_eksamen.R
import com.example.pgr208_android_eksamen.models.ImageApiResponse
import com.squareup.picasso.Picasso
import kotlinx.coroutines.NonDisposableHandle.parent

class ImageAdapter(
    private val images: List<ImageApiResponse>):
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val imageView: ImageView = view.findViewById<ImageView?>(R.id.galleryImg)
    }
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.gallery_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Picasso.get().load(images[position].link).into(viewHolder.imageView)
    }

    override fun getItemCount(): Int {
        return images.size
    }
}