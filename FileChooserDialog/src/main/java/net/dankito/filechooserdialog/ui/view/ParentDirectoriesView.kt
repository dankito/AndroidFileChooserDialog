package net.dankito.filechooserdialog.ui.view

import android.content.Context
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import kotlinx.android.synthetic.main.file_chooser_dialog_view_parent_directory.view.*
import net.dankito.filechooserdialog.R
import java.io.File


class ParentDirectoriesView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    var parentDirectorySelectedListener: ((parentDirectory: File) -> Unit)? = null


    private val parentDirectoriesLayout: LinearLayout

    private val layoutInflater = LayoutInflater.from(context)


    init {
        layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT

        parentDirectoriesLayout = LinearLayout(context)
        parentDirectoriesLayout.orientation = LinearLayout.HORIZONTAL
        parentDirectoriesLayout.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        parentDirectoriesLayout.layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT

        addView(parentDirectoriesLayout)
    }


    fun showParentDirectories(directory: File) {
        parentDirectoriesLayout.removeAllViews()

        addParentDirectoriesRecursively(directory)

        this.post { // wait till new size is calculated
            this.fullScroll(ScrollView.FOCUS_RIGHT)
        }
    }

    private fun addParentDirectoriesRecursively(directory: File?) {
        if(directory != null && directory.isDirectory && directory.parentFile != null) { // parent.parentFile != null: filter out root
            addParentDirectoriesRecursively(directory.parentFile)

            addParentDirectoryView(directory)
        }
    }

    private fun addParentDirectoryView(parent: File) {
        val parentDirectoryView = layoutInflater.inflate(R.layout.file_chooser_dialog_view_parent_directory, null)
        parentDirectoryView.txtDirectoryName.text = getFormattedFolderName(parent)

        parentDirectoriesLayout.addView(parentDirectoryView)
        parentDirectoryView.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        parentDirectoryView.setOnClickListener {
            parentDirectorySelectedListener?.invoke(parent)
        }
    }

    private fun getFormattedFolderName(parent: File): CharSequence {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml("<u>${parent.name}</u>", Html.FROM_HTML_MODE_LEGACY)
        }
        else {
            @Suppress("DEPRECATION")
            return Html.fromHtml("<u>${parent.name}</u>")
        }
    }

}