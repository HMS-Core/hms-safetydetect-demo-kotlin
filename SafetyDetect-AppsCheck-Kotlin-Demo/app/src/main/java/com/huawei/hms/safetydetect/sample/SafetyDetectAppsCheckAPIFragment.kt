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
import android.widget.ListAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.huawei.hms.common.ApiException
import com.huawei.hms.support.api.entity.core.CommonCode
import com.huawei.hms.support.api.entity.safetydetect.MaliciousAppsData
import com.huawei.hms.support.api.safetydetect.SafetyDetect
import com.huawei.hms.support.api.safetydetect.SafetyDetectStatusCodes
import kotlinx.android.synthetic.main.fg_appscheck.*

/**
 * An example of how to use AppsCheck Service API.
 * Note that you have to configure an AppId for SafetyDetect Service first.
 */
class SafetyDetectAppsCheckAPIFragment : Fragment() {

    companion object {
        private  val TAG = SafetyDetectAppsCheckAPIFragment::class.java.simpleName
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fg_appscheck, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fg_enable_appscheck.setOnClickListener { enableAppsCheck() }
        fg_verify_appscheck.setOnClickListener { verifyAppsCheckEnabled() }
        fg_get_malicious_apps.setOnClickListener { getMaliciousApps() }
    }

    /**
     * isVerifyAppsCheck() shows whether app verification is enabled
     */
    private fun verifyAppsCheckEnabled() {
        SafetyDetect.getClient(requireContext())
            .isVerifyAppsCheck
            .addOnSuccessListener { appsCheckResp ->

                val isVerifyAppsEnabled = appsCheckResp.result
                if (isVerifyAppsEnabled) {
                    val text = "The AppsCheck feature is enabled."
                    Toast.makeText(activity!!.applicationContext, text, Toast.LENGTH_SHORT).show()
                }

            }
            .addOnFailureListener { apiExp -> // There was an error communicating with the service.
                // There was an error communicating with the service.
                val errorMsg : String? = if (apiExp is ApiException) {
                    // An error with the HMS API contains some additional details and You can use the apiException.getStatusCode() method to get the status code.
                    val apiException = apiExp as ApiException
                    SafetyDetectStatusCodes.getStatusCodeString(apiException.statusCode) +
                            ": " + apiException.message
                } else {
                    // Unknown type of error has occurred.
                    apiExp.message
                }
                val msg = "Verify AppsCheck Enabled failed! Message: $errorMsg"
                Log.e(com.huawei.hms.safetydetect.sample.SafetyDetectAppsCheckAPIFragment.TAG, msg)
                Toast.makeText(activity!!.applicationContext, msg, Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * enableAppsCheck()  enable appsCheck
     */
    private fun enableAppsCheck() {
        SafetyDetect.getClient(activity!!.applicationContext)
                .enableAppsCheck()
                .addOnSuccessListener { appsCheckResp ->
                    val isEnableAppsCheck = appsCheckResp.result
                    if (isEnableAppsCheck) {
                        val text = "The AppsCheck feature is enabled."
                        Toast.makeText(activity!!.applicationContext, text, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e -> // There was an error communicating with the service.
                    val errorMsg: String?
                    errorMsg = if (e is ApiException) {
                        // An error with the HMS API contains some additional details.
                        val apiException = e as ApiException
                        SafetyDetectStatusCodes.getStatusCodeString(apiException.statusCode) +
                                ": " + apiException.message
                        // You can use the apiException.getStatusCode() method to get the status code.
                    } else {
                        // Unknown type of error has occurred.
                        e.message
                    }
                    val msg = "Enable AppsCheck failed! Message: $errorMsg"
                    Toast.makeText(activity!!.applicationContext, msg, Toast.LENGTH_SHORT).show()
                }
    }

    /**
     * getMaliciousAppsList()  List malicious installed apps
     */
    private fun getMaliciousApps() {
        SafetyDetect.getClient(activity)
                .maliciousAppsList
                .addOnSuccessListener { maliciousAppsListResp ->
                    val appsDataList: List<MaliciousAppsData> = maliciousAppsListResp.maliciousAppsList
                    if (maliciousAppsListResp.rtnCode == CommonCode.OK) {
                        if (appsDataList.isEmpty()) {
                            val text = "No known potentially malicious apps are installed."
                            Toast.makeText(activity!!.applicationContext, text, Toast.LENGTH_SHORT).show()
                        } else {
                            for (maliciousApp in appsDataList) {
                                Log.e(TAG, "Information about a malicious app:")
                                Log.e(TAG, "  APK: " + maliciousApp.apkPackageName)
                                Log.e(TAG, "  SHA-256: " + maliciousApp.apkSha256)
                                Log.e(TAG, "  Category: " + maliciousApp.apkCategory)
                            }
                            val maliciousAppAdapter: ListAdapter = MaliciousAppsDataListAdapter(appsDataList, requireContext())
                            fg_list_app.adapter = maliciousAppAdapter
                        }
                    } else {
                        val msg = ("Get malicious apps list failed! Message: "
                                + maliciousAppsListResp.errorReason)
                        Log.e(com.huawei.hms.safetydetect.sample.SafetyDetectAppsCheckAPIFragment.TAG, msg)
                        Toast.makeText(activity!!.applicationContext, msg, Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e -> // There was an error communicating with the service.
                    val errorMsg: String?
                    errorMsg = if (e is ApiException) {
                        // An error with the HMS API contains some additional details.
                        val apiException = e as ApiException
                        SafetyDetectStatusCodes.getStatusCodeString(apiException.statusCode) +
                                ": " + apiException.message
                        // You can use the apiException.getStatusCode() method to get the status code.
                    } else {
                        // Unknown type of error has occurred.
                        e.message
                    }
                    val msg = "Get malicious apps list failed! Message: $errorMsg"
                    Log.e(com.huawei.hms.safetydetect.sample.SafetyDetectAppsCheckAPIFragment.TAG, msg)
                    Toast.makeText(activity!!.applicationContext, msg, Toast.LENGTH_SHORT).show()
                }
    }
}