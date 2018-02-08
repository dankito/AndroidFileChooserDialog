package net.dankito.filechooserdialog.ui.view

import android.content.Context
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet


class FixMeasurementBugDrawerLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : DrawerLayout(context, attrs, defStyleAttr) {


    /**
     * Fixes java.lang.IllegalArgumentException: DrawerLayout must be measured with MeasureSpec.EXACTLY.
     *
     * Thanks to Rajesh! https://stackoverflow.com/a/32515581
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val adjustedWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY)

        val adjustedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.EXACTLY)

        super.onMeasure(adjustedWidthMeasureSpec, adjustedHeightMeasureSpec)
    }

}