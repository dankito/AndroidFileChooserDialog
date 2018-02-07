package net.dankito.filechooserdialog.ui.adapter

import android.view.View
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
import java.io.File


class DirectoryContentAdapter : ListRecyclerAdapter<File, DirectoryContentViewHolder>() {

    override fun getListItemLayoutId() = R.layout.list_item_file

    override fun createViewHolder(itemView: View): DirectoryContentViewHolder {
        return DirectoryContentViewHolder(itemView)
    }

    override fun bindItemToView(viewHolder: DirectoryContentViewHolder, item: File) {
        viewHolder.txtFilename.text = item.name
    }

}