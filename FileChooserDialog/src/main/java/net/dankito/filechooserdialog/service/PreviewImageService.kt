package net.dankito.filechooserdialog.service

import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
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

            getPreviewImageForFile(viewHolder, file)
        }
    }

    private fun getPreviewImageForFile(viewHolder: DirectoryContentViewHolder, file: File) {
        mimeTypeService.getMimeType(file)?.let { mimeType ->
            if(canLoadThumbnailForFile(mimeType)) {
                LoadThumbnailTask(viewHolder, file, mimeType, thumbnailService, previewImageCache).execute()
            }
        }
    }

    private fun canLoadThumbnailForFile(mimeType: String): Boolean {
        return mimeTypeService.isImageType(mimeType) || mimeTypeService.isVideoType(mimeType)
    }

}