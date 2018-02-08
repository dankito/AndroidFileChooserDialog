package net.dankito.filechooserdialog.service

import android.webkit.MimeTypeMap
import java.io.File


class MimeTypeService {

    fun getMimeType(file: File): String? {
        file.extension?.let { extension ->
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }

        return null
    }


    fun isImageType(mimeType: String): Boolean {
        return mimeType.startsWith("image/")
    }

    fun isVideoType(mimeType: String): Boolean {
        return mimeType.startsWith("video/")
    }

}