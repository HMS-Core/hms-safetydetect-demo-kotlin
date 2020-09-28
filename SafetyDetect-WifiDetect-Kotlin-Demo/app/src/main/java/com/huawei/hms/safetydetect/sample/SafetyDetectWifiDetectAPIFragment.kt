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

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes
import kotlinx.android.synthetic.main.fg_wifidetect.*

/**
 * An example of how to use WifiDetect Service API.
 * Note that you have to configure an AppId for SafetyDetect Service first.
 *
 * @since 4.0.3.300
 */
class SafetyDetectWifiDetectAPIFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_wifidetect, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fg_get_wifidetect_status.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.fg_get_wifidetect_status) {
            wifiDetectStatus
        }
    }

    /**
     * getWifiDetectStatus()  Get Wifi Status.
     */
    private val wifiDetectStatus: Unit
        private get() {
            SafetyDetect.getClient(activity)
                    .wifiDetectStatus
                    .addOnSuccessListener { wifiDetectResponse ->
                        val wifiDetectStatus = wifiDetectResponse.wifiDetectStatus
                        val wifiDetectView = "WifiDetect status: $wifiDetectStatus"
                        fg_wifidetecttextView.text = wifiDetectView
                    }
                    .addOnFailureListener { e -> // There was an error communicating with the service.
                        val errorMsg: String?
                        errorMsg = if (e is ApiException) {
                            // An error with the HMS API contains some additional details.
                            val apiException = e
                            (SafetyDetectStatusCodes.getStatusCodeString(apiException.statusCode) + ": "
                                    + apiException.message)
                            // You can use the apiException.getStatusCode() method to get the status code.
                        } else {
                            // Unknown type of error has occurred.
                            e.message
                        }
                        val msg = "Get wifiDetect status failed! Message: $errorMsg"
                        Log.e(TAG, msg)
                        fg_wifidetecttextView.text = msg
                    }
        }

    companion object {
        val TAG = SafetyDetectWifiDetectAPIFragment::class.java.simpleName
    }
}