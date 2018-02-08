package net.dankito.filechooserdialog.service

import android.os.Environment
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
import net.dankito.filechooserdialog.ui.extensions.setTintColor
import net.dankito.filechooserdialog.ui.util.LoadThumbnailTask
import net.dankito.filechooserdialog.ui.util.PreviewImageCache
import java.io.File


class PreviewImageService(private val thumbnailService: ThumbnailService, private val mimeTypeService: MimeTypeService) {

    private val previewImageCache = PreviewImageCache()


    fun setPreviewImage(viewHolder: DirectoryContentViewHolder, file: File) {
        viewHolder.imgPreviewImage.clearColorFilter()
        val cachedPreviewImage = previewImageCache.getCachedPreviewImage(file)

        if(cachedPreviewImage != null) {
            viewHolder.imgPreviewImage.setImageBitmap(cachedPreviewImage)
        }
        else {
            viewHolder.imgPreviewImage.setImageBitmap(null) // reset preview image (don't wait till preview image is calculated to show it, as otherwise it may show previous file's preview image

            getPreviewImageForFile(viewHolder, file)
        }
    }

    private fun getPreviewImageForFile(viewHolder: DirectoryContentViewHolder, file: File) {
        val mimeType = mimeTypeService.getMimeType(file)
//        println("Mime type for ${file.name} is $mimeType")

        if(mimeType == null) {
            if(file.isDirectory) {
                setPreviewImageForFolder(viewHolder, file)
            }
            else { // fallback
                setPreviewImageToResource(viewHolder, R.drawable.ic_insert_drive_file_white_48dp)
            }
        }
        else {
            setPreviewImageForFile(viewHolder, file, mimeType)
        }
    }

    private fun setPreviewImageForFolder(viewHolder: DirectoryContentViewHolder, folder: File) {
        when(folder) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) ->
                    setPreviewImageToResource(viewHolder, R.drawable.ic_folder_download_white_48dp)
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)->
                setPreviewImageToResource(viewHolder, R.drawable.ic_folder_image_white_48dp)
            else ->
                setPreviewImageToResource(viewHolder, R.drawable.ic_folder_white_48dp)
        }
    }

    private fun setPreviewImageForFile(viewHolder: DirectoryContentViewHolder, file: File, mimeType: String) {
        if(canLoadThumbnailForFile(mimeType)) {
            LoadThumbnailTask(viewHolder, file, mimeType, thumbnailService, previewImageCache).execute()
        }
        else {
            setPreviewImageToResource(viewHolder, R.drawable.ic_insert_drive_file_white_48dp)
        }
    }

    private fun setPreviewImageToResource(viewHolder: DirectoryContentViewHolder, imageResourceId: Int) {
        viewHolder.imgPreviewImage.setImageResource(imageResourceId)

        viewHolder.imgPreviewImage.setTintColor(R.color.file_icon_tint_color)
    }

    private fun canLoadThumbnailForFile(mimeType: String): Boolean {
        return mimeTypeService.isImageType(mimeType) || mimeTypeService.isVideoType(mimeType)
    }

}