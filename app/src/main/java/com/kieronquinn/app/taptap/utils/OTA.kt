package com.kieronquinn.app.taptap.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.text.Html
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import com.kieronquinn.app.taptap.BuildConfig
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.fragments.bottomsheets.AlertBottomSheetFragment
import com.kieronquinn.app.taptap.fragments.bottomsheets.GenericBottomSheetFragment
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.net.URL
import android.Manifest
import android.content.pm.PackageManager

class OTA {
    companion object {
        const val PERMISSION_REQUEST_STORAGE = 0
        private var downloadManager: DownloadManager? = null

        private fun parse(json: String, context: Context): JSONObject? {
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(json)
            } catch (e: JSONException) {
                Log.d("TapTap", e.stackTrace.toString())
                Links.startLinkIntent(context, Links.APK_REPO + "/releases")
            }
            return jsonObject
        }

        private fun downloadUpdate(context: Context, url: String, versionCode: String, version: String) {
            downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            var destination =
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"
            destination += "taptap-build$versionCode.apk"
            val uri = Uri.parse("file://$destination")
            val file = File(destination)
            if (file.exists()) file.delete()
            val request = DownloadManager.Request(Uri.parse(url))
            // Log.d("TapTap", uri.toString())
            // Log.d("TapTap", destination.toString())
            request.setTitle("Tap, Tap " + version)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setDestinationUri(uri)
            request.setAllowedOverRoaming(false)
            request.setMimeType("application/vnd.android.package-archive")
            request.setDescription(context.getString(R.string.downloading_description))
            showInstallOption(destination, uri, context)
            downloadManager?.enqueue(request)
        }

        private fun checkUpdates(version: String, context: Context, manager: FragmentManager, showUpToDate: Boolean = true){
            val resp = URL(Links.GITHUB_RELEASES).readText()
            val jsonObj = parse(resp, context)
            if (jsonObj == null) {
                if (showUpToDate) {
                    GenericBottomSheetFragment.create(context.getString(R.string.no_updates), R.string.ota_simple_title, android.R.string.ok)
                        .show(manager, "bs_ota")
                }
                return
            } else {
                if (jsonObj.getString("tag_name") == version && BuildConfig.BUILD_TYPE != "debug") {
                    if (showUpToDate) {
                        GenericBottomSheetFragment.create(context.getString(R.string.no_updates), R.string.ota_simple_title, android.R.string.ok)
                            .show(manager, "bs_ota")
                    }
                    return
                } else {
                    AlertBottomSheetFragment.create(
                        Html.fromHtml("<p><b>${jsonObj.getString("name")}</b></p>${jsonObj.getString("body").replace("\r", "").replace("\n", "<br>")}", Html.FROM_HTML_MODE_LEGACY),
                        R.string.ota_title,
                        R.string.ota_update,
                        R.string.ota_later,
                        {
                            val hasPermissionToInstall = checkStoragePermission(context);
                            if (!hasPermissionToInstall) {
                                requestStoragePermission(context, manager)
                            }
                            downloadUpdate(context, Links.APK_LINK.replace("\$code\$", jsonObj.getString("tag_name")), jsonObj.getString("tag_name"), jsonObj.getString("name")); true
                        },
                        {true}
                    ).show(manager, "bs_ota")
                }
            }
        }

        private fun showInstallOption(
            destination: String,
            uri: Uri,
            context: Context
        ) {
            // set BroadcastReceiver to install app when .apk is downloaded
            val onComplete = object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context,
                    intent: Intent
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        val contentUri = FileProvider.getUriForFile(
                            context,
                            BuildConfig.APPLICATION_ID + ".provider",
                            File(destination)
                        )
                        // Log.d("TapTap", contentUri.toString())
                        // Log.d("TapTap", destination)
                        // Log.d("TapTap", File(destination).length().toString())
                        val install = Intent(Intent.ACTION_VIEW)
                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                        install.data = contentUri
                        context.startActivity(install)
                        context.unregisterReceiver(this)
                        // finish()
                    } else {
                        val install = Intent(Intent.ACTION_VIEW)
                        install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        install.setDataAndType(
                            uri,
                            "\"application/vnd.android.package-archive\""
                        )
                        context.startActivity(install)
                        context.unregisterReceiver(this)
                        // finish()
                    }
                }
            }
            context.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }

        fun checkSelfPermissionCompat(permission: String, context: Context) =
            ActivityCompat.checkSelfPermission(context, permission)
        fun shouldShowRequestPermissionRationaleCompat(permission: String, context: Context) =
            ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permission)
        fun requestPermissionsCompat(
            permissionsArray: Array<String>,
            requestCode: Int,
            context: Context
        ) {
            ActivityCompat.requestPermissions(context as Activity, permissionsArray, requestCode)
        }

        private fun checkStoragePermission(context: Context): Boolean {
            // Check if the storage permission has been granted
            return checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE, context) ==
                    PackageManager.PERMISSION_GRANTED
        }

        private fun requestStoragePermission(context: Context, manager: FragmentManager) {
            if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE, context)) {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_STORAGE,
                    context
                )
            } else {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_STORAGE,
                    context
                )
            }
        }

        fun runChecking(context: Context, version: String, manager: FragmentManager, showUpToDate: Boolean = true) {
            try {
                return checkUpdates(version, context, manager, showUpToDate)
            } catch (e: Exception) {
                return
            }
        }
    }
}