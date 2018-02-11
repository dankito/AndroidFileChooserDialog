package net.dankito.filechooserdialog.ui.view

import android.content.Context
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
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

    private val thumbnailService = ThumbnailService(mimeTypeService)

    private val previewImageService = PreviewImageService(thumbnailService, mimeTypeService)

    private val fileService = FilesService()

    private lateinit var selectedFilesManager: SelectedFilesManager

    private lateinit var config: FileChooserDialogConfig

    private lateinit var contentAdapter: DirectoryContentAdapter


    init {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        addItemDecoration(dividerItemDecoration)
    }

    fun setupView(selectedFilesManager: SelectedFilesManager, config: FileChooserDialogConfig) {
        this.selectedFilesManager = selectedFilesManager
        this.config = config

        contentAdapter = DirectoryContentAdapter(previewImageService, selectedFilesManager)

        this.adapter = contentAdapter
        contentAdapter.itemClickListener = { file -> fileClicked(file) }
    }


    fun showContentOfDirectory(directory: File) {
        this.currentDirectory = fileService.avoidDirectoriesWeAreNotAllowedToList(directory)

        fileService.getFilesOfDirectorySorted(currentDirectory, config.extensionsFilters)?.let { files ->
            contentAdapter.items = files

            currentDirectoryChangedListener?.invoke(currentDirectory)
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

}