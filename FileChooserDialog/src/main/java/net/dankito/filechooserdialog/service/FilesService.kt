package net.dankito.filechooserdialog.service

import java.io.File


class FilesService {

    fun avoidDirectoriesWeAreNotAllowedToList(directory: File, isNavigatingBack: Boolean): File {
        if(directory.absolutePath == "/storage/emulated") { // we're allowed to list /storage/emulated -> list /storage/emulated/0 instead
            if(isNavigatingBack) {
                return directory.parentFile
            }
            else {
                return File(directory, "0")
            }
        }

        if(directory.absolutePath == "/storage/self") { // we're allowed to list /storage/self -> list /storage/self/primary instead
            if(isNavigatingBack) {
                return directory.parentFile
            }
            else {
                return File(directory, "primary")
            }
        }

        return directory
    }


    fun getFilesOfDirectorySorted(directory: File): List<File>? {
        directory.listFiles()?.let { files -> // listFiles() can return null, e.g when not having rights to read this directory
            return files.sortedWith(fileComparator)
        }

        return null
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