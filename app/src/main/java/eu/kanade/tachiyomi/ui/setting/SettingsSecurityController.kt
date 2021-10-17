package eu.kanade.tachiyomi.ui.setting

import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceScreen
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.preference.PreferenceKeys
import eu.kanade.tachiyomi.data.preference.asImmediateFlowIn
import eu.kanade.tachiyomi.ui.security.SecureActivityDelegate
import eu.kanade.tachiyomi.util.system.AuthenticatorUtil.isAuthenticationSupported

class SettingsSecurityController : SettingsController() {
    override fun setupPreferenceScreen(screen: PreferenceScreen) = screen.apply {
        titleRes = R.string.security

        if (context.isAuthenticationSupported()) {
            switchPreference {
                key = PreferenceKeys.useBiometrics
                titleRes = R.string.lock_with_biometrics
                defaultValue = false

                requireAuthentication(
                    activity as? FragmentActivity,
                    activity!!.getString(R.string.lock_with_biometrics)
                )
            }
            intListPreference(activity) {
                key = PreferenceKeys.lockAfter
                titleRes = R.string.lock_when_idle
                val values = listOf(0, 2, 5, 10, 20, 30, 60, 90, 120, -1)
                entries = values.mapNotNull {
                    when (it) {
                        0 -> context.getString(R.string.always)
                        -1 -> context.getString(R.string.never)
                        else -> resources?.getQuantityString(
                            R.plurals.after_minutes,
                            it,
                            it
                        )
                    }
                }
                entryValues = values
                defaultValue = 0

                preferences.useBiometrics().asImmediateFlowIn(viewScope) { isVisible = it }
            }
        }

        switchPreference {
            key = PreferenceKeys.secureScreen
            titleRes = R.string.secure_screen
            summaryRes = R.string.hide_app_block_screenshots
            defaultValue = false

            onChange {
                it as Boolean
                SecureActivityDelegate.setSecure(activity, it)
                true
            }
        }
        switchPreference {
            key = PreferenceKeys.hideNotificationContent
            titleRes = R.string.hide_notification_content
            defaultValue = false
        }
    }
}
