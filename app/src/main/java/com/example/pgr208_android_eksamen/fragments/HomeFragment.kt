package com.example.pgr208_android_eksamen.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pgr208_android_eksamen.MainActivity
import com.example.pgr208_android_eksamen.R
import com.example.pgr208_android_eksamen.databinding.FragmentHomeBinding
import com.example.pgr208_android_eksamen.utilities.BitmapUtility
import com.example.pgr208_android_eksamen.utilities.BitmapUtility.uriToBitmap
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat

import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    var vFilename: String = ""

    private lateinit var binding: FragmentHomeBinding
    private var imageUri : Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.uploadBtn.setOnClickListener{
            selectImageAndUpload()
        }

        binding.cameraBtn.setOnClickListener{
            selectCameraAndUpload()
        }

        binding.searchBtn.setOnClickListener{
            imageUri?.let {
                Toast.makeText(this.context, "Searching image, please wait...", Toast.LENGTH_LONG).show()
                val action = HomeFragmentDirections.actionImagePreviewFragmentToUploadFragment(imageUri.toString())
                findNavController().navigate(action)
            }?: run{
                Toast.makeText(this.context, "You must upload or use the camera to get an image to search for first!", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun selectImageAndUpload() {
        when { ContextCompat.checkSelfPermission(
            activity as MainActivity,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity as MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showPermissionDeniedDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
    lateinit var currentPhotoPath: String
    private fun createImageFile():File{
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun selectCameraAndUpload() {

        when { ContextCompat.checkSelfPermission(activity as MainActivity,
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
            //https://gist.github.com/dyazincahya/8eb2c63afda9d049944ba2257e3edad7
            val packageManager: PackageManager = (activity as MainActivity).packageManager
            //Set Intent
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
                intent.resolveActivity(packageManager)?.also {
                    val photoFile: File? = try {
                        createImageFile()
                    }catch (ex: IOException){
                        null
                    }
                    photoFile?.also {
                        val photoURI: Uri = FileProvider.getUriForFile(
                            activity as MainActivity,
                            "com.example.prg208_android_eksamen",
                            it)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI)
                        cameraLauncher.launch(intent)
                    }
                }
            }
        }
            ActivityCompat.shouldShowRequestPermissionRationale(activity as MainActivity, Manifest.permission.CAMERA) -> {
                showPermissionDeniedDialog()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

    }

    //Methods for opening gallery and cropping image

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            isGranted: Boolean ->
        if (isGranted){
            //No action required, we have permission
        } else {
            showPermissionDeniedDialog()
            //Explain why we need the permission to the user
        }
    }



    private fun showPermissionDeniedDialog() {
        Log.d("failed", "failed")
        this.context?.let {
            AlertDialog.Builder(it)
                .setTitle("PERMISSION DENIED")
                .setMessage("A required permission is denied... Please choose to allow permissions from the App Settings")
                .setPositiveButton(
                    "App Settings"
                ) { _, _ ->
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", it.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .create().show()
        }
    }

    private val galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                imageUri = result.data?.data
                Log.d("YOO!", "$imageUri")
                Log.d("YO!", "${result.data}")
                Log.d("YOOO!", "${result.data?.data}")

                imageUri?.let {uri -> launchImageCrop(uri)
                    Log.d("GalleryPicker", "URI: $imageUri")
                }
            }
        }


    private val cameraLauncher :
            ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                Log.d("YO", "${result.resultCode}" + "${Activity.RESULT_OK}")
                imageUri = result.data?.data
                Log.d("YOOO", "$imageUri")
                Log.d("YOOO", "${result.data}")
                Log.d("YOOO", "${result.data?.data}")


                imageUri?.let {uri -> launchImageCrop(uri)
                    Log.d("CameraPicker", "URI: $imageUri")
                }
            }
        }


    //Crop-methods

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = CropImage.getActivityResult(data)
        Log.d("ImageCrop", "Result: $result")

        activity?.let {
            if (resultCode == Activity.RESULT_OK) {
                val uri: Uri? = result.uri
                val bitmap =
                    BitmapUtility.getBitmap(
                        it, null, uri.toString(), ::uriToBitmap
                    )
                val filename = uri?.lastPathSegment?.substringBefore('.')
                val file = BitmapUtility.bitmapToFile(bitmap, "$filename.png", it)
                Log.d("Image Crop", "file: $file")
                Log.d("ImageCrop", "Extension of file is ${file.extension}")

                imageUri = file.toUri()
                setImage(imageUri!!)
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e("Image Crop", "Crop error: ${result.error}" )
            }
        }
    }

    private fun launchImageCrop(imageUri: Uri){
        activity?.let {
            CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
                .start(it, this)
        }
    }

    private fun setImage(uri: Uri){
        binding.imageView.setImageURI(uri)
        binding.imageView.setBackgroundResource(R.drawable.shadow_rect)
    }
}