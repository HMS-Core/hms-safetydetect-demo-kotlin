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

class MainActivity : AppCompatActivity(), View.OnClickListener {

    // Fragment Object
    private var fg: Fragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        txt_userdetect.performClick()
    }

    private fun bindViews() {
        txt_userdetect.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val fTransaction = supportFragmentManager.beginTransaction()
        hideAllFragment(fTransaction)
        txt_topbar.setText(R.string.title_activity_user_detect)
        if (fg == null) {
            fg = SafetyDetectUserDetectAPIFragment()
            fg?.let{
                fTransaction.add(R.id.ly_content, it)
            }
        } else {
            fg?.let{
                fTransaction.show(it)
            }
        }
        fTransaction.commit()
    }

    private fun hideAllFragment(fragmentTransaction: FragmentTransaction) {
        fg?.let {
            fragmentTransaction.hide(it)
        }
    }
}