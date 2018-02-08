package net.dankito.filechooserdialog.ui.adapter

import android.view.View
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.service.ThumbnailService
import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
import net.dankito.filechooserdialog.ui.util.LoadPreviewImageTask
import net.dankito.filechooserdialog.ui.util.PreviewImageCache
import java.io.File


class DirectoryContentAdapter(private val thumbnailService: ThumbnailService, private val selectedFiles: List<File>) : ListRecyclerAdapter<File, DirectoryContentViewHolder>() {

    private val previewImageCache = PreviewImageCache()


    override fun getListItemLayoutId() = R.layout.list_item_file

    override fun createViewHolder(itemView: View): DirectoryContentViewHolder {
        return DirectoryContentViewHolder(itemView)
    }

    override fun bindItemToView(viewHolder: DirectoryContentViewHolder, item: File) {
        viewHolder.txtFilename.text = item.name

        setPreviewImage(viewHolder, item)

        viewHolder.itemView.isActivated = selectedFiles.contains(item)
    }

    private fun setPreviewImage(viewHolder: DirectoryContentViewHolder, file: File) {
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