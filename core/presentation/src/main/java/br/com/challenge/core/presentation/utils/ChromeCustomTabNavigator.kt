package br.com.challenge.core.presentation.utils

import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri

class ChromeCustomTabNavigator {

    fun openChromeCustomTab(
        context: android.content.Context,
        url: String,
        toolbarColor: Color
    ) {
        val builder = CustomTabsIntent.Builder().apply {
            setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder().setToolbarColor(toolbarColor.toArgb()).build())
            setStartAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            setExitAnimations(context, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, url.toUri())
    }
}