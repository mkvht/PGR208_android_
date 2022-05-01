package com.example.pgr208_android_eksamen.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pgr208_android_eksamen.MainActivity
import com.example.pgr208_android_eksamen.R
import com.example.pgr208_android_eksamen.adapters.ImageAdapter
import com.example.pgr208_android_eksamen.models.ImageApiResponse
import com.example.pgr208_android_eksamen.models.ImageModel


class SavedResultFragment() :
    Fragment(R.layout.fragment_view_result) {
    private lateinit var binding: FragmentViewSavedResultBinding
    private val args: ViewSavedResultFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val savedImage: ImageModel = args.savedImage
        val database = (activity as MainActivity).database
        binding = FragmentViewSavedResultBinding.inflate(inflater, container, false)
        val byteArray = savedImage.image
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        binding.savedImage.setImageBitmap(bitmap)

        //Get the URL's for the saved image and convert it to ImageApiResponses that the gallery can preview
        val results = savedImage.id?.let { database.getResultsForSavedImage(it) }
        val mappedResults = results?.map { ImageApiResponse(it.url, it.url) }?.toList()

        val galleryRv = binding.imagesContainer
        galleryRv.layoutManager = GridLayoutManager(activity, 2)
        galleryRv.adapter = mappedResults?.let { ImageAdapter(it) }

        binding.deleteImageBtn.setOnClickListener {
            val action = ViewSavedResultFragmentDirections.actionSavedResultsPreviewFragmentToSavedResultsFragment()
            (activity as MainActivity).database.deleteImage(savedImage)
            requireView().findNavController().navigate(action)
        }
        return binding.root
    }
}