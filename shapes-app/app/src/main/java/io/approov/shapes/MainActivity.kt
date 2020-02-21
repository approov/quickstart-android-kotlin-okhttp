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

import org.json.JSONException
import org.json.JSONObject

import java.io.IOException

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class MainActivity : Activity() {
    private var activity: Activity? = null
    private var statusView: View? = null
    private var statusImageView: ImageView? = null
    private var statusTextView: TextView? = null
    private var connectivityCheckButton: Button? = null
    private var shapesCheckButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activity = this

        // find controls
        statusView = findViewById(R.id.viewStatus)
        statusImageView = findViewById<View>(R.id.imgStatus) as ImageView
        statusTextView = findViewById(R.id.txtStatus)
        connectivityCheckButton = findViewById(R.id.btnConnectionCheck)
        shapesCheckButton = findViewById(R.id.btnShapesCheck)

        // handle connection check
        connectivityCheckButton!!.setOnClickListener {
            // hide status
            activity!!.runOnUiThread { statusView!!.visibility = View.INVISIBLE }

            // make a new Request
            val request = Request.Builder()
                    .url(resources.getString(R.string.hello_url)).build()
            val client = OkHttpClient()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.d(TAG, "Connectivity call failed")
                    val imgId = R.drawable.confused
                    val msg = "Request failed: " + e.message

                    activity!!.runOnUiThread {
                        statusImageView!!.setImageResource(imgId)
                        statusTextView!!.text = msg
                        statusView!!.visibility = View.VISIBLE
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val imgId: Int
                    val msg = "Http status code " + response.code
                    if (response.isSuccessful) {
                        Log.d(TAG, "Connectivity call successful")
                        imgId = R.drawable.hello
                    } else {
                        Log.d(TAG, "Connectivity call unsuccessful")
                        imgId = R.drawable.confused
                    }

                    activity!!.runOnUiThread {
                        statusImageView!!.setImageResource(imgId)
                        statusTextView!!.text = msg
                        statusView!!.visibility = View.VISIBLE
                    }
                }
            })
        }

        // handle getting shapes
        shapesCheckButton!!.setOnClickListener {
            // hide status
            activity!!.runOnUiThread { statusView!!.visibility = View.INVISIBLE }

            // *** COMMENT THE LINE BELOW FOR APPROOV ***
            val client = OkHttpClient()

            // *** UNCOMMENT THE LINE BELOW FOR APPROOV ***
            //val client = ShapesApp.approovService!!.getOkHttpClient();

            // create a new request
            val url = resources.getString(R.string.shapes_url)
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.d(TAG, "Shapes call failed")
                    val imgId = R.drawable.confused
                    val msg = "Request failed: " + e.message
                    activity!!.runOnUiThread {
                        statusImageView!!.setImageResource(imgId)
                        statusTextView!!.text = msg
                        statusView!!.visibility = View.VISIBLE
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
                    activity!!.runOnUiThread {
                        statusImageView!!.setImageResource(finalImgId)
                        statusTextView!!.text = finalMsg
                        statusView!!.visibility = View.VISIBLE
                    }
                }
            })
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
