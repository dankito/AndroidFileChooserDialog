package net.dankito.filechooserdialog.ui.view

import android.os.Environment
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.file_chooser_dialog_dialog_file_chooser.view.*
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.service.BackStack
import net.dankito.filechooserdialog.service.IPermissionsManager
import net.dankito.filechooserdialog.service.SelectedFilesManager
import java.io.File


class FileChooserView {


    private lateinit var folderShortcutsNavigationView: FolderShortcutsNavigationView

    private lateinit var parentDirectoriesView: ParentDirectoriesView

    private lateinit var directoryContentView: DirectoryContentView

    private lateinit var btnSelect: Button


    private lateinit var selectedFilesManager: SelectedFilesManager

    private lateinit var config: FileChooserDialogConfig

    private lateinit var selectFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit


    private val backStack = BackStack()


    fun setup(rootView: View, dialogType: FileChooserDialogType, permissionsManager: IPermissionsManager?, config: FileChooserDialogConfig,
              selectFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit) {
        this.config = config
        this.selectFilesCallback = selectFilesCallback

        folderShortcutsNavigationView = rootView.navigationView
        folderShortcutsNavigationView.folderShortcutSelectedListener = { setCurrentDirectory(it) }

        selectedFilesManager = SelectedFilesManager(dialogType)
        selectedFilesManager.addSelectedFilesChangedListeners { selectedFilesChanged(it) }

        parentDirectoriesView = rootView.parentDirectoriesView
        parentDirectoriesView.parentDirectorySelectedListener = { setCurrentDirectory(it) }

        directoryContentView = rootView.directoryContentView
        directoryContentView.setupView(selectedFilesManager, permissionsManager, config)
        directoryContentView.currentDirectoryChangedListener = { currentDirectoryChanged(it) }

        rootView.btnCancel.setOnClickListener { cancelSelectingFiles() }

        btnSelect = rootView.btnSelect
        btnSelect.setOnClickListener { selectingFilesDone() }

        setCurrentDirectory(Environment.getExternalStorageDirectory())
    }


    fun handlesBackButtonPress(): Boolean {
        if(folderShortcutsNavigationView.handlesBackButtonPress()) {

        }
        else if(backStack.canNavigateBack()) {
            backStack.pop()?.let { setCurrentDirectory(it) }
        }
        else {
            cancelSelectingFiles()
        }

        return true
    }


    private fun setCurrentDirectory(directory: File) {
        directoryContentView.showContentOfDirectory(directory)
    }

    private fun currentDirectoryChanged(directory: File) {
        backStack.add(directory)

        parentDirectoriesView.showParentDirectories(directory)
    }


    private fun selectedFilesChanged(selectedFiles: List<File>) {
        btnSelect.isEnabled = selectedFiles.isNotEmpty()
    }

    private fun selectingFilesDone() {
        selectFilesCallback(true, selectedFilesManager.selectedFiles)
    }

    private fun cancelSelectingFiles() {
        selectFilesCallback(false, null)
    }

}