package com.example.pgr208_android_eksamen.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.ListFragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.pgr208_android_eksamen.R
import com.example.pgr208_android_eksamen.fragments.ListFragmentDirections
import com.example.pgr208_android_eksamen.models.ImageModel

class ListAdapter(private val savedImages: List<ImageModel>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.resultItemThumbnail)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.result_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val byteArray = savedImages[position].image
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        holder.imageView.setImageBitmap(bitmap)
        holder.view.setOnClickListener {
            val action = ListFragmentDirections.actionSavedResultsFragmentToSavedResultsPreviewFragment(savedImages[position])
            holder.view.findFragment<ListFragment>().findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return savedImages.size
    }
}