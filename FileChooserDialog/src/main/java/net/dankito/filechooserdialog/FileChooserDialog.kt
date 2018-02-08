package net.dankito.filechooserdialog

import android.os.Build
import android.os.Environment
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.dialog_file_chooser.view.*
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.service.SelectedFilesManager
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

    private lateinit var drawerLayout: DrawerLayout


    private lateinit var dialogType: FileChooserDialogType

    private lateinit var selectedFilesManager: SelectedFilesManager

    private var selectSingleFileCallback: ((didUserSelectFile: Boolean, File?) -> Unit)? = null

    private var selectMultipleFilesCallback: ((didUserSelectFiles: Boolean, List<File>?) -> Unit)? = null


    override fun setupUI(rootView: View) {
        selectedFilesManager = SelectedFilesManager(dialogType)
        selectedFilesManager.addSelectedFilesChangedListeners { selectedFilesChanged(it) }

        parentDirectoriesView = rootView.parentDirectoriesView
        parentDirectoriesView.parentDirectorySelectedListener = { setCurrentDirectory(it) }

        directoryContentView = rootView.directoryContentView
        directoryContentView.setupDialog(selectedFilesManager)
        directoryContentView.currentDirectoryChangedListener = { currentDirectoryChanged(it) }

        rootView.btnCancel.setOnClickListener { cancelSelectingFilesAndCloseDialog() }

        btnSelect = rootView.btnSelect
        btnSelect.setOnClickListener { selectingFilesDone() }

        setupNavigationMenu(rootView.navigationView)

        setCurrentDirectory(Environment.getExternalStorageDirectory())
    }

    private fun setupNavigationMenu(navigationView: NavigationView) {
        navigationView.menu?.findItem(R.id.navFolderShortcutDocuments)?.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT // Documents folder is only available on KitKat and newer
        // TODO: check SD card and USB stick state

        navigationView.setNavigationItemSelectedListener { navigationItemSelected(it) }
    }

    override fun customizeToolbar(rootView: View, toolbar: Toolbar) {
        super.customizeToolbar(rootView, toolbar)

        toolbar.setNavigationOnClickListener(null)

        drawerLayout = rootView.folderShortcutsDrawerLayout

        val toggle = ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }


    override fun handlesBackButtonPress(): Boolean {
        val parent = directoryContentView.currentDirectory.parentFile

        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawerLayout()

            return true
        }
        else if(parent == null || parent.absolutePath == "/") {
            cancelSelectingFiles()
            return false
        }
        else { // navigate up one level
            setCurrentDirectory(parent, true)

            return true
        }
    }

    private fun closeDrawerLayout() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun navigationButtonClicked() {
        cancelSelectingFiles()

        super.navigationButtonClicked()
    }

    private fun navigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.navFolderShortcutInternalStorage -> setCurrentDirectory(Environment.getExternalStorageDirectory())
            R.id.navFolderShortcutSdCard -> setCurrentDirectory(Environment.getExternalStorageDirectory()) // TODO
            R.id.navFolderShortcutDownloads -> setCurrentDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
            R.id.navFolderShortcutCameraPhotos -> setCurrentDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
            R.id.navFolderShortcutPictures -> setCurrentDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
            R.id.navFolderShortcutMusic -> setCurrentDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC))
            R.id.navFolderShortcutMovies -> setCurrentDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES))
            R.id.navFolderShortcutDocuments -> if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                                                  setCurrentDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
        }

        closeDrawerLayout()
        return true
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
                if(selectedFilesManager.selectedFiles.size == 1) {
                    callback(true, selectedFilesManager.selectedFiles[0])
                }
            }
        }
        else if(dialogType == FileChooserDialogType.SelectMultipleFiles) {
            selectMultipleFilesCallback?.invoke(true, selectedFilesManager.selectedFiles)
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