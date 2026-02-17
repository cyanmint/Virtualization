/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.virtualization.terminal

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class SettingsVmProtectionActivity : AppCompatActivity() {

    private lateinit var sharedPref: SharedPreferences
    private lateinit var vmProtectionSwitch: Switch
    private lateinit var vmProtectionDesc: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_vm_protection)

        val toolbar: MaterialToolbar = findViewById(R.id.settings_vm_protection_toolbar)
        setSupportActionBar(toolbar)

        sharedPref = getSharedPreferences(VmPreferences.PREFS_NAME, Context.MODE_PRIVATE)
        
        vmProtectionSwitch = findViewById(R.id.vm_protection_switch)
        vmProtectionDesc = findViewById(R.id.vm_protection_description)

        // Load current setting
        val isProtected = sharedPref.getBoolean(VmPreferences.KEY_VM_PROTECTED, DEFAULT_VM_PROTECTED)
        vmProtectionSwitch.isChecked = isProtected
        updateDescription(isProtected)

        // Set up switch listener
        vmProtectionSwitch.setOnCheckedChangeListener { _, isChecked ->
            with(sharedPref.edit()) {
                putBoolean(VmPreferences.KEY_VM_PROTECTED, isChecked)
                apply()
            }
            updateDescription(isChecked)
            Toast.makeText(
                this,
                resources.getString(R.string.settings_vm_protection_restart_message),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateDescription(isProtected: Boolean) {
        vmProtectionDesc.text = if (isProtected) {
            resources.getString(R.string.settings_vm_protection_enabled_desc)
        } else {
            resources.getString(R.string.settings_vm_protection_disabled_desc)
        }
    }

    companion object {
        const val DEFAULT_VM_PROTECTED = true
    }
}
