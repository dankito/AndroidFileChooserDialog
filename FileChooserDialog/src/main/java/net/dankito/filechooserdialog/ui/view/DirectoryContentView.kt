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

    private val contentAdapter = DirectoryContentAdapter()


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
            contentAdapter.items = files.toList()

            return true
        }

        return false
    }

    private fun fileClicked(file: File) {
        if(file.isDirectory) {
            setCurrentDirectory(file)
        }
    }

}