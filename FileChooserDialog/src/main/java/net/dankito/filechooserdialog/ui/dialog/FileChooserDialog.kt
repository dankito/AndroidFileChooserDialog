package net.dankito.filechooserdialog.ui.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.dankito.deepthought.android.service.permissions.IPermissionsManager
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.ui.view.FileChooserView
import java.io.File


class FileChooserDialog : DialogFragment() {

    companion object {
        const val DialogTag = "FileChooserDialog"
    }


    protected val fileChooserView = FileChooserView()

    protected var permissionsManager: IPermissionsManager? = null

    protected var dialogType = FileChooserDialogType.SelectSingleFile

    protected var config = FileChooserDialogConfig()

    var selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)? = null

    var selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.dialog_file_chooser, container, false)

        setupUI(rootView)

        return rootView
    }

    private fun setupUI(rootView: View) {
        fileChooserView.setup(rootView, dialogType, permissionsManager, config) { didUserSelectFiles, selectedFiles ->
            selectingFilesDone(didUserSelectFiles, selectedFiles)
        }

        dialog.setOnKeyListener(keyEventListener)
    }


    private fun handlesBackButtonPress(): Boolean {
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


    fun showOpenSingleFileDialog(activity: FragmentActivity, permissionsManager: IPermissionsManager? = null, config: FileChooserDialogConfig,
                                 selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        showDialog(activity, false, FileChooserDialogType.SelectSingleFile, permissionsManager, config, selectSingleFileCallback, null)
    }

    fun showOpenMultipleFilesDialog(activity: FragmentActivity, permissionsManager: IPermissionsManager? = null, config: FileChooserDialogConfig,
                                    selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit) {
        showDialog(activity, false, FileChooserDialogType.SelectMultipleFiles, permissionsManager, config, null, selectMultipleFilesCallback)
    }

    fun showOpenSingleFileFullscreenDialog(activity: FragmentActivity, permissionsManager: IPermissionsManager? = null, config: FileChooserDialogConfig,
                                 selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        showDialog(activity, true, FileChooserDialogType.SelectSingleFile, permissionsManager, config, selectSingleFileCallback, null)
    }

    fun showOpenMultipleFilesFullscreenDialog(activity: FragmentActivity, permissionsManager: IPermissionsManager? = null, config: FileChooserDialogConfig,
                                    selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit) {
        showDialog(activity, true, FileChooserDialogType.SelectMultipleFiles, permissionsManager, config, null, selectMultipleFilesCallback)
    }

    private fun showDialog(activity: FragmentActivity, fullscreen: Boolean, dialogType: FileChooserDialogType, permissionsManager: IPermissionsManager?, config: FileChooserDialogConfig,
                           selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)?, selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)?) {
        this.dialogType = dialogType
        this.permissionsManager = permissionsManager
        this.config = config

        this.selectSingleFileCallback = selectSingleFileCallback
        this.selectMultipleFilesCallback = selectMultipleFilesCallback


        if(fullscreen) {
            setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogWithStatusBar)
        }

        show(activity.supportFragmentManager, DialogTag)
    }


    private fun closeDialogOnUiThread() {
        dismiss()
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