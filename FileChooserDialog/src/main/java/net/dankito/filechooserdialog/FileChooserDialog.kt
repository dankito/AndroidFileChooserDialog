package net.dankito.filechooserdialog

import android.os.Environment
import android.support.v4.app.FragmentManager
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.dialog_file_chooser.view.*
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.ui.dialog.FullscreenDialogFragment
import net.dankito.filechooserdialog.ui.view.DirectoryContentView
import net.dankito.filechooserdialog.ui.view.ParentDirectoriesView
import java.io.File


class FileChooserDialog : FullscreenDialogFragment() {

    override fun getDialogTag() = "FileChooserDialog"

    override fun getLayoutId() = R.layout.dialog_file_chooser


    private lateinit var parentDirectoriesView: ParentDirectoriesView

    private lateinit var directoryContentView: DirectoryContentView

    private lateinit var btnSelect: Button


    private lateinit var dialogType: FileChooserDialogType

    private var selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)? = null

    private var selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)? = null


    override fun setupUI(rootView: View) {
        parentDirectoriesView = rootView.parentDirectoriesView
        parentDirectoriesView.parentDirectorySelectedListener = { setCurrentDirectory(it) }

        directoryContentView = rootView.directoryContentView
        directoryContentView.dialogType = dialogType
        directoryContentView.currentDirectoryChangedListener = { currentDirectoryChanged(it) }
        directoryContentView.selectedFilesChangedListener = { selectedFilesChanged(it) }

        rootView.btnCancel.setOnClickListener { cancelSelectingFilesAndCloseDialog() }

        btnSelect = rootView.btnSelect
        btnSelect.setOnClickListener { selectingFilesDone() }

        setCurrentDirectory(Environment.getExternalStorageDirectory())
    }


    override fun handlesBackButtonPress(): Boolean {
        val parent = directoryContentView.currentDirectory.parentFile

        if(parent == null || parent.absolutePath == "/") {
            cancelSelectingFiles()
            return false
        }
        else { // navigate up one level
            setCurrentDirectory(parent, true)

            return true
        }
    }

    override fun navigationButtonClicked() {
        cancelSelectingFiles()

        super.navigationButtonClicked()
    }


    private fun setCurrentDirectory(directory: File, isNavigatingBack: Boolean = false) {
        directoryContentView.showContentOfDirectory(directory, isNavigatingBack)
    }

    private fun currentDirectoryChanged(directory: File) {
        parentDirectoriesView.showParentDirectories(directory)
    }


    private fun selectedFilesChanged(selectedFiles: List<File>) {
        btnSelect.isEnabled = selectedFiles.isNotEmpty()
    }

    private fun selectingFilesDone() {
        if(dialogType == FileChooserDialogType.SelectSingleFile) {
            selectSingleFileCallback?.let {callback ->
                if(directoryContentView.selectedFiles.size == 1) {
                    callback(true, directoryContentView.selectedFiles[0])
                }
            }
        }
        else if(dialogType == FileChooserDialogType.SelectMultipleFiles) {
            selectMultipleFilesCallback?.invoke(true, directoryContentView.selectedFiles)
        }

        closeDialogOnUiThread()
    }

    private fun cancelSelectingFilesAndCloseDialog() {
        cancelSelectingFiles()

        closeDialogOnUiThread()
    }

    private fun cancelSelectingFiles() {
        selectSingleFileCallback?.invoke(false, null)

        selectMultipleFilesCallback?.invoke(false, null)
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