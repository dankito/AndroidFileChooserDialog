package net.dankito.filechooserdialog.ui.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import net.dankito.filechooserdialog.ui.adapter.DirectoryContentAdapter
import java.io.File


class DirectoryContentView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {


    var currentDirectoryChangedListener: ((currentDirectory: File) -> Unit)? = null

    var selectedFilesChangedListener: ((List<File>) -> Unit)? = null

    val selectedFiles: List<File> = mutableListOf()


    private val contentAdapter = DirectoryContentAdapter(selectedFiles)


    init {
        layoutManager = LinearLayoutManager(context)
        isLongClickable = true

        this.adapter = contentAdapter
        contentAdapter.itemClickListener = { file -> fileClicked(file) }
    }


    fun setCurrentDirectory(directory: File) {
        if(showContentForDirectory(directory)) {
            currentDirectoryChangedListener?.invoke(directory)
        }
    }

    private fun showContentForDirectory(directory: File): Boolean {
        directory.listFiles()?.let { files -> // listFiles() can return null, e.g when not having rights to read this directory
            contentAdapter.items = files.sortedWith(fileComparator)

            return true
        }

        return false
    }

    private fun fileClicked(file: File) {
        if(file.isDirectory) {
            setCurrentDirectory(file)
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


    private val fileComparator = Comparator<File> { file0, file1 ->
        if(file0 != null && file1 == null) {
            return@Comparator -1
        }
        else if(file0 == null && file1 != null) {
            return@Comparator 1
        }
        else if(file0 == null && file1 == null) {
            return@Comparator 0
        }

        if(file0.isDirectory && file1.isDirectory == false) { // list directories before files
            return@Comparator -1
        }
        else if(file0.isDirectory == false && file1.isDirectory) {
            return@Comparator 1
        }

        return@Comparator file0.name.compareTo(file1.name)
    }

}