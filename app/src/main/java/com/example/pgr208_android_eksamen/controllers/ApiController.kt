package com.example.pgr208_android_eksamen.controllers

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.example.pgr208_android_eksamen.models.ImageApiResponse
import org.json.JSONArray
import java.io.File

object ApiController {
    private val TAG = "ApiController"

    enum class Endpoint(val path: String) {
        PROVIDER_GOOGLE("/google"),
        PROVIDER_BING("/bing"),
        PROVIDER_TINEYE("/tineye")
    }

    private const val serverUrl: String = "http://api-edu.gtl.ai/api/v1/imagesearch"

    /**
     * Method to perform a reverse image search on the server
     *
     * @param endpoint Which provider to use for the search
     * @param imageUrl The url to the (uploaded) image used as input for the search
     * @param callback Optional callback on completion of request (with either a list of the responses, or an error)
     */
    fun reverseSearch(endpoint: Endpoint, imageUrl: String, callback: ((result: List<ImageApiResponse>?, error: ANError?) -> Unit)? = null) {

        AndroidNetworking.get("$serverUrl{endpoint}")
            .addPathParameter("endpoint", endpoint.path)
            .addQueryParameter("url", imageUrl)
            .setPriority(Priority.LOW)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(jsonArray: JSONArray) {
                    val list = ArrayList<ImageApiResponse>()
                    (0 until jsonArray.length()).forEach {
                        val item = jsonArray.getJSONObject(it)
                        list.add(ImageApiResponse(item.getString("image_link"), item.getString("thumbnail_link")))
                    }
                    Log.d(TAG, "Result from reverse search: $list")
                    callback?.let { it(list, null) }
                }

                override fun onError(error: ANError?) {
                    callback?.let{
                        it(null, error)
                    }
                    Log.e(TAG, "Error on GET request: $error")
                }
            })
    }

    /**
     * Method to perform a reverse image search on the server
     *
     * @param png The png file to upload
     * @param callback Optional callback with either the url of the uploaded image, or an error
     */
    fun uploadImage(png: File, callback: ((uploadUrl: String?, error: ANError?) -> Unit)? = null) {
        when(png.extension){
            "png" ->{
                Log.d(TAG, "Attempting upload of file ${png.name} to $serverUrl/upload")
                AndroidNetworking.upload("$serverUrl/upload")
                    .addMultipartFile("image", png)
                    .addHeaders("Content-Type", "image/png")
                    .setTag("uploadTest")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsString(object : StringRequestListener {
                        @RequiresApi(Build.VERSION_CODES.N)
                        override fun onResponse(response: String?) {
                            Log.d(TAG, "File uploaded")
                            Log.d(TAG, "Response: $response")
                            callback?.let { callback ->
                                response?.let { res -> callback(res, null) }
                            }
                        }

                        override fun onError(error: ANError) {
                            callback?.let { callback -> callback(null, error) }
                            Log.e(TAG, "Failed to upload image: $error")
                        }
                    })
            }
            else -> callback?.let { it(null, ANError("Server can only handle upload of png-files!")) }
        }
    }
}