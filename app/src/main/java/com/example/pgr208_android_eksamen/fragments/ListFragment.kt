package com.example.pgr208_android_eksamen.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pgr208_android_eksamen.MainActivity
import com.example.pgr208_android_eksamen.adapters.ListAdapter

class ListFragment: Fragment(R.layout.fragment_list_saved_results) {

    private lateinit var binding: FragmentListSavedResultsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListSavedResultsBinding.inflate(inflater, container, false)
        val view = binding.root

        val database = (activity as MainActivity).database
        val savedImages = database.getAllSavedImages()

        val recyclerView = binding.savedResultsRv
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = ListAdapter(savedImages)

        return view
    }


}