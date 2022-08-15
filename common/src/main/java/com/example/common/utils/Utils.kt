package com.example.common.utils

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.util.DisplayMetrics
import android.util.Size
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.BindingAdapter
import com.example.common.R
import com.example.common.SHARED_PREF_FILE_PERMISSION_SEEN
import com.example.common.SHARED_PREF_NAME
import java.util.*
import kotlin.math.roundToInt


fun convertPxToDp(context: Context, px: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        px,
        context.resources.displayMetrics
    )
}

fun gatherDeviceWidth(context: Context): Float {
    return context.resources.displayMetrics.xdpi
}

fun gatherDeviceHeight(context: Context): Float {
    return context.resources.displayMetrics.ydpi
}

object ScreenSizeCompat {
    private val api: Api =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ApiLevel30()
        else Api()

    /**
     * Returns screen size in pixels.
     */
    fun getScreenSize(context: Context): Size = api.getScreenSize(context)

    @Suppress("DEPRECATION")
    private open class Api {
        open fun getScreenSize(context: Context): Size {
            val display = context.getSystemService(WindowManager::class.java).defaultDisplay
            val metrics = if (display != null) {
                DisplayMetrics().also { display.getRealMetrics(it) }
            } else {
                Resources.getSystem().displayMetrics
            }
            return Size(metrics.widthPixels, metrics.heightPixels)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private class ApiLevel30 : Api() {
        override fun getScreenSize(context: Context): Size {
            val metrics: WindowMetrics =
                context.getSystemService(WindowManager::class.java).currentWindowMetrics
            return Size(metrics.bounds.width(), metrics.bounds.height())
        }
    }
}

fun convertDpToPx(context: Context, dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )
}

/**
 * This class will used for shared preferences.
 */
class SharedPreference(val context: Context) {
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    fun saveString(KEY_NAME: String, text: String?) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME, text)
        editor.apply()
    }

    @SuppressLint("ApplySharedPref")
    fun saveStringSynchronized(KEY_NAME: String, text: String?) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME, text)
        editor.commit()
    }

    fun saveInt(KEY_NAME: String, value: Int) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putInt(KEY_NAME, value)
        editor.apply()
    }

    fun saveBoolean(KEY_NAME: String, status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_NAME, status)
        editor.apply()
    }

    @SuppressLint("ApplySharedPref")
    fun saveBooleanSynchronized(KEY_NAME: String, status: Boolean) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putBoolean(KEY_NAME, status)
        editor.commit()
    }

    fun getValueString(KEY_NAME: String): String? {
        return sharedPref.getString(KEY_NAME, null)
    }

    fun getValueInt(KEY_NAME: String): Int {
        return sharedPref.getInt(KEY_NAME, 0)
    }

    fun getValueBoolean(KEY_NAME: String, defaultValue: Boolean): Boolean {
        return sharedPref.getBoolean(KEY_NAME, defaultValue)
    }

    fun clearSharedPreference() {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun removeValue(KEY_NAME: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.remove(KEY_NAME)
        editor.apply()
    }

}

@BindingAdapter("android:throttleClick")
fun View.clickWithThrottle(action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        private val throttleTime = 1250L

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < throttleTime) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

@BindingAdapter("android:doubleTap")
fun View.clickWithDoubleTap(action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        private val throttleTime = 750L

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < throttleTime) {
                action()
            } else {
                lastClickTime = SystemClock.elapsedRealtime()
                return
            }


        }
    })

}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

object FilePermissionHelper {
    private const val WRITE_EXTERNAL_STORAGE_PERMISSION = WRITE_EXTERNAL_STORAGE
    private const val READ_EXTERNAL_STORAGE_PERMISSION = READ_EXTERNAL_STORAGE

    private fun hasFilePermissionFragment(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            WRITE_EXTERNAL_STORAGE_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            activity,
            READ_EXTERNAL_STORAGE_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestFilePermission(
        activity: Activity,
        intent: Intent,
        resultLauncherSettings: ActivityResultLauncher<Intent>,
        resultLauncher: ActivityResultLauncher<Array<String>>
    ): Boolean {
        val sharedPreference = SharedPreference(activity)
        if (!hasFilePermissionFragment(activity) && !shouldShowRequestPermissionRationaleFragmentFilePermission(
                activity
            ) && sharedPreference.getValueBoolean(
                SHARED_PREF_FILE_PERMISSION_SEEN, false
            )
        ) {
            val permissionList: MutableList<String> = mutableListOf()
            if (ContextCompat.checkSelfPermission(
                    activity,
                    WRITE_EXTERNAL_STORAGE_PERMISSION
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    activity,
                    READ_EXTERNAL_STORAGE_PERMISSION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionList.add(activity.resources.getString(R.string.error_permission_system_storage))
            }
            var permissionErrorMessage = ""
            for (index in 0 until permissionList.size) {
                permissionErrorMessage += if (index != 0) {
                    " " + "," + " " + permissionList[index]
                } else {
                    permissionList[index]
                }
            }
            activity.toast(
                activity.resources.getString(
                    R.string.error_permission_system_message,
                    permissionErrorMessage
                )
            )
            launchPermissionSettingsFilePermissionWithResultLauncher(
                intent,
                resultLauncherSettings
            )
            return false
        } else if (!hasFilePermissionFragment(activity)) {
            resultLauncher.launch(listOfFilePermission())
            return false
        }

        return true

    }

    private fun listOfFilePermission(): Array<String> {
        return arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
    }


    private fun shouldShowRequestPermissionRationaleFragmentFilePermission(activity: Activity): Boolean {
        return activity.shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE) || activity.shouldShowRequestPermissionRationale(
            READ_EXTERNAL_STORAGE
        )
    }

    private fun launchPermissionSettingsFilePermissionWithResultLauncher(
        intent: Intent,
        resultLauncher: ActivityResultLauncher<Intent>
    ) {
        resultLauncher.launch(intent)
    }

    fun settingsIntentFilePermission(activity: Activity, requestCode: Int): Intent {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", activity.packageName, null)
        intent.putExtra("requestCode", requestCode)
        return intent
    }
}

object DrawOverlayPermissionHelper {
    fun hasDrawOverlayPermission(activity: Activity): Boolean {
        return Settings.canDrawOverlays(activity)
    }

    fun requestDrawOverlayPermission(
        activity: Activity,
        intent: Intent,
        resultLauncher: ActivityResultLauncher<Intent>
    ): Boolean {
        if (!hasDrawOverlayPermission(activity)) {
            resultLauncher.launch(intent)
            return false
        }
        return true

    }

    fun intentDrawOverlayPermission(activity: Activity, requestCode: Int): Intent {
        val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_OVERLAY_PERMISSION
        intent.data = Uri.fromParts("package", activity.packageName, null)
        intent.putExtra("requestCode", requestCode)
        return intent
    }
}

object ResourceHelper {
    fun galleryImageIntent(): Intent {
        val intent = Intent(ACTION_VIEW)
        intent.type = "image/*"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return intent
    }

    fun galleryVideoIntent(): Intent {
        val intent = Intent(ACTION_VIEW)
        intent.type = "video/*"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return intent
    }

    fun galleryIntent(): Intent {
        return Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_GALLERY)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}

fun generateRandomId(): String {
//    return (System.currentTimeMillis().toString().substring(0, 6) + (0..100).random()).toInt()
    return UUID.randomUUID().toString()
}

fun createScreenShotSurfaceView(view: View): Bitmap {
    val viewScreenShot: View = view
    return Bitmap.createBitmap(
        viewScreenShot.width,
        viewScreenShot.height,
        Bitmap.Config.ARGB_8888
    )
}

fun copyToClipboard(context: Context, text: String, labelText: String) {
    val clipboard =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = ClipData.newPlainText(labelText, text)
    clipboard.setPrimaryClip(clip)
}

fun applyHtml(text: String): Spanned {
    val span: Spanned = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        Html.fromHtml(
            text
        )
    } else {
        Html.fromHtml(
            text, Html.FROM_HTML_MODE_LEGACY
        )
    }
    return span
}

fun isUsingNightModeResources(context: Context): Boolean {
    return when (context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> true
        Configuration.UI_MODE_NIGHT_NO -> false
        Configuration.UI_MODE_NIGHT_UNDEFINED -> false
        else -> false
    }
}


fun convertLongToCounter(millis: Long): String {
    val seconds = (millis / 1000) % 60
    val minutes = (millis / (1000 * 60) % 60)
    val hours = (millis / (1000 * 60 * 60) % 24)
    return String.format("%02d.%02d.%02d", hours, minutes, seconds)
}

fun convertFloatOneDecimal(value:Float): String {
    return String.format("%.1f", value)
}

fun convertFloatTwoDecimal(value:Float): String {
    return String.format("%.2f", value)
}

fun isKeyboardVisible(rootView: View) =
    ViewCompat.getRootWindowInsets(rootView)!!.isVisible(WindowInsetsCompat.Type.ime())

