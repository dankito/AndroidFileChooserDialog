package net.dankito.filechooserdialog.ui.dialog

import android.support.v4.app.FragmentManager
import android.view.View
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.ui.view.FileChooserView
import java.io.File


class FullscreenFileChooserDialog : FullscreenDialogFragment() {

    override fun getDialogTag() = "FileChooserDialog"

    override fun getLayoutId() = R.layout.dialog_file_chooser


    private val fileChooserView = FileChooserView()


    private lateinit var dialogType: FileChooserDialogType

    private var selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)? = null

    private var selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)? = null


    override fun setupUI(rootView: View) {
        fileChooserView.setup(rootView, dialogType) { didUserSelectFiles, selectedFiles ->
            selectingFilesDone(didUserSelectFiles, selectedFiles)
        }
    }


    override fun handlesBackButtonPress(): Boolean {
        return fileChooserView.handlesBackButtonPress()
    }


    private fun selectingFilesDone(didUserSelectFiles: Boolean, selectedFiles: List<File>?) {
        if(dialogType == FileChooserDialogType.SelectSingleFile) {
            val selectedFile = if(selectedFiles?.isNotEmpty() == true) selectedFiles[0] else null
            selectSingleFileCallback?.invoke(didUserSelectFiles, selectedFile)
        }
        else {
            selectMultipleFilesCallback?.invoke(didUserSelectFiles, selectedFiles)
        }

        closeDialogOnUiThread()
    }


    fun showOpenSingleFileDialog(fragmentManager: FragmentManager, selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        this.dialogType = FileChooserDialogType.SelectSingleFile
        this.selectSingleFileCallback = selectSingleFileCallback

        showInFullscreen(fragmentManager)
    }

    fun showOpenMultipleFilesDialog(fragmentManager: FragmentManager, selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit) {
        this.dialogType = FileChooserDialogType.SelectMultipleFiles
        this.selectMultipleFilesCallback = selectMultipleFilesCallback

        showInFullscreen(fragmentManager)
    }

}