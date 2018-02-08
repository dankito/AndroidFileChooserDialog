package net.dankito.filechooserdialog.ui.extensions

import android.os.Build
import android.support.v4.widget.ImageViewCompat
import android.widget.ImageView


fun ImageView.setTintColor(tintColorResourceId: Int) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setColorFilter(context.getColor(tintColorResourceId))
    }
    else {
        setColorFilter(context.resources.getColor(tintColorResourceId))
    }
}


/**
 * Tint color cannot be set directly in layout xml as older versions of Android don't support the android:tint attribute (application crashes then)
 */
fun ImageView.setTintList(tintColorResource: Int) {
    val resources = context.resources

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.imageTintList = resources.getColorStateList(tintColorResource, context.theme)
    }
    else {
        ImageViewCompat.setImageTintList(this, resources.getColorStateList(tintColorResource))
    }
}