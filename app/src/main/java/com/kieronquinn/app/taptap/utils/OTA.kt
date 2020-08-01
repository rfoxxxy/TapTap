package com.kieronquinn.app.taptap.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.text.Html
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.kieronquinn.app.taptap.R
import com.kieronquinn.app.taptap.fragments.bottomsheets.AlertBottomSheetFragment
import com.kieronquinn.app.taptap.fragments.bottomsheets.GenericBottomSheetFragment
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.net.URL

class OTA {
    companion object {
        const val GITHUB_RELEASES = "https://api.github.com/repos/rfoxxxy/TapTap/releases/latest"
        const val APK_LINK = "https://github.com/rfoxxxy/TapTap/releases/download/\$code\$/app-release.apk"
        private var downloadManager: DownloadManager? = null

        private fun parse(json: String, context: Context): JSONObject? {
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(json)
            } catch (e: JSONException) {
                e.printStackTrace()
                Toast.makeText(context, "An error occured: " + e.localizedMessage, Toast.LENGTH_LONG)
            }
            return jsonObject
        }

        private fun downloadUpdate(context: Context, url: String, versionCode: String) {
            downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle("Tap, Tap update")
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                "taptap-build$versionCode.apk"
            )
            request.setAllowedOverRoaming(false)
            request.setMimeType("application/vnd.android.package-archive")
            val id = downloadManager?.enqueue(request) ?: -1L
            if (id == -1L) return
        }

        private fun checkUpdates(version: String, context: Context, manager: FragmentManager, showUpToDate: Boolean = true){
            val resp = URL(GITHUB_RELEASES).readText()
            val jsonObj = parse(resp, context)
            if (jsonObj == null) {
                if (showUpToDate) {
                    GenericBottomSheetFragment.create(context.getString(R.string.no_updates), R.string.ota_simple_title, android.R.string.ok)
                        .show(manager, "bs_ota")
                }
                return
            } else {
                if (jsonObj.getString("tag_name") == version) {
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
                        {downloadUpdate(context, APK_LINK.replace("\$code\$", jsonObj.getString("tag_name")), jsonObj.getString("tag_name")); true},
                        {true}
                    ).show(manager, "bs_ota")
                }
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