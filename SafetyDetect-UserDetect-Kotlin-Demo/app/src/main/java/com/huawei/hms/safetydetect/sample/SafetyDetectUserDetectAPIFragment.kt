/*
* Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

* Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/

package com.huawei.hms.safetydetect.sample

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes
import kotlinx.android.synthetic.main.fg_userdetect.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * An example of how to use UserDetect Service API.
 *
 * @since 5.0.2.300
 */
class SafetyDetectUserDetectAPIFragment : Fragment(),
    View.OnClickListener {

    companion object {
        val TAG: String =
            SafetyDetectUserDetectAPIFragment::class.java.simpleName
        //TODO(developer):replace the APP_ID id with your own app id
        private const val APP_ID = "******"

        /**
         * Send responseToken to your server to get the result of user detect.
         */
        private inline fun verify(
            responseToken: String,
            crossinline handleVerify: (Boolean) -> Unit
        ) {
            var isTokenVerified = false
            val inputResponseToken: String = responseToken
            val isTokenResponseVerified = GlobalScope.async {
                val jsonObject = JSONObject()
                try {
                    // TODO(developer): Replace the baseUrl with your own server address,better not hard code.
                    val baseUrl =
                        "https://www.example.com/userdetect/verify"
                    val put = jsonObject.put("response", inputResponseToken)
                    val result: String? = sendPost(baseUrl, put)
                    result?.let {
                        val resultJson = JSONObject(result)
                        isTokenVerified = resultJson.getBoolean("success")
                        // if success is true that means the user is real human instead of a robot.
                        Log.i(TAG, "verify: result = $isTokenVerified")
                    }
                    return@async isTokenVerified
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@async false
                }
            }
            GlobalScope.launch(Dispatchers.Main) {
                isTokenVerified = isTokenResponseVerified.await()
                handleVerify(isTokenVerified)
            }
        }

        /**
         * post the response token to yur own server.
         */
        @Throws(Exception::class)
        private fun sendPost(baseUrl: String, postDataParams: JSONObject): String? {
            val url = URL(baseUrl)
            val conn =
                url.openConnection() as HttpURLConnection
            val responseCode = conn.run {
                readTimeout = 20000
                connectTimeout = 20000
                requestMethod = "POST"
                doInput = true
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                outputStream.use { os ->
                    BufferedWriter(
                        OutputStreamWriter(
                            os,
                            StandardCharsets.UTF_8
                        )
                    ).use {
                        it.write(postDataParams.toString())
                        it.flush()
                    }
                }
                responseCode
            }

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                val stringBuffer = StringBuffer()
                lateinit var line: String
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuffer.append(line)
                    break
                }
                bufferedReader.close()
                return stringBuffer.toString()
            }
            return null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //init user detect
        SafetyDetect.getClient(activity).initUserDetect()
        return inflater.inflate(R.layout.fg_userdetect, container, false)
    }

    override fun onDestroyView() {
        //shut down user detect
        SafetyDetect.getClient(activity).shutdownUserDetect()
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fg_userdetect_btn.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.fg_userdetect_btn) {
            processView()
            detect()
        }
    }

    private fun detect() {
        Log.i(TAG, "User detection start.")
        SafetyDetect.getClient(activity)
            .userDetection(APP_ID)
            .addOnSuccessListener {
                /**
                 * Called after successfully communicating with the SafetyDetect API.
                 * The #onSuccess callback receives an
                 * [com.huawei.hms.support.api.entity.safetydetect.UserDetectResponse] that contains a
                 * responseToken that can be used to get user detect result.
                 */
                // Indicates communication with the service was successful.
                Log.i(TAG, "User detection succeed, response = $it")
                verify(it.responseToken) { verifySucceed ->
                    activity?.applicationContext?.let { context ->
                        if (verifySucceed) {
                            Toast.makeText(
                                context,
                                "User detection succeed and verify succeed",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        } else {
                            Toast.makeText(
                                context, "User detection succeed but verify fail,"
                                        + "please replace verify url with your's server address",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }

                    fg_userdetect_btn.setBackgroundResource(R.drawable.btn_round_normal)
                    fg_userdetect_btn.text = getString(R.string.rerun)
                }

            }
            .addOnFailureListener {  // There was an error communicating with the service.
                val errorMsg: String? = if (it is ApiException) {
                    // An error with the HMS API contains some additional details.
                    "${SafetyDetectStatusCodes.getStatusCodeString(it.statusCode)}: ${it.message}"
                    // You can use the apiException.getStatusCode() method to get the status code.
                } else {
                    // Unknown type of error has occurred.
                    it.message
                }
                Log.i(TAG, "User detection fail. Error info: $errorMsg")
                activity?.applicationContext?.let { context ->
                    Toast.makeText(
                        context,
                        errorMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                fg_userdetect_btn.setBackgroundResource(R.drawable.btn_round_yellow)
                fg_userdetect_btn.text = getString(R.string.rerun)
            }
    }

    private fun processView() {
        fg_userdetect_btn.text = getString(R.string.processing)
        fg_userdetect_btn.setBackgroundResource(R.drawable.btn_round_processing)
    }

}