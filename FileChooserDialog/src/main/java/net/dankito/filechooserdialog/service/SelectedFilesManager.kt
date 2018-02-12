package net.dankito.filechooserdialog.service

import net.dankito.filechooserdialog.model.FileChooserDialogType
import java.io.File


class SelectedFilesManager(private var dialogType: FileChooserDialogType) {

    val selectedFiles: List<File> = mutableListOf()

    private val selectedFilesChangedListeners = ArrayList<(List<File>) -> Unit>()


    fun toggleFileIsSelected(file: File) {
        val isFileSelected = isFileSelected(file)

        if(dialogType != FileChooserDialogType.SelectMultipleFiles) {
            clearSelectedFilesWithoutCallingListeners()
        }

        if(isFileSelected) {
            (selectedFiles as MutableList).remove(file)
        }
        else {
            (selectedFiles as MutableList).add(file)
        }

        callSelectedFilesChangedListeners()
    }

    fun clearSelectedFiles() {
        clearSelectedFilesWithoutCallingListeners()

        callSelectedFilesChangedListeners()
    }

    private fun clearSelectedFilesWithoutCallingListeners() {
        (selectedFiles as MutableList).clear()
    }


    fun isFileSelected(file: File): Boolean {
        return selectedFiles.contains(file)
    }


    fun addSelectedFilesChangedListeners(listener: (List<File>) -> Unit) {
        selectedFilesChangedListeners.add(listener)
    }

    private fun callSelectedFilesChangedListeners() {
        selectedFilesChangedListeners.forEach { it(selectedFiles) }
    }

}