package net.dankito.filechooserdialog

import android.os.Environment
import android.support.v4.app.FragmentManager
import android.view.View
import kotlinx.android.synthetic.main.dialog_file_chooser.view.*
import net.dankito.filechooserdialog.ui.adapter.DirectoryContentAdapter
import net.dankito.filechooserdialog.ui.dialog.FullscreenDialogFragment
import java.io.File


class FileChooserDialog : FullscreenDialogFragment() {

    override fun getDialogTag() = "FileChooserDialog"

    override fun getLayoutId() = R.layout.dialog_file_chooser


    private val adapter = DirectoryContentAdapter()


    override fun setupUI(rootView: View) {
        rootView.rcyCurrentDirectoryContent.adapter = adapter
        adapter.itemClickListener = { file -> fileClicked(file) }

        currentDirectoryChanged(Environment.getExternalStorageDirectory())
    }


    private fun currentDirectoryChanged(directory: File) {
        showContentForDirectory(directory)
    }

    private fun showContentForDirectory(directory: File) {
        adapter.items = directory.listFiles().toList()
    }

    private fun fileClicked(file: File) {
        if(file.isDirectory) {
            currentDirectoryChanged(file)
        }
    }


    fun showOpenFileDialog(fragmentManager: FragmentManager) {
        showInFullscreen(fragmentManager)
    }

}