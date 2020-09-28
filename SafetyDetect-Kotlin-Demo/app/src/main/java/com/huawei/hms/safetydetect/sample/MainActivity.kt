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
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Safety detect fragments are launched from MainActivity
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    // Fragment Object
    private var fragmentAppsCheck: Fragment? = null
    private var fragmentSysIntegrityCheck: Fragment? = null
    private var fragmentUrlCheck: Fragment? = null
    private var fragmentUserDetectCheck: Fragment? = null
    private var fragmentWifiDetectCheck: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        txt_sysintegrity.performClick()
    }

    private fun bindViews() {
        txt_appscheck.setOnClickListener(this)
        txt_sysintegrity.setOnClickListener(this)
        txt_urlcheck.setOnClickListener(this)
        txt_userdetect.setOnClickListener(this)
        txt_wifidetect.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val fragTransaction = supportFragmentManager.beginTransaction()
        hideAllFragment(fragTransaction)
        when (v.id) {
            R.id.txt_appscheck -> {
                setSelected()
                txt_appscheck.isSelected = true
                txt_topbar.text = getString(R.string.title_activity_apps_check)
                if (fragmentAppsCheck == null) {
                    fragmentAppsCheck = SafetyDetectAppsCheckAPIFragment()
                    fragmentAppsCheck?.let { fragTransaction.add(R.id.ly_content, it) }
                } else {
                    fragmentAppsCheck?.let { fragTransaction.show(it) }
                }
            }
            R.id.txt_sysintegrity -> {
                setSelected()
                txt_sysintegrity.isSelected = true
                txt_topbar.text = getString(R.string.title_activity_sys_integrity)
                if (fragmentSysIntegrityCheck == null) {
                    fragmentSysIntegrityCheck = SafetyDetectSysIntegrityAPIFragment()
                    fragmentSysIntegrityCheck?.let { fragTransaction.add(R.id.ly_content, it) }
                } else {
                    fragmentSysIntegrityCheck?.let { fragTransaction.show(it) }
                }
            }
            R.id.txt_urlcheck -> {
                setSelected()
                txt_urlcheck.isSelected = true
                txt_topbar.text = getString(R.string.title_url_check_entry)
                if (fragmentUrlCheck == null) {
                    fragmentUrlCheck = SafetyDetectUrlCheckAPIFragment()
                    fragmentUrlCheck?.let { fragTransaction.add(R.id.ly_content, it) }
                } else {
                    fragmentUrlCheck?.let { fragTransaction.show(it) }
                }
            }
            R.id.txt_userdetect -> {
                setSelected()
                txt_userdetect.isSelected = true
                txt_topbar.text = getString(R.string.title_user_detect_entry)
                if (fragmentUserDetectCheck == null) {
                    fragmentUserDetectCheck = SafetyDetectUserDetectAPIFragment()
                    fragmentUserDetectCheck?.let { fragTransaction.add(R.id.ly_content, it) }
                } else {
                    fragmentUserDetectCheck?.let { fragTransaction.show(it) }
                }
            }
            R.id.txt_wifidetect -> {
                setSelected()
                txt_wifidetect.isSelected = true
                txt_topbar.text = getString(R.string.title_wifi_detect_entry)
                if (fragmentWifiDetectCheck == null) {
                    fragmentWifiDetectCheck = SafetyDetectWifiDetectAPIFragment()
                    fragmentWifiDetectCheck?.let { fragTransaction.add(R.id.ly_content, it) }
                } else {
                    fragmentWifiDetectCheck?.let { fragTransaction.show(it) }
                }
            }
        }
        fragTransaction.commit()
    }

    private fun setSelected() {
        txt_appscheck.isSelected = false
        txt_sysintegrity.isSelected = false
        txt_urlcheck.isSelected = false
        txt_userdetect.isSelected = false
        txt_wifidetect.isSelected = false
    }

    private fun hideAllFragment(fragmentTransaction: FragmentTransaction) {
        if (fragmentAppsCheck != null) {
            fragmentAppsCheck?.let { fragmentTransaction.hide(it) }
        }
        if (fragmentSysIntegrityCheck != null) {
            fragmentSysIntegrityCheck?.let { fragmentTransaction.hide(it) }
        }
        if (fragmentUrlCheck != null) {
            fragmentUrlCheck?.let { fragmentTransaction.hide(it) }
        }
        if (fragmentUserDetectCheck != null) {
            fragmentUserDetectCheck?.let { fragmentTransaction.hide(it) }
        }
        if (fragmentWifiDetectCheck != null) {
            fragmentWifiDetectCheck?.let { fragmentTransaction.hide(it) }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            super.onBackPressed()
        } else {
            finish()
        }
    }
}
