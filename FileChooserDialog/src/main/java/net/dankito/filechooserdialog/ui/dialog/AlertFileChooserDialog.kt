package net.dankito.filechooserdialog.ui.dialog

import android.content.DialogInterface
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AlertDialog
import android.view.KeyEvent
import android.view.LayoutInflater
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.model.Options
import net.dankito.filechooserdialog.ui.view.FileChooserView
import java.io.File


class AlertFileChooserDialog : IFileChooserDialog {

    override val fileChooserView = FileChooserView()

    override var dialogType = FileChooserDialogType.SelectSingleFile

    override var options = Options()

    override var selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)? = null

    override var selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)? = null


    private lateinit var dialog: AlertDialog


    override fun showDialog(activity: FragmentActivity) {
        val rootView = LayoutInflater.from(activity.baseContext).inflate(R.layout.dialog_file_chooser, null)

        rootView?.let {
            setup(rootView)
        }


        val builder = AlertDialog.Builder(activity)
        builder.setView(rootView)

        this.dialog = builder.create()
        dialog.show()


        dialog.setOnKeyListener(keyEventListener)
    }

    private val keyEventListener = object : DialogInterface.OnKeyListener {
        override fun onKey(dialog: DialogInterface?, keyCode: Int, keyEvent: KeyEvent?): Boolean {
            if(keyEvent?.keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                return handlesBackButtonPress()
            }

            return false
        }

    }

    override fun closeDialogOnUiThread() {
        dialog.dismiss()
    }

}