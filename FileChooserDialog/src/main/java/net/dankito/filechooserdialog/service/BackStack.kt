package net.dankito.filechooserdialog.service

import java.io.File


class BackStack {

    private val stack = ArrayList<File>()


    fun add(directory: File) {
        stack.add(directory)
    }

    fun pop(): File? {
        if(stack.size > 1) {
            stack.removeAt(stack.size - 1) // remove current directory

            return stack.removeAt(stack.size - 1) // also remove previous directory as due to internal logic it will immediately get added again
        }

        return null
    }

    fun canNavigateBack(): Boolean {
        return stack.size > 1
    }

}