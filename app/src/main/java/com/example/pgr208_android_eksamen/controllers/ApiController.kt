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
        PROVIDER_BING("/bing?q=furniture"),
        PROVIDER_GOOGLE("/google?url=https://www.furniture.png"),
        PROVIDER_TINEYE("/tineye")
    }

    private const val serverUrl: String = "http://api-edu.gtl.ai/api/v1/imagesearch"

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
                    Log.d(TAG, "Reverse search result: $list")
                    callback?.let { it(list, null) }
                }

                override fun onError(error: ANError?) {
                    callback?.let{
                        it(null, error)
                    }
                    Log.e(TAG, "Error-GET request: $error")
                }
            })
    }

    fun uploadImage(png: File, callback: ((uploadUrl: String?, error: ANError?) -> Unit)? = null) {
        when(png.extension){
            "png" ->{
                Log.d(TAG, "Search file ${png.name} to $serverUrl/upload")
                AndroidNetworking.upload("$serverUrl/upload")
                    .addMultipartFile("image", png)
                    .addHeaders("Content-Type", "image/png")
                    .setTag("testSearch")
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
                            Log.e(TAG, "Failed to search for image: $error")
                        }
                    })
            }
            else -> callback?.let { it(null, ANError("Can only search with png-files!")) }
        }
    }
}
