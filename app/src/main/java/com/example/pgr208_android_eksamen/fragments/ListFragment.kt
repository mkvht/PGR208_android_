package com.example.pgr208_android_eksamen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pgr208_android_eksamen.MainActivity
import com.example.pgr208_android_eksamen.R
import com.example.pgr208_android_eksamen.adapters.ListAdapter
import com.example.pgr208_android_eksamen.databinding.FragmentAlbumBinding

class ListFragment: Fragment(R.layout.fragment_album) {

    private lateinit var binding: FragmentAlbumBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAlbumBinding.inflate(inflater, container, false)
        val view = binding.root

        val database = (activity as MainActivity).database
        val savedImages = database.getAllSavedImages()

        val recyclerView = binding.savedResultsRv
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = ListAdapter(savedImages)

        return view
    }


}