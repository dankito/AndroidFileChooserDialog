package net.dankito.filechooserdialog.service

import net.dankito.utils.io.FileUtils
import net.dankito.utils.io.ListDirectory
import java.io.File


class LocalFilesystemDirectoryContentRetriever : IDirectoryContentRetriever {

    private val fileUtils = FileUtils()


    override fun getFilesOfDirectorySorted(directory: File, listDirectory: ListDirectory, folderDepth: Int, extensionsFilters: List<String>, callback: (List<File>?) -> Unit) {
        callback(fileUtils.getFilesOfDirectorySorted(directory, listDirectory, folderDepth, extensionsFilters))
    }

}