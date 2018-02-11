package net.dankito.filechooserdialog.ui.view

import android.Manifest
import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.widget.Toast
import net.dankito.deepthought.android.service.permissions.IPermissionsManager
import net.dankito.deepthought.android.service.permissions.PermissionsManager
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.filechooserdialog.service.*
import net.dankito.filechooserdialog.ui.adapter.DirectoryContentAdapter
import java.io.File


class DirectoryContentView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {


    var currentDirectory: File = File("/")
        private set


    var currentDirectoryChangedListener: ((currentDirectory: File) -> Unit)? = null


    private val mimeTypeService = MimeTypeService()

    private val thumbnailService = ThumbnailService(context, mimeTypeService)

    private val previewImageService = PreviewImageService(thumbnailService, mimeTypeService)

    private val fileService = FilesService()


    private lateinit var selectedFilesManager: SelectedFilesManager

    private var permissionsManager: IPermissionsManager? = null

    private lateinit var config: FileChooserDialogConfig

    private lateinit var contentAdapter: DirectoryContentAdapter


    private val directoryScrollPositions = HashMap<File, Int>()


    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    fun setupView(selectedFilesManager: SelectedFilesManager, permissionsManager: IPermissionsManager?, config: FileChooserDialogConfig) {
        this.selectedFilesManager = selectedFilesManager
        this.permissionsManager = permissionsManager
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
        this.currentDirectory = fileService.avoidDirectoriesWeAreNotAllowedToList(directory)

        if(PermissionsManager.isPermissionGranted(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showContentOfDirectoryWithPermissionGranted(currentDirectory, previousDirectory)
        }
        else {
            permissionsManager?.let {   permissionsManager ->
                permissionsManager.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, R.string.rationale_permission_to_read_external_storage_message) { _, isGranted ->
                    if(isGranted) {
                        showContentOfDirectoryWithPermissionGranted(currentDirectory, previousDirectory)
                    }
                    else {
                        showDoNotHavePermissionToReadStorageMessage()
                    }
            } }

            if(permissionsManager == null) {
                showDoNotHavePermissionToReadStorageMessage()
            }
        }
    }

    private fun showContentOfDirectoryWithPermissionGranted(currentDirectory: File, previousDirectory: File) {
        fileService.getFilesOfDirectorySorted(currentDirectory, config.extensionsFilters)?.let { files ->
            contentAdapter.items = files

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