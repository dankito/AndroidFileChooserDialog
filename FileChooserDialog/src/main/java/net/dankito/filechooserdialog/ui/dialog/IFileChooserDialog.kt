package net.dankito.filechooserdialog.ui.dialog

import android.support.v4.app.FragmentActivity
import android.view.View
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.ui.view.FileChooserView
import java.io.File


interface IFileChooserDialog {

    companion object {
        const val DialogTag = "FileChooserDialog"

        val DialogLayoutResourceId = R.layout.dialog_file_chooser
    }


    val fileChooserView: FileChooserView


    var dialogType: FileChooserDialogType

    var selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)?

    var selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)?


    fun showDialog(activity: FragmentActivity)

    fun closeDialogOnUiThread()


    fun setup(rootView: View) {
        fileChooserView.setup(rootView, dialogType) { didUserSelectFiles, selectedFiles ->
            selectingFilesDone(didUserSelectFiles, selectedFiles)
        }
    }

    fun handlesBackButtonPress(): Boolean {
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


    fun showOpenSingleFileDialog(activity: FragmentActivity, selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        showDialog(activity, FileChooserDialogType.SelectSingleFile, selectSingleFileCallback, null)
    }

    fun showOpenMultipleFilesDialog(activity: FragmentActivity, selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit) {
        showDialog(activity, FileChooserDialogType.SelectMultipleFiles, null, selectMultipleFilesCallback)
    }

    private fun showDialog(activity: FragmentActivity, dialogType: FileChooserDialogType, selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)?,
                           selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)?) {
        this.dialogType = dialogType

        this.selectSingleFileCallback = selectSingleFileCallback
        this.selectMultipleFilesCallback = selectMultipleFilesCallback

        showDialog(activity)
    }

}