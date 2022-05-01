package com.example.pgr208_android_eksamen.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pgr208_android_eksamen.MainActivity
import com.example.pgr208_android_eksamen.adapters.ImageAdapter
import com.example.pgr208_android_eksamen.controllers.ApiController
import com.example.pgr208_android_eksamen.models.ImageApiResponse
import com.example.pgr208_android_eksamen.models.ImageModel
import com.example.pgr208_android_eksamen.models.ResultModel
import com.example.pgr208_android_eksamen.utilities.BitmapUtility

class RISFragment : Fragment(R.layout.fragment_reverse_image_search) {

    private lateinit var binding: FragmentReverseImageSearchBinding
    private lateinit var imageUri: Uri
    private var bingResponse = listOf<ImageApiResponse>()
    private var tineyeResponse = listOf<ImageApiResponse>()
    private var googleResponse = listOf<ImageApiResponse>()
    private val args by navArgs<ReverseImageSearchFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {

        imageUri = Uri.parse(args.currentURI)
        binding = FragmentReverseImageSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        val rv = binding.imagesContainer
        rv.layoutManager = GridLayoutManager(activity, 2)

        uploadImage()

        binding.uploadPreviewIv.setImageURI(imageUri)
        binding.btnGoogle.setOnClickListener {
            //Show google results
            rv.adapter = ImageAdapter(googleResponse)
        }

        binding.btnBing.setOnClickListener {
            //Show bing results
            rv.adapter = ImageAdapter(bingResponse)

        }

        binding.btnTineye.setOnClickListener {
            //Show tineye results
            rv.adapter = ImageAdapter(tineyeResponse)

        }

        binding.btnSave.setOnClickListener {
            val database = (activity as MainActivity).database
            //TODO - save the image in a more long-term location
            val bitmap = BitmapUtility.uriToBitmap(requireContext(), null, imageUri.toString())
            val byteArray = BitmapUtility.bitmapToByteArray(bitmap)
            val imageId = database.insertImageSearch(ImageModel(image = byteArray))
            bingResponse.forEach {
                database.insertImageResult(ResultModel(url = it.imageLink, searchId = imageId))
            }
        }
        return view
    }

    /**
     * Method that automatically uploads an image to the server and attempts to fetch results
     */
    private fun uploadImage() {
        val context = activity?.applicationContext

        //Error message when there are no matching images from the endpoint
        val noMatchingImages = { endpoint: String ->
            Toast.makeText(context, "No matching images returned from $endpoint. Try a different image", Toast.LENGTH_LONG).show()
        }

        //Error message when a reverse search throws an error
        val errorReverseSearch = { endpoint: String ->
            Toast.makeText(context, "There was an error performing reverse search on $endpoint", Toast.LENGTH_LONG).show()
        }

        //Message for when the servers respond with resulting images
        val finishedFetching = { endpoint: String ->
            Toast.makeText(
                context,
                "Results returned from $endpoint. Click the corresponding button to see the result",
                Toast.LENGTH_LONG
            ).show()
        }

        val onSuccessUpload = { url: String ->
            Toast.makeText(
                context,
                "Image successfully uploaded to server. Performing reverse search. This might take a while...",
                Toast.LENGTH_LONG
            ).show()

            ApiController.reverseSearch(ApiController.Endpoint.PROVIDER_BING, url) {
                    result, error ->
                result?.let{
                    if (result.isEmpty()) noMatchingImages("Bing")
                    finishedFetching("Bing")
                    bingResponse = result
                }
                error?.let{ errorReverseSearch("Bing") }
            }

            ApiController.reverseSearch(ApiController.Endpoint.PROVIDER_GOOGLE, url){
                    result, error ->
                result?.let {
                    if (result.isEmpty()) noMatchingImages("Google")
                    finishedFetching("Google")
                    googleResponse = result
                }
                error?.let{ errorReverseSearch("Google") }
            }

            ApiController.reverseSearch(ApiController.Endpoint.PROVIDER_TINEYE, url){
                    result, error ->
                result?.let {
                    if (result.isEmpty()) noMatchingImages("Tineye")
                    tineyeResponse = result
                }
                error?.let{ errorReverseSearch("Tineye") }
            }
        }

        imageUri.let {
            ApiController.uploadImage(
                it.toFile()
            ) { uploadUrl, error ->
                uploadUrl?.let { onSuccessUpload(uploadUrl) }
                error?.let {
                    Toast.makeText(
                        context,
                        "There was an error uploading the image to the server...",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
    }
}