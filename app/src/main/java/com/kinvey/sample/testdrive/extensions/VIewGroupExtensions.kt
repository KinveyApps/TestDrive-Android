package com.kinvey.sample.testdrive.extensions

import android.view.View
import android.view.ViewGroup

inline fun ViewGroup.forEach(action: (View) -> Unit) {
    for (i in 0 until childCount) {
        action(getChildAt(i))
    }
}

inline fun ViewGroup.forEachIndexed(action: (Int, View) -> Unit) {
    for (i in 0 until childCount) {
        action(i, getChildAt(i))
    }
}
