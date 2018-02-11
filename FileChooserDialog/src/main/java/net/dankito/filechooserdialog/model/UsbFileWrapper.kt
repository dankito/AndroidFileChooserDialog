package net.dankito.filechooserdialog.model

import com.github.mjdev.libaums.fs.UsbFile
import java.io.File


class UsbFileWrapper(private val usbFile: UsbFile) : File(usbFile.name) {

    override fun getName(): String {
        return usbFile.name
    }

    override fun listFiles(): Array<File> {
        return usbFile.listFiles().map { UsbFileWrapper(it) }.toTypedArray()
    }

    override fun getParentFile(): File? {
        if(usbFile.parent == null) {
            return null
        }

        return UsbFileWrapper(usbFile.parent)
    }

    override fun isDirectory(): Boolean {
        return usbFile.isDirectory
    }

    override fun length(): Long {
        return usbFile.length
    }

    override fun lastModified(): Long {
        return usbFile.lastModified()
    }

}