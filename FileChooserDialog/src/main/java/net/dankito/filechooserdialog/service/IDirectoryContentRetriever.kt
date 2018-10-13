package net.dankito.filechooserdialog.service

import net.dankito.utils.io.ListDirectory
import java.io.File


interface IDirectoryContentRetriever {

    fun getFilesOfDirectorySorted(directory: File, listDirectory: ListDirectory, folderDepth: Int, extensionsFilters: List<String>): List<File>?

}