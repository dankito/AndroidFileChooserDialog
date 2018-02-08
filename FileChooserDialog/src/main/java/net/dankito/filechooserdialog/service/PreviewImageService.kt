package net.dankito.filechooserdialog.service

import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
import net.dankito.filechooserdialog.ui.extensions.setTintColor
import net.dankito.filechooserdialog.ui.util.LoadThumbnailTask
import net.dankito.filechooserdialog.ui.util.PreviewImageCache
import java.io.File


class PreviewImageService(private val thumbnailService: ThumbnailService, private val mimeTypeService: MimeTypeService) {

    private val previewImageCache = PreviewImageCache()


    fun setPreviewImage(viewHolder: DirectoryContentViewHolder, file: File) {
        val cachedPreviewImage = previewImageCache.getCachedPreviewImage(file)

        if(cachedPreviewImage != null) {
            viewHolder.imgThumbnail.setImageBitmap(cachedPreviewImage)
        }
        else {
            viewHolder.imgThumbnail.setImageBitmap(null) // reset preview image (don't wait till preview image is calculated to show it, as otherwise it may show previous file's preview image
            viewHolder.imgThumbnail.clearColorFilter()

            getPreviewImageForFile(viewHolder, file)
        }
    }

    private fun getPreviewImageForFile(viewHolder: DirectoryContentViewHolder, file: File) {
        val mimeType = mimeTypeService.getMimeType(file)

        if(mimeType == null) {
            if(file.isDirectory) {
                setPreviewImageToResource(viewHolder, R.drawable.ic_folder_white_48dp)
            }
            else {
                setPreviewImageToResource(viewHolder, R.drawable.ic_insert_drive_file_white_48dp)
            }
        }
        else {
            if(canLoadThumbnailForFile(mimeType)) {
                LoadThumbnailTask(viewHolder, file, mimeType, thumbnailService, previewImageCache).execute()
            }
            else {
                setPreviewImageToResource(viewHolder, R.drawable.ic_insert_drive_file_white_48dp)
            }
        }
    }

    private fun setPreviewImageToResource(viewHolder: DirectoryContentViewHolder, imageResourceId: Int) {
        viewHolder.imgThumbnail.setImageResource(imageResourceId)

        viewHolder.imgThumbnail.setTintColor(R.color.file_icon_tint_color)
    }

    private fun canLoadThumbnailForFile(mimeType: String): Boolean {
        return mimeTypeService.isImageType(mimeType) || mimeTypeService.isVideoType(mimeType)
    }

}