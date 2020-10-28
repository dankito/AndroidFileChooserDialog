package net.dankito.filechooserdialog.ui.view

import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.file_chooser_dialog_dialog_file_chooser.view.*
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.service.BackStack
import net.dankito.filechooserdialog.service.SelectedFilesManager
import net.dankito.utils.android.GenericTextWatcher
import net.dankito.utils.android.permissions.IPermissionsService
import net.dankito.utils.android.ui.view.IHandlesBackButtonPress
import java.io.File


open class FileChooserView : IHandlesBackButtonPress {


    private lateinit var folderShortcutsNavigationView: FolderShortcutsNavigationView

    private lateinit var parentDirectoriesView: ParentDirectoriesView

    private lateinit var lytSetFilename: ViewGroup

    private lateinit var edtxtSetFilename: EditText

    private lateinit var directoryContentView: DirectoryContentView

    private lateinit var btnSelect: Button


    private lateinit var selectedFilesManager: SelectedFilesManager

    private lateinit var config: FileChooserDialogConfig

    private lateinit var selectFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit


    private val backStack = BackStack()


    open fun setup(rootView: View, dialogType: FileChooserDialogType, permissionsService: IPermissionsService?, config: FileChooserDialogConfig,
              selectFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit) {
        this.config = config
        this.selectFilesCallback = selectFilesCallback

        folderShortcutsNavigationView = rootView.navigationView
        folderShortcutsNavigationView.folderShortcutSelectedListener = { setCurrentDirectory(it) }

        selectedFilesManager = SelectedFilesManager(dialogType)
        selectedFilesManager.addSelectedFilesChangedListeners { selectedFilesChanged(it) }

        parentDirectoriesView = rootView.parentDirectoriesView
        parentDirectoriesView.parentDirectorySelectedListener = { setCurrentDirectory(it) }

        lytSetFilename = rootView.lytSetFilename
        lytSetFilename.visibility = if (dialogType == FileChooserDialogType.SaveFile) View.VISIBLE else View.GONE

        edtxtSetFilename = rootView.edtxtSetFilename
        if (dialogType == FileChooserDialogType.SaveFile && config.suggestedFilenameForSaveFileDialog != null) {
            edtxtSetFilename.setText(config.suggestedFilenameForSaveFileDialog)
        }

        edtxtSetFilename.addTextChangedListener(GenericTextWatcher { selectedFilesChanged(selectedFilesManager.selectedFiles)}) // if select button is enabled depends on if a filename is set in SaveFile type

        directoryContentView = rootView.directoryContentView
        directoryContentView.setupView(selectedFilesManager, dialogType, permissionsService, config)
        directoryContentView.currentDirectoryChangedListener = { currentDirectoryChanged(it) }

        rootView.btnCancel.setOnClickListener { cancelSelectingFiles() }

        btnSelect = rootView.btnSelect
        btnSelect.setOnClickListener { selectingFilesDone() }


        directoryContentView.directoryContentRetriever = config.directoryContentRetriever

        if (config.showFolderShortcutsView == false) {
            (rootView as? ViewGroup)?.removeView(folderShortcutsNavigationView)
        }

        val initialDir = config.initialDirectory ?: Environment.getExternalStorageDirectory()
        setCurrentDirectory(initialDir)
    }


    override fun handlesBackButtonPress(): Boolean {
        if(config.showFolderShortcutsView && folderShortcutsNavigationView.handlesBackButtonPress()) {

        }
        else if(backStack.canNavigateBack()) {
            backStack.pop()?.let { setCurrentDirectory(it) }
        }
        else {
            cancelSelectingFiles()
        }

        return true
    }


    protected open fun setCurrentDirectory(directory: File) {
        directoryContentView.showContentOfDirectory(directory)
    }

    protected open fun currentDirectoryChanged(directory: File) {
        backStack.add(directory)

        parentDirectoriesView.showParentDirectories(directory)
    }


    protected open fun selectedFilesChanged(selectedFiles: List<File>) {
        btnSelect.isEnabled = selectedFiles.isNotEmpty() &&
                (lytSetFilename.visibility != View.VISIBLE || edtxtSetFilename.text.toString().isNotBlank())
    }

    protected open fun selectingFilesDone() {
        selectFilesCallback(true, selectedFilesManager.selectedFiles)
    }

    protected open fun cancelSelectingFiles() {
        selectFilesCallback(false, null)
    }

}