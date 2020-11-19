package net.dankito.filechooserdialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.file_chooser_dialog_dialog_file_chooser.*
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.ui.view.FileChooserView
import net.dankito.utils.android.permissions.IPermissionsService
import java.io.File


open class FileChooserDialog : DialogFragment() {

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

    protected open fun setupUI(rootView: View) {
        fileChooserView.setup(rootView, dialogType, permissionsService, config) { didUserSelectFiles, selectedFiles ->
            selectingFilesDone(didUserSelectFiles, selectedFiles)
        }

        dialog?.setOnKeyListener(keyEventListener)
    }


    protected open fun handlesBackButtonPress(): Boolean {
        return fileChooserView.handlesBackButtonPress()
    }


    protected open fun selectingFilesDone(didUserSelectFiles: Boolean, selectedFiles: List<File>?) {
        if(dialogType == FileChooserDialogType.SelectSingleFile || dialogType == FileChooserDialogType.SaveFile || dialogType == FileChooserDialogType.SelectFolder) {
            var selectedFile = if(selectedFiles?.isNotEmpty() == true) selectedFiles[0] else null

            if (dialogType == FileChooserDialogType.SaveFile && selectedFile != null) {
                selectedFile = File(selectedFile, edtxtSetFilename.text.toString())
            }

            selectSingleFileCallback?.invoke(didUserSelectFiles, selectedFile)
        }
        else {
            selectMultipleFilesCallback?.invoke(didUserSelectFiles, selectedFiles)
        }

        closeDialogOnUiThread()
    }


    @JvmOverloads
    open fun showOpenSingleFileDialog(activity: FragmentActivity, permissionsService: IPermissionsService? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                      selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        showDialog(activity, false, FileChooserDialogType.SelectSingleFile, permissionsService, config, selectSingleFileCallback, null)
    }

    @JvmOverloads
    open fun showOpenMultipleFilesDialog(activity: FragmentActivity, permissionsService: IPermissionsService? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                    selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit) {
        showDialog(activity, false, FileChooserDialogType.SelectMultipleFiles, permissionsService, config, null, selectMultipleFilesCallback)
    }

    @JvmOverloads
    open fun showSelectFolderDialog(activity: FragmentActivity, permissionsService: IPermissionsService? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                               selectFolderCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        showDialog(activity, false, FileChooserDialogType.SelectFolder, permissionsService, config, selectFolderCallback, null)
    }

    @JvmOverloads
    open fun showOpenSingleFileFullscreenDialog(activity: FragmentActivity, permissionsService: IPermissionsService? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                 selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        showDialog(activity, true, FileChooserDialogType.SelectSingleFile, permissionsService, config, selectSingleFileCallback, null)
    }

    @JvmOverloads
    open fun showOpenMultipleFilesFullscreenDialog(activity: FragmentActivity, permissionsService: IPermissionsService? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                    selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit) {
        showDialog(activity, true, FileChooserDialogType.SelectMultipleFiles, permissionsService, config, null, selectMultipleFilesCallback)
    }

    @JvmOverloads
    open fun showSaveFileDialog(activity: FragmentActivity, permissionsService: IPermissionsService? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                fileSelectedCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        showDialog(activity, false, FileChooserDialogType.SaveFile, permissionsService, config, fileSelectedCallback, null)
    }

    @JvmOverloads
    open fun showSaveFileInFullscreenDialog(activity: FragmentActivity, permissionsService: IPermissionsService? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                            fileSelectedCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        showDialog(activity, true, FileChooserDialogType.SaveFile, permissionsService, config, fileSelectedCallback, null)
    }

    @JvmOverloads
    open fun showSelectFolderFullscreenDialog(activity: FragmentActivity, permissionsService: IPermissionsService? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                         selectFolderCallback: (didUserSelectFile: Boolean, File?) -> Unit) {
        showDialog(activity, true, FileChooserDialogType.SelectFolder, permissionsService, config, selectFolderCallback, null)
    }

    protected open fun showDialog(activity: FragmentActivity, fullscreen: Boolean, dialogType: FileChooserDialogType, permissionsService: IPermissionsService?, config: FileChooserDialogConfig,
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


    protected open fun closeDialogOnUiThread() {
        dismiss()
    }


    private val keyEventListener = DialogInterface.OnKeyListener { _, _, keyEvent ->
        return@OnKeyListener handleKeyPress(keyEvent)
    }

    protected open fun handleKeyPress(keyEvent: KeyEvent): Boolean {
        if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
            return handlesBackButtonPress()
        }

        return false
    }

}