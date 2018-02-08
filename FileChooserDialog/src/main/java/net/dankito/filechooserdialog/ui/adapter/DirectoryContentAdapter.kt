package net.dankito.filechooserdialog.ui.adapter

import android.view.View
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.service.PreviewImageService
import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
import java.io.File


class DirectoryContentAdapter(private val previewImageService: PreviewImageService, private val selectedFiles: List<File>)
    : ListRecyclerAdapter<File, DirectoryContentViewHolder>() {


    override fun getListItemLayoutId() = R.layout.list_item_file

    override fun createViewHolder(itemView: View): DirectoryContentViewHolder {
        return DirectoryContentViewHolder(itemView)
    }

    override fun bindItemToView(viewHolder: DirectoryContentViewHolder, item: File) {
        viewHolder.txtFilename.text = item.name

        previewImageService.setPreviewImage(viewHolder, item)

        viewHolder.itemView.isActivated = selectedFiles.contains(item)
    }

}