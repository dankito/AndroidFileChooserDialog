package net.dankito.filechooserdialog.ui.adapter

import android.view.View
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.service.ThumbnailService
import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
import net.dankito.filechooserdialog.ui.util.LoadPreviewImageTask
import java.io.File


class DirectoryContentAdapter(private val thumbnailService: ThumbnailService, private val selectedFiles: List<File>) : ListRecyclerAdapter<File, DirectoryContentViewHolder>() {


    override fun getListItemLayoutId() = R.layout.list_item_file

    override fun createViewHolder(itemView: View): DirectoryContentViewHolder {
        return DirectoryContentViewHolder(itemView)
    }

    override fun bindItemToView(viewHolder: DirectoryContentViewHolder, item: File) {
        viewHolder.txtFilename.text = item.name

        viewHolder.imgThumbnail.setImageBitmap(null) // reset preview image (don't wait till preview image is calculated to show it, as otherwise it may show previous file's preview image
        LoadPreviewImageTask(viewHolder, item, thumbnailService).execute()

        viewHolder.itemView.isActivated = selectedFiles.contains(item)
    }

}