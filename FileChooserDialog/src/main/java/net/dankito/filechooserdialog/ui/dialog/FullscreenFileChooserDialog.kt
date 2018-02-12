package net.dankito.filechooserdialog.ui.dialog

import android.content.DialogInterface
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import android.view.View
import net.dankito.deepthought.android.service.permissions.IPermissionsManager
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.ui.view.FileChooserView
import java.io.File


class FullscreenFileChooserDialog : FullscreenDialogFragment(), IFileChooserDialog {

    override val fileChooserView = FileChooserView()

    override var permissionsManager: IPermissionsManager? = null

    override var dialogType = FileChooserDialogType.SelectSingleFile

    override var config = FileChooserDialogConfig()

    override var selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)? = null

    override var selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)? = null


    override fun getDialogTag() = IFileChooserDialog.DialogTag

    override fun getLayoutId() = IFileChooserDialog.DialogLayoutResourceId


    override fun setupUI(rootView: View) {
        setup(rootView)

        dialog.setOnKeyListener(keyEventListener)
    }


    override fun showDialog(activity: FragmentActivity) {
        showInFullscreen(activity.supportFragmentManager)
    }

    override fun closeDialogOnUiThread() {
        super.closeDialogOnUiThread()
    }


    private val keyEventListener = object : DialogInterface.OnKeyListener {
        override fun onKey(dialog: DialogInterface?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
            if(keyEvent?.keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                return handlesBackButtonPress()
            }

            return false
        }

    }

}