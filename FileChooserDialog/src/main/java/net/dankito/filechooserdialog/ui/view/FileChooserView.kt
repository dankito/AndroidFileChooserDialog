package net.dankito.filechooserdialog.ui.view

import android.app.Activity
import android.os.Build
import android.os.Environment
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.dialog_file_chooser.view.*
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.service.SelectedFilesManager
import java.io.File


class FileChooserView {


    private lateinit var parentDirectoriesView: ParentDirectoriesView

    private lateinit var directoryContentView: DirectoryContentView

    private lateinit var btnSelect: Button

    private lateinit var drawerLayout: DrawerLayout


    private lateinit var selectedFilesManager: SelectedFilesManager

    private lateinit var selectFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit


    fun setup(rootView: View, dialogType: FileChooserDialogType, selectFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit) {
        this.selectFilesCallback = selectFilesCallback

        selectedFilesManager = SelectedFilesManager(dialogType)
        selectedFilesManager.addSelectedFilesChangedListeners { selectedFilesChanged(it) }

        parentDirectoriesView = rootView.parentDirectoriesView
        parentDirectoriesView.parentDirectorySelectedListener = { setCurrentDirectory(it) }

        directoryContentView = rootView.directoryContentView
        directoryContentView.setupDialog(selectedFilesManager)
        directoryContentView.currentDirectoryChangedListener = { currentDirectoryChanged(it) }

        rootView.btnCancel.setOnClickListener { cancelSelectingFiles() }

        btnSelect = rootView.btnSelect
        btnSelect.setOnClickListener { selectingFilesDone() }

        setupNavigationMenu(rootView.navigationView)

        rootView.toolbar?.let {
            customizeToolbar(rootView, it)
        }

        setCurrentDirectory(Environment.getExternalStorageDirectory())
    }

    private fun setupNavigationMenu(navigationView: NavigationView) {
        navigationView.menu?.findItem(R.id.navFolderShortcutDocuments)?.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT // Documents folder is only available on KitKat and newer
        // TODO: check SD card and USB stick state

        navigationView.setNavigationItemSelectedListener { navigationItemSelected(it) }
    }

    private fun customizeToolbar(rootView: View, toolbar: Toolbar) {
        toolbar.setNavigationOnClickListener(null)

        drawerLayout = rootView.folderShortcutsDrawerLayout

        val toggle = ActionBarDrawerToggle(
                rootView.context as Activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }


    fun handlesBackButtonPress(): Boolean {
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
        selectFilesCallback(true, selectedFilesManager.selectedFiles)
    }

    private fun cancelSelectingFiles() {
        selectFilesCallback(false, null)
    }

}