package net.dankito.filechooserdialog.ui.view

import android.Manifest
import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.Toast
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.service.PreviewImageService
import net.dankito.filechooserdialog.service.SelectedFilesManager
import net.dankito.filechooserdialog.service.ThumbnailService
import net.dankito.filechooserdialog.ui.adapter.DirectoryContentAdapter
import net.dankito.mime.MimeTypeCategorizer
import net.dankito.mime.MimeTypeDetector
import net.dankito.utils.android.io.AndroidFolderUtils
import net.dankito.utils.android.permissions.IPermissionsService
import net.dankito.utils.android.permissions.PermissionsService
import net.dankito.utils.io.FilesUtils
import java.io.File


class DirectoryContentView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {


    var currentDirectory: File = File("/")
        private set


    var currentDirectoryChangedListener: ((currentDirectory: File) -> Unit)? = null


    private val mimeTypeDetector = MimeTypeDetector()

    private val mimeTypeCategorizer = MimeTypeCategorizer()

    private val thumbnailService = ThumbnailService(context, mimeTypeDetector, mimeTypeCategorizer)

    private val previewImageService = PreviewImageService(thumbnailService, mimeTypeDetector, mimeTypeCategorizer)

    private val filesUtils = FilesUtils()

    private val folderUtils = AndroidFolderUtils(context)


    private lateinit var selectedFilesManager: SelectedFilesManager

    private lateinit var dialogType: FileChooserDialogType

    private var permissionsService: IPermissionsService? = null

    private lateinit var config: FileChooserDialogConfig

    private lateinit var contentAdapter: DirectoryContentAdapter


    private val directoryScrollPositions = HashMap<File, Int>()


    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    fun setupView(selectedFilesManager: SelectedFilesManager, dialogType: FileChooserDialogType, permissionsService: IPermissionsService?, config: FileChooserDialogConfig) {
        this.selectedFilesManager = selectedFilesManager
        this.dialogType = dialogType
        this.permissionsService = permissionsService
        this.config = config

        contentAdapter = DirectoryContentAdapter(previewImageService, selectedFilesManager, config)

        this.adapter = contentAdapter
        contentAdapter.itemClickListener = { file -> fileClicked(file) }

        applyConfiguration(config)
    }

    private fun applyConfiguration(config: FileChooserDialogConfig) {
        if(config.showHorizontalItemDividers) {
            val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            addItemDecoration(dividerItemDecoration)
        }
    }


    fun showContentOfDirectory(directory: File) {
        val previousDirectory = this.currentDirectory
        this.currentDirectory = folderUtils.avoidDirectoriesWeAreNotAllowedToList(directory)

        if(PermissionsService.isPermissionGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showContentOfDirectoryWithPermissionGranted(currentDirectory, previousDirectory)
        }
        else {
            permissionsService?.let { permissionsService ->
                permissionsService.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, config.permissionToReadExternalStorageRationaleResourceId) { _, isGranted ->
                    if(isGranted) {
                        showContentOfDirectoryWithPermissionGranted(currentDirectory, previousDirectory)
                    }
                    else {
                        showDoNotHavePermissionToReadStorageMessage()
                    }
            } }

            if(permissionsService == null) {
                showDoNotHavePermissionToReadStorageMessage()
            }
        }
    }

    private fun showContentOfDirectoryWithPermissionGranted(currentDirectory: File, previousDirectory: File) {
        val returnOnlyDirectories = dialogType == FileChooserDialogType.SelectFolder

        filesUtils.getFilesOfDirectorySorted(currentDirectory, returnOnlyDirectories, config.extensionsFilters)?.let { files ->
            contentAdapter.items = files

            selectedFilesManager.clearSelectedFiles()
            if(dialogType == FileChooserDialogType.SelectFolder) {
                selectedFilesManager.toggleFileIsSelected(currentDirectory)
            }

            saveAndRestoreScrollPosition(currentDirectory, previousDirectory)

            currentDirectoryChangedListener?.invoke(currentDirectory)
        }
    }

    private fun saveAndRestoreScrollPosition(currentDirectory: File, previousDirectory: File) {
        (layoutManager as? LinearLayoutManager)?.let { layoutManager ->
            directoryScrollPositions[previousDirectory] = layoutManager.findFirstVisibleItemPosition() // saves current scroll position to be restored when returning to previous directory

            val previousScrollPosition = directoryScrollPositions[currentDirectory]
            if(previousScrollPosition != null) {
                layoutManager.scrollToPosition(previousScrollPosition)
                postDelayed( { // wait till items are set
                    layoutManager.scrollToPosition(previousScrollPosition)
                }, 100)
            }
            else {
                layoutManager.scrollToPosition(0) // when previous scrollY > 0, top of directory isn't displayed but right at previous scroll position
            }
        }
    }

    private fun fileClicked(file: File) {
        if(file.isDirectory) {
            showContentOfDirectory(file)
        }
        else {
            selectedFilesManager.toggleFileIsSelected(file)
        }
    }


    private fun showDoNotHavePermissionToReadStorageMessage() {
        Toast.makeText(context, R.string.does_not_have_permission_to_read_external_storage_message, Toast.LENGTH_LONG).show()
    }

}