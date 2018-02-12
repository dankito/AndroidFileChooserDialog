package net.dankito.filechooserdialog.ui.extensions

import android.content.Context
import android.content.res.ColorStateList


fun Context.createColorStateList(tintColorResource: Int): ColorStateList {
    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        return resources.getColorStateList(tintColorResource, theme)
    }
    else {
        @Suppress("DEPRECATION")
        return resources.getColorStateList(tintColorResource)
    }
}