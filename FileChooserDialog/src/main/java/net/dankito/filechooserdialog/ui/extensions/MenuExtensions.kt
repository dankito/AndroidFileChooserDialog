package net.dankito.filechooserdialog.ui.extensions

import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.res.ResourcesCompat
import android.view.Menu


fun Menu?.setItemsTintColor(context: Context, tintColorResourceId: Int) {
    this?.let { menu ->
        val tintColor = ResourcesCompat.getColor(context.resources, tintColorResourceId, context.theme)

        for(i in 0 until menu.size()) {
            menu.getItem(i)?.icon?.let { icon ->
                // If we don't mutate the drawable, then all drawable's with this id will have a color
                // filter applied to it.
                icon.mutate()
                icon.setColorFilter(tintColor, PorterDuff.Mode.SRC_ATOP)
            }
        }
    }
}