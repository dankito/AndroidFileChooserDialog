package net.dankito.filechooserdialog.service

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.provider.MediaStore
import java.io.File


class ThumbnailService(private val context: Context, private val mimeTypeService: MimeTypeService) {

    fun getThumbnail(file: File, width: Int, height: Int): Bitmap? {
        mimeTypeService.getMimeType(file)?.let { mimeType ->
            return getThumbnail(file, mimeType, width, height)
        }

        return null
    }

    fun getThumbnail(file: File, mimeType: String, width: Int, height: Int): Bitmap? {
        if(mimeTypeService.isImageFile(mimeType)) {
            getImageThumbnailFromMediaStore(file)?.let { return it }

            // then create one ...
            return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(file.absolutePath), width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
        }
        else if(mimeTypeService.isVideoFile(mimeType)) {
            getVideoThumbnailFromMediaStore(file)?.let { return it }

            // then create one ...
            return ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Video.Thumbnails.MICRO_KIND)
        }

        return null
    }

    private fun getImageThumbnailFromMediaStore(file: File): Bitmap? {
        var cursor: Cursor? = null

        try {
            val contentResolver = context.contentResolver
            cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.MediaColumns._ID),
                    MediaStore.MediaColumns.DATA + "=?", arrayOf(file.absolutePath), null)

            if(cursor != null && cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))

                return MediaStore.Images.Thumbnails.getThumbnail(contentResolver, id.toLong(), MediaStore.Images.Thumbnails.MICRO_KIND, null)
            }
        } catch(e: Exception) {
            // TODO: log error
        }
        finally {
            cursor?.close()
        }

        return null
    }

    private fun getVideoThumbnailFromMediaStore(file: File): Bitmap? {
        var cursor: Cursor? = null

        try {
            val contentResolver = context.contentResolver
            cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.MediaColumns._ID),
                    MediaStore.MediaColumns.DATA + "=?", arrayOf(file.absolutePath), null)

            if(cursor != null && cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))

                return MediaStore.Video.Thumbnails.getThumbnail(contentResolver, id.toLong(), MediaStore.Video.Thumbnails.MICRO_KIND, null)
            }
        } catch(e: Exception) {
            // TODO: log error
        }
        finally {
            cursor?.close()
        }

        return null
    }

}