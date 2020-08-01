package com.kieronquinn.app.taptap.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.preference.PreferenceScreen
import com.kieronquinn.app.taptap.preferences.Preference

class Links {
    companion object {
        const val LINK_GITHUB = "https://kieronquinn.co.uk/redirect/TapTap/github"
        const val LINK_XDA = "https://kieronquinn.co.uk/redirect/TapTap/xda"
        const val LINK_DONATE = "https://kieronquinn.co.uk/redirect/TapTap/donate"
        const val LINK_TWITTER = "https://kieronquinn.co.uk/redirect/TapTap/twitter"
        const val FORK_GITHUB = "https://github.com/rfoxxxy/TapTap"
        const val APK_REPO = FORK_GITHUB
        const val GITHUB_RELEASES = "https://api.github.com/repos/rfoxxxy/TapTap/releases/latest"
        const val APK_LINK = "https://github.com/rfoxxxy/TapTap/releases/download/\$code\$/app-release.apk"

        fun startLinkIntent(context: Context, link: String){
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            context.startActivity(intent)
        }

        fun setupPreference(context: Context, preferenceScreen: PreferenceScreen, preferenceKey: String, link: String){
            val preference = preferenceScreen.findPreference<Preference>(preferenceKey)
            preference?.onPreferenceClickListener = androidx.preference.Preference.OnPreferenceClickListener {
                startLinkIntent(context, link)
                true
            }
        }
    }
}