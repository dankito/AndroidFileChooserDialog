package net.dankito.filechooserdialog.ui.dialog

import android.support.v4.app.FragmentManager
import android.view.View
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.ui.view.FileChooserView
import java.io.File


class FullscreenFileChooserDialog : FullscreenDialogFragment(), IFileChooserDialog {

    override val fileChooserView = FileChooserView()

    override var dialogType = FileChooserDialogType.SelectSingleFile

    override var selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)? = null

    override var selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)? = null


    override fun getDialogTag() = IFileChooserDialog.DialogTag

    override fun getLayoutId() = IFileChooserDialog.DialogLayoutResourceId


    override fun setupUI(rootView: View) {
        setup(rootView)
    }


    override fun closeDialogOnUiThread() {
        super.closeDialogOnUiThread()
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