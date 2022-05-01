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

class ListAdapter(private val savedImages: List<SavedImageModel>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    class ViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val imageView: ImageView = view.findViewById(R.id.resultItemThumbnail)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.saved_results_item, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val byteArray = savedImages[position].image
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        viewHolder.imageView.setImageBitmap(bitmap)
        viewHolder.view.setOnClickListener {
            val dothis = ListFragmentDirections.actionSavedResultsFragmentToSavedResultsPreviewFragment(savedImages[position])
            viewHolder.view.findFragment<ListFragment>().findNavController().navigate(dothis)
        }
    }

    override fun getItemCount(): Int {
        return savedImages.size
    }