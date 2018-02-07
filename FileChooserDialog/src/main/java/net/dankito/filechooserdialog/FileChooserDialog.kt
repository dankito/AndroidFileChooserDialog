package net.dankito.filechooserdialog

import android.os.Environment
import android.support.v4.app.FragmentManager
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import kotlinx.android.synthetic.main.dialog_file_chooser.view.*
import kotlinx.android.synthetic.main.view_parent_directory.view.*
import net.dankito.filechooserdialog.ui.dialog.FullscreenDialogFragment
import java.io.File


class FileChooserDialog : FullscreenDialogFragment() {

    override fun getDialogTag() = "FileChooserDialog"

    override fun getLayoutId() = R.layout.dialog_file_chooser


    private lateinit var parentDirectoriesScrollView: HorizontalScrollView

    private lateinit var parentDirectoriesLayout: LinearLayout


    override fun setupUI(rootView: View) {
        parentDirectoriesScrollView = rootView.scrParentDirectoriesView
        parentDirectoriesLayout = rootView.lytParentDirectoriesView

        rootView.rcyCurrentDirectoryContent.currentDirectoryChangedListener = { currentDirectoryChanged(it) }
        rootView.rcyCurrentDirectoryContent.setCurrentDirectory(Environment.getExternalStorageDirectory())
    }


    private fun currentDirectoryChanged(directory: File) {
        showParentDirectories(directory)
    }

    private fun showParentDirectories(directory: File) {
        parentDirectoriesLayout.removeAllViews()

        addParentDirectoriesRecursively(directory)

        parentDirectoriesScrollView.post { // wait till new size is calculated
            parentDirectoriesScrollView.fullScroll(ScrollView.FOCUS_RIGHT)
        }
    }

    private fun addParentDirectoriesRecursively(directory: File?) {
        if(directory != null && directory.isDirectory && directory.parentFile != null) { // parent.parentFile != null: filter out root
            addParentDirectoriesRecursively(directory.parentFile)

            addParentDirectoryView(directory)
        }
    }

    private fun addParentDirectoryView(parent: File) {
        val parentDirectoryView = layoutInflater.inflate(R.layout.view_parent_directory, null)
        parentDirectoryView.txtDirectoryName.text = parent.name

        parentDirectoriesLayout.addView(parentDirectoryView)
        parentDirectoryView.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        parentDirectoryView.setOnClickListener { currentDirectoryChanged(parent) }
    }


    fun showOpenFileDialog(fragmentManager: FragmentManager) {
        showInFullscreen(fragmentManager)
    }

}