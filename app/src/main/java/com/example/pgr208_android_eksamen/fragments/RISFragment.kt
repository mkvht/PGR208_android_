package com.example.pgr208_android_eksamen.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pgr208_android_eksamen.MainActivity
import com.example.pgr208_android_eksamen.R
import com.example.pgr208_android_eksamen.adapters.ImageAdapter
import com.example.pgr208_android_eksamen.controllers.ApiController
import com.example.pgr208_android_eksamen.models.ImageApiResponse
import com.example.pgr208_android_eksamen.models.ImageModel
import com.example.pgr208_android_eksamen.models.ResultModel
import com.example.pgr208_android_eksamen.utilities.BitmapUtility
import com.example.pgr208_android_eksamen.databinding.FragmentReverseImageSearchBinding

class RISFragment : Fragment(R.layout.fragment_reverse_image_search) {

    private lateinit var binding: FragmentReverseImageSearchBinding
    private lateinit var imageUri: Uri
    private var bingResults = listOf<ImageApiResponse>()
    private var tineyeResults = listOf<ImageApiResponse>()
    private var googleResults = listOf<ImageApiResponse>()
    private val args by navArgs<RISFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        imageUri = Uri.parse(args.currentURI)
        binding = FragmentReverseImageSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        val rv = binding.imagesContainer
        rv.layoutManager = GridLayoutManager(activity, 2)

        uploadingImage()

        binding.uploadImage.setImageURI(imageUri)

        binding.bingBtn.setOnClickListener {
            //Bing results
            rv.adapter = ImageAdapter(bingResults)
        }

        binding.googleBtn.setOnClickListener {
            //Google results
            rv.adapter = ImageAdapter(googleResults)
        }

        binding.tineyeBtn.setOnClickListener {
            //Tineye results
            rv.adapter = ImageAdapter(tineyeResults)

        }

        binding.btnSave.setOnClickListener {
            val database = (activity as MainActivity).database
            val bitmap = BitmapUtility.uriToBitmap(requireContext(), null, imageUri.toString())
            val byteArray = BitmapUtility.bitmapToByteArray(bitmap)
            val imageId = database.insertImageSearch(ImageModel(image = byteArray))
            bingResults.forEach {
                database.insertImageResult(ResultModel(url = it.imageLink, searchId = imageId))
            }
        }
        view.findViewById<ImageView>(R.id.upload_image).setBackgroundResource(R.drawable.shadow_rect)
        return view
    }

    private fun uploadingImage() {
        val context = activity?.applicationContext

        //Servers respond with resulting images
        val finishedFetchingResults = { endpoint: String ->
            Toast.makeText(context, "Finished processing $endpoint. Please click the buttons above to view results", Toast.LENGTH_LONG
            ).show()
        }

        //Error: a reverse search has a fault
        val preformingReverseSearchError = { endpoint: String ->
            Toast.makeText(context, "A problem has occurred while preforming reverse search on $endpoint", Toast.LENGTH_LONG
            ).show()
        }

        //Error: no matching images from the endpoint
        val noSimilarImagesFound = { endpoint: String ->
            Toast.makeText(context, "Could not find any matching images from $endpoint. Please try a another image", Toast.LENGTH_LONG
            ).show()
        }

        val uploadedToServer = { url: String ->
            Toast.makeText(context, "The image has been successfully uploaded to the server. One moment please...", Toast.LENGTH_LONG
                ).show()


                ApiController.reverseSearch(ApiController.Endpoint.PROVIDER_BING, url) {
                    result, error ->
                result?.let{
                    if (result.isEmpty()) noSimilarImagesFound("Bing")
                    finishedFetchingResults("Bing")
                    bingResults = result
                }
                error?.let{ preformingReverseSearchError("Bing") }
            }

            ApiController.reverseSearch(ApiController.Endpoint.PROVIDER_GOOGLE, url){
                    result, error ->
                result?.let {
                    if (result.isEmpty()) noSimilarImagesFound("Google")
                    finishedFetchingResults("Google")
                    googleResults = result
                }
                error?.let{ preformingReverseSearchError("Google") }
            }

            ApiController.reverseSearch(ApiController.Endpoint.PROVIDER_TINEYE, url){
                    result, error ->
                result?.let {
                    if (result.isEmpty()) noSimilarImagesFound("Tineye")
                    tineyeResults = result
                }
                error?.let{ preformingReverseSearchError("Tineye") }
            }
        }

        imageUri.let {
            ApiController.uploadImage(
                it.toFile()
            ) { uploadUrl, error ->
                uploadUrl?.let { uploadedToServer(uploadUrl) }
                error?.let {
                    Toast.makeText(context, "There was an error uploading the image to the server...", Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
    }
}