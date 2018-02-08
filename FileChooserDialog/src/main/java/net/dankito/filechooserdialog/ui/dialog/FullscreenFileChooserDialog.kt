package net.dankito.filechooserdialog.ui.dialog

import android.support.v4.app.FragmentActivity
import android.view.View
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.model.Options
import net.dankito.filechooserdialog.ui.view.FileChooserView
import java.io.File


class FullscreenFileChooserDialog : FullscreenDialogFragment(), IFileChooserDialog {

    override val fileChooserView = FileChooserView()

    override var dialogType = FileChooserDialogType.SelectSingleFile

    override var options = Options()

    override var selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)? = null

    override var selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)? = null


    override fun getDialogTag() = IFileChooserDialog.DialogTag

    override fun getLayoutId() = IFileChooserDialog.DialogLayoutResourceId


    override fun setupUI(rootView: View) {
        setup(rootView)
    }


    override fun showDialog(activity: FragmentActivity) {
        showInFullscreen(activity.supportFragmentManager)
    }

    override fun closeDialogOnUiThread() {
        super.closeDialogOnUiThread()
    }

}