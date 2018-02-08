package net.dankito.filechooserdialog.ui.adapter.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_file.view.*


class DirectoryContentViewHolder(fileListItemView: View) : RecyclerView.ViewHolder(fileListItemView) {

    val imgPreviewImage: ImageView = fileListItemView.imgPreviewImage

    val txtFilename: TextView = fileListItemView.txtFilename

}