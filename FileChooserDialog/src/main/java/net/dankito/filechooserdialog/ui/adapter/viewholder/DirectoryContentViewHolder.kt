package net.dankito.filechooserdialog.ui.adapter.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.file_chooser_dialog_list_item_file.view.*


class DirectoryContentViewHolder(fileListItemView: View) : RecyclerView.ViewHolder(fileListItemView) {

    val imgPreviewImage: ImageView = fileListItemView.imgPreviewImage

    val txtFilename: TextView = fileListItemView.txtFilename

    val imgIsSelected: ImageView = fileListItemView.imgIsSelected

}