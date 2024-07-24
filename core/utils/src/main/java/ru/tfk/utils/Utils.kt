package ru.tfk.utils

import android.os.Build

fun isAndroidVersionGreaterOrEqual(targetVersion: Int): Boolean {
    return Build.VERSION.SDK_INT >= targetVersion
}
