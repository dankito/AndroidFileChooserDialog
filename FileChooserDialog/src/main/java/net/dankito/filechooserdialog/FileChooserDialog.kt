package net.dankito.filechooserdialog

import android.os.Environment
import android.support.v4.app.FragmentManager
import android.view.View
import kotlinx.android.synthetic.main.dialog_file_chooser.view.*
import net.dankito.filechooserdialog.ui.dialog.FullscreenDialogFragment
import net.dankito.filechooserdialog.ui.view.DirectoryContentView
import net.dankito.filechooserdialog.ui.view.ParentDirectoriesView
import java.io.File


class FileChooserDialog : FullscreenDialogFragment() {

    override fun getDialogTag() = "FileChooserDialog"

    override fun getLayoutId() = R.layout.dialog_file_chooser


    private lateinit var parentDirectoriesView: ParentDirectoriesView

    private lateinit var directoryContentView: DirectoryContentView


    override fun setupUI(rootView: View) {
        parentDirectoriesView = rootView.parentDirectoriesView
        parentDirectoriesView.parentDirectorySelectedListener = { setCurrentDirectory(it) }

        directoryContentView = rootView.directoryContentView
        directoryContentView.currentDirectoryChangedListener = { currentDirectoryChanged(it) }

        setCurrentDirectory(Environment.getExternalStorageDirectory())
    }


    override fun handlesBackButtonPress(): Boolean {
        val parent = directoryContentView.currentDirectory.parentFile

        if(parent == null || parent.absolutePath == "/") {
            return false
        }
        else { // navigate up one level
            setCurrentDirectory(parent, true)

            return true
        }
    }

    private fun setCurrentDirectory(directory: File, isNavigatingBack: Boolean = false) {
        directoryContentView.showContentOfDirectory(directory, isNavigatingBack)
    }

    private fun currentDirectoryChanged(directory: File) {
        parentDirectoriesView.showParentDirectories(directory)
    }


    fun showOpenFileDialog(fragmentManager: FragmentManager) {
        showInFullscreen(fragmentManager)
    }

}