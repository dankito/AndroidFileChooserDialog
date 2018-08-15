package net.dankito.filechooserdialog.service

import java.io.File


class FilesService {

    fun avoidDirectoriesWeAreNotAllowedToList(directory: File): File {
        if(directory.absolutePath == "/storage/emulated") { // we're allowed to list /storage/emulated -> list /storage/emulated/0 instead
            return File(directory, "0")
        }

        if(directory.absolutePath == "/storage/self") { // we're allowed to list /storage/self -> list /storage/self/primary instead
            return File(directory, "primary")
        }

        return directory
    }


    fun getFilesOfDirectorySorted(directory: File, returnOnlyDirectories: Boolean = false, extensionsFilters: List<String> = emptyList()): List<File>? {
        getFilesOfDirectory(directory, returnOnlyDirectories, extensionsFilters)?.let { files ->
            return files.sortedWith(fileComparator)
        }

        return null
    }

    fun getFilesOfDirectory(directory: File, returnOnlyDirectories: Boolean = false, extensionsFilters: List<String> = emptyList()): List<File>? {
        val alsoReturnFiles = ! returnOnlyDirectories

        if(extensionsFilters.isEmpty() && alsoReturnFiles) {
            return directory.listFiles()?.toList() // listFiles() can return null, e.g when not having rights to read this directory
        }

        return directory.listFiles { file ->
            val normalizedExtensionsFilters = normalizeExtensionFilters(extensionsFilters)

            file?.let {
                return@listFiles file.isDirectory ||
                        (alsoReturnFiles && normalizedExtensionsFilters.contains(it.extension.toLowerCase()))
            }

            false
        }?.toList()
    }

    /**
     * Removes '*.' at start of extension filter on lower cases extension
     */
    fun normalizeExtensionFilters(extensionsFilters: List<String>): List<String> {
        return extensionsFilters.map {
            var normalizedFilter = it

            if(normalizedFilter.startsWith('*')) {
                normalizedFilter = normalizedFilter.substring(1)
            }

            if(normalizedFilter.startsWith('.')) {
                normalizedFilter = normalizedFilter.substring(1)
            }

            normalizedFilter.toLowerCase()
        }
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

        return@Comparator file0.name.compareTo(file1.name, true)
    }

}