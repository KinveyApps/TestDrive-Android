package com.kinvey.sample.testdrive

import android.view.View
import android.view.ViewGroup
import com.kinvey.sample.testdrive.extensions.forEach

object UiUtils {

    fun enableLayout(view: View, enable: Boolean) {
        view.isEnabled = enable
        if (view is ViewGroup) {
            view.forEach { child ->
                if (child is ViewGroup) { enableLayout(child, enable) }
                else child.isEnabled = enable
            }
        }
    }
}