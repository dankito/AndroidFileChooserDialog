package net.dankito.filechooserdialog.ui.dialog

import android.support.v4.app.FragmentActivity
import android.view.View
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
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

    var config: FileChooserDialogConfig

    var selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)?

    var selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)?


    fun showDialog(activity: FragmentActivity)

    fun closeDialogOnUiThread()


    fun setup(rootView: View) {
        fileChooserView.setup(rootView, dialogType, config) { didUserSelectFiles, selectedFiles ->
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


    fun showOpenSingleFileDialog(activity: FragmentActivity, config: FileChooserDialogConfig, selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        showDialog(activity, FileChooserDialogType.SelectSingleFile, config, selectSingleFileCallback, null)
    }

    fun showOpenMultipleFilesDialog(activity: FragmentActivity, config: FileChooserDialogConfig, selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit) {
        showDialog(activity, FileChooserDialogType.SelectMultipleFiles, config, null, selectMultipleFilesCallback)
    }

    private fun showDialog(activity: FragmentActivity, dialogType: FileChooserDialogType, config: FileChooserDialogConfig, selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)?,
                           selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)?) {
        this.dialogType = dialogType
        this.config = config

        this.selectSingleFileCallback = selectSingleFileCallback
        this.selectMultipleFilesCallback = selectMultipleFilesCallback

        showDialog(activity)
    }

}