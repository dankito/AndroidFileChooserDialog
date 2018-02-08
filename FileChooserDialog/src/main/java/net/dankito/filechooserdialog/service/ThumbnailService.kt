package net.dankito.filechooserdialog.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import java.io.File


class ThumbnailService(private val mimeTypeService: MimeTypeService) {

    fun getThumbnail(file: File, width: Int = 40, height: Int = 30): Bitmap? {
        mimeTypeService.getMimeType(file)?.let { mimeType ->
            return getThumbnail(file, mimeType, width, height)
        }

        return null
    }

    fun getThumbnail(file: File, mimeType: String, width: Int = 40, height: Int = 30): Bitmap? {
        if(mimeTypeService.isImageType(mimeType)) {
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.absolutePath), width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
        }
        else if(mimeTypeService.isVideoType(mimeType)) {
            return ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Video.Thumbnails.MINI_KIND)
        }

        return null
    }

}