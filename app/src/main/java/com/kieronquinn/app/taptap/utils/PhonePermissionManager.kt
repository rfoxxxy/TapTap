package com.kieronquinn.app.taptap.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.kieronquinn.app.taptap.R

class PhonePermissionManager {
    companion object {
        const val PERMISSION_REQUEST_PHONE = 0

        private fun checkSelfPermissionCompat(permission: String, context: Context) =
            ActivityCompat.checkSelfPermission(context, permission)
        private fun shouldShowRequestPermissionRationaleCompat(permission: String, context: Context) =
            ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)
        private fun requestPermissionsCompat(
            permissionsArray: Array<String>,
            requestCode: Int,
            context: Context
        ) {
            ActivityCompat.requestPermissions(context as Activity, permissionsArray, requestCode)
        }

        private fun checkPhonePermission(context: Context): Boolean {
            return checkSelfPermissionCompat(Manifest.permission.CALL_PHONE, context) ==
                    PackageManager.PERMISSION_GRANTED
        }

        private fun requestPhonePermission(context: Context) {
            if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.CALL_PHONE, context)) {
                Toast.makeText(context, context.getString(R.string.call_phone_permission_toast), Toast.LENGTH_LONG).show()
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.CALL_PHONE),
                    PERMISSION_REQUEST_PHONE,
                    context
                )
            } else {
                Toast.makeText(context, context.getString(R.string.call_phone_permission_toast), Toast.LENGTH_LONG).show()
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.CALL_PHONE),
                    PERMISSION_REQUEST_PHONE,
                    context
                )
            }
        }

        fun check(context: Context) {
            val hasPermission = checkPhonePermission(context)
            if (!hasPermission) {
                requestPhonePermission(context)
            }
        }

    }
}