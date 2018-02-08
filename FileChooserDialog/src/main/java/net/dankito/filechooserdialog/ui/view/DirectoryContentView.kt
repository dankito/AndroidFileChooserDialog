package net.dankito.filechooserdialog.ui.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import net.dankito.filechooserdialog.service.FilesService
import net.dankito.filechooserdialog.service.MimeTypeService
import net.dankito.filechooserdialog.service.PreviewImageService
import net.dankito.filechooserdialog.service.ThumbnailService
import net.dankito.filechooserdialog.ui.adapter.DirectoryContentAdapter
import java.io.File


class DirectoryContentView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {


    var currentDirectory: File = File("/")
        private set

    val selectedFiles: List<File> = mutableListOf()


    var currentDirectoryChangedListener: ((currentDirectory: File) -> Unit)? = null

    var selectedFilesChangedListener: ((List<File>) -> Unit)? = null


    private val mimeTypeService = MimeTypeService()

    private val thumbnailService = ThumbnailService(mimeTypeService)

    private val previewImageService = PreviewImageService(thumbnailService, mimeTypeService)

    private val fileService = FilesService()

    private val contentAdapter = DirectoryContentAdapter(previewImageService, selectedFiles)


    init {
        layoutManager = LinearLayoutManager(context)

        this.adapter = contentAdapter
        contentAdapter.itemClickListener = { file -> fileClicked(file) }
    }


    fun showContentOfDirectory(directory: File, isNavigatingBack: Boolean = false) {
        this.currentDirectory = fileService.avoidDirectoriesWeAreNotAllowedToList(directory, isNavigatingBack)

        fileService.getFilesOfDirectorySorted(currentDirectory)?.let { files ->
            contentAdapter.items = files

            currentDirectoryChangedListener?.invoke(currentDirectory)
        }
    }

    private fun fileClicked(file: File) {
        if(file.isDirectory) {
            showContentOfDirectory(file)
        }
        else {
            toggleFileIsSelected(file)
        }
    }

    private fun toggleFileIsSelected(file: File) {
        if(selectedFiles.contains(file)) {
            (selectedFiles as MutableList).remove(file)
        }
        else {
            (selectedFiles as MutableList).add(file)
        }

        adapter.notifyDataSetChanged()

        selectedFilesChangedListener?.invoke(selectedFiles)
    }

}