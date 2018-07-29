package net.dankito.filechooserdialog

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.service.IPermissionsService
import net.dankito.filechooserdialog.ui.view.FileChooserView
import java.io.File


class FileChooserDialog : DialogFragment() {

    companion object {
        const val DialogTag = "FileChooserDialog"
    }


    protected val fileChooserView = FileChooserView()

    protected var permissionsService: IPermissionsService? = null

    protected var dialogType = FileChooserDialogType.SelectSingleFile

    protected var config = FileChooserDialogConfig()

    var selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)? = null

    var selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.file_chooser_dialog_dialog_file_chooser, container, false)

        setupUI(rootView)

        return rootView
    }

    private fun setupUI(rootView: View) {
        fileChooserView.setup(rootView, dialogType, permissionsService, config) { didUserSelectFiles, selectedFiles ->
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


    @JvmOverloads
    fun showOpenSingleFileDialog(activity: FragmentActivity, permissionsService: IPermissionsService? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                 selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        showDialog(activity, false, FileChooserDialogType.SelectSingleFile, permissionsService, config, selectSingleFileCallback, null)
    }

    @JvmOverloads
    fun showOpenMultipleFilesDialog(activity: FragmentActivity, permissionsService: IPermissionsService? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                    selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit) {
        showDialog(activity, false, FileChooserDialogType.SelectMultipleFiles, permissionsService, config, null, selectMultipleFilesCallback)
    }

    @JvmOverloads
    fun showOpenSingleFileFullscreenDialog(activity: FragmentActivity, permissionsService: IPermissionsService? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                 selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        showDialog(activity, true, FileChooserDialogType.SelectSingleFile, permissionsService, config, selectSingleFileCallback, null)
    }

    @JvmOverloads
    fun showOpenMultipleFilesFullscreenDialog(activity: FragmentActivity, permissionsService: IPermissionsService? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                    selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit) {
        showDialog(activity, true, FileChooserDialogType.SelectMultipleFiles, permissionsService, config, null, selectMultipleFilesCallback)
    }

    private fun showDialog(activity: FragmentActivity, fullscreen: Boolean, dialogType: FileChooserDialogType, permissionsService: IPermissionsService?, config: FileChooserDialogConfig,
                           selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)?, selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)?) {
        this.dialogType = dialogType
        this.permissionsService = permissionsService
        this.config = config

        this.selectSingleFileCallback = selectSingleFileCallback
        this.selectMultipleFilesCallback = selectMultipleFilesCallback


        val style = if(fullscreen) R.style.FullscreenDialogWithStatusBar else R.style.Dialog
        setStyle(DialogFragment.STYLE_NORMAL, style)

        show(activity.supportFragmentManager, DialogTag)
    }


    private fun closeDialogOnUiThread() {
        dismiss()
    }


    private val keyEventListener = DialogInterface.OnKeyListener { _, _, keyEvent ->
        if(keyEvent?.keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
            return@OnKeyListener handlesBackButtonPress()
        }

        false
    }

}