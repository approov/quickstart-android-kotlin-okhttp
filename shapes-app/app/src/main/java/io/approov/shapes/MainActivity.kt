// Main activity for Approov Shapes App Demo (using OkHttp)
//
// MIT License
//
// Copyright (c) 2016-present, Critical Blue Ltd.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
// (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge,
// publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
// ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
// THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package io.approov.shapes

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity: Activity() {
    private lateinit var activity: Activity
    private lateinit var statusView: View
    private lateinit var statusImageView: ImageView
    private lateinit var statusTextView: TextView
    private lateinit var helloCheckButton: Button
    private lateinit var shapesCheckButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this

        // find controls
        statusView = findViewById(R.id.viewStatus)
        statusImageView = findViewById<View>(R.id.imgStatus) as ImageView
        statusTextView = findViewById(R.id.txtStatus)
        helloCheckButton = findViewById(R.id.btnConnectionCheck)
        shapesCheckButton = findViewById(R.id.btnShapesCheck)

        // handle the hello connection check
        helloCheckButton.setOnClickListener {
            // hide status
            activity.runOnUiThread { statusView.visibility = View.INVISIBLE }

            // make a new Request
            val request = Request.Builder()
                    .url(resources.getString(R.string.hello_url)).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "Hello call failed: " + e.message)
                    val imgId = R.drawable.confused
                    val msg = "Request failed: " + e.message
                    activity.runOnUiThread {
                        statusImageView.setImageResource(imgId)
                        statusTextView.text = msg
                        statusView.visibility = View.VISIBLE
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val imgId: Int
                    val msg = "Http status code " + response.code
                    if (response.isSuccessful) {
                        Log.d(TAG, "Hello call successful")
                        imgId = R.drawable.hello
                    } else {
                        Log.d(TAG, "Hello call unsuccessful")
                        imgId = R.drawable.confused
                    }

                    activity.runOnUiThread {
                        statusImageView.setImageResource(imgId)
                        statusTextView.text = msg
                        statusView.visibility = View.VISIBLE
                    }
                }
            })
        }

        // handle getting shapes
        shapesCheckButton.setOnClickListener {
            // hide status
            activity.runOnUiThread { statusView.visibility = View.INVISIBLE }

            // *** COMMENT THE LINE BELOW FOR APPROOV ***
            val client = OkHttpClient()

            // *** UNCOMMENT THE LINE BELOW FOR APPROOV USING SECURE PROTECTION ***
            //ShapesApp.approovService.addSubstitutionHeader("Api-Key", null)

            // *** UNCOMMENT THE LINE BELOW FOR APPROOV ***
            //val client = ShapesApp.approovService.getOkHttpClient();

            // create a new request
            val url = resources.getString(R.string.shapes_url)
            val apiKey = resources.getString(R.string.shapes_api_key)
            val request = Request.Builder().addHeader("Api-Key", apiKey).url(url).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d(TAG, "Shapes call failed: " + e.message)
                    val imgId = R.drawable.confused
                    val msg = "Request failed: " + e.message
                    activity.runOnUiThread {
                        statusImageView.setImageResource(imgId)
                        statusTextView.text = msg
                        statusView.visibility = View.VISIBLE
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    var imgId = R.drawable.confused
                    var msg = "Http status code " + response.code
                    if (response.isSuccessful) {
                        Log.d(TAG, "Shapes call successful")
                        var shapeJSON: JSONObject? = null
                        try {
                            shapeJSON = JSONObject(response.body?.string())
                        } catch (e: JSONException) {
                            msg = "Invalid JSON: $e"
                        }

                        if (shapeJSON != null) {
                            try {
                                val shape = shapeJSON.getString("shape")
                                if (shape != null) {
                                    if (shape.equals("square", ignoreCase = true)) {
                                        imgId = R.drawable.square
                                    } else if (shape.equals("circle", ignoreCase = true)) {
                                        imgId = R.drawable.circle
                                    } else if (shape.equals("rectangle", ignoreCase = true)) {
                                        imgId = R.drawable.rectangle
                                    } else if (shape.equals("triangle", ignoreCase = true)) {
                                        imgId = R.drawable.triangle
                                    }
                                }
                            } catch (e: JSONException) {
                                msg = "JSONException: $e"
                            }

                        }
                    } else {
                        Log.d(TAG, "Shapes call unsuccessful")
                    }

                    val finalImgId = imgId
                    val finalMsg = msg
                    activity.runOnUiThread {
                        statusImageView.setImageResource(finalImgId)
                        statusTextView.text = finalMsg
                        statusView.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
