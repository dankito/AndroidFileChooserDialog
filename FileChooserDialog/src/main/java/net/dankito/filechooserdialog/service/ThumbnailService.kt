package net.dankito.filechooserdialog.service

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import net.dankito.mime.MimeTypeCategorizer
import net.dankito.mime.MimeTypeDetector
import net.dankito.utils.android.image.AndroidImageUtils
import java.io.File


class ThumbnailService(private val context: Context, private val mimeTypeDetector: MimeTypeDetector, private val mimeTypeCategorizer: MimeTypeCategorizer) {

    private val imageUtils = AndroidImageUtils()


    /**
     * Parameters prefWidth and prefHeight will only be used if there's no system thumbnail from MediaStorage and
     *  a thumbnail therefore gets created.
     */
    fun getThumbnail(file: File, prefWidth: Int, prefHeight: Int): Bitmap? {
        mimeTypeDetector.getBestPickForFile(file)?.let { mimeType ->
            return getThumbnail(file, mimeType, prefWidth, prefHeight)
        }

        return null
    }

    /**
     * Parameters prefWidth and prefHeight will only be used if there's no system thumbnail from MediaStorage and
     *  a thumbnail therefore gets created.
     */
    fun getThumbnail(file: File, mimeType: String, prefWidth: Int, prefHeight: Int): Bitmap? {
        if(mimeTypeCategorizer.isImageFile(mimeType)) {
            getImageThumbnailFromMediaStore(file)?.let { return it }

            // then create one ...
            return ThumbnailUtils.extractThumbnail(imageUtils.getCorrectlyRotatedBitmap(file), prefWidth, prefHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT)
        }
        else if(mimeTypeCategorizer.isVideoFile(mimeType)) {
            getVideoThumbnailFromMediaStore(file)?.let { return it }

            // then create one ...
            return ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Video.Thumbnails.MICRO_KIND)
        }
        else if(mimeTypeCategorizer.isAudioFile(mimeType)) {
            return getAlbumCoverFromMediaStore(file)
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

                val thumbnail = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, id.toLong(), MediaStore.Images.Thumbnails.MICRO_KIND, null)
                return imageUtils.correctOrientationIfNeeded(thumbnail, file.absolutePath)
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

    private fun getAlbumCoverFromMediaStore(file: File): Bitmap? {
        var cursor: Cursor? = null

        try {
            val contentResolver = context.contentResolver
            cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Audio.Media.ALBUM_ID),
                    MediaStore.MediaColumns.DATA + "=?", arrayOf(file.absolutePath), null)

            if(cursor != null && cursor.moveToFirst()) {
                val albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                val artworkUri = Uri.parse("content://media/external/audio/albumart")
                val albumArtUri = ContentUris.withAppendedId(artworkUri, albumId)

                return MediaStore.Images.Media.getBitmap(contentResolver, albumArtUri)
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