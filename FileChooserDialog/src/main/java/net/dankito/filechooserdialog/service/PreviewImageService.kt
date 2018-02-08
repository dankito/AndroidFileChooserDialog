package net.dankito.filechooserdialog.service

import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
import net.dankito.filechooserdialog.ui.util.LoadPreviewImageTask
import net.dankito.filechooserdialog.ui.util.PreviewImageCache
import java.io.File


class PreviewImageService(private val thumbnailService: ThumbnailService) {

    private val previewImageCache = PreviewImageCache()


    fun setPreviewImage(viewHolder: DirectoryContentViewHolder, file: File) {
        val cachedPreviewImage = previewImageCache.getCachedPreviewImage(file)

        if(cachedPreviewImage != null) {
            viewHolder.imgThumbnail.setImageBitmap(cachedPreviewImage)
        }
        else {
            viewHolder.imgThumbnail.setImageBitmap(null) // reset preview image (don't wait till preview image is calculated to show it, as otherwise it may show previous file's preview image

            LoadPreviewImageTask(viewHolder, file, thumbnailService, previewImageCache).execute()
        }
    }

}