package net.dankito.filechooserdialog.ui.adapter

import android.view.View
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.filechooserdialog.service.PreviewImageService
import net.dankito.filechooserdialog.service.SelectedFilesManager
import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
import java.io.File


class DirectoryContentAdapter(private val previewImageService: PreviewImageService, private val selectedFilesManager: SelectedFilesManager,
                              private val config: FileChooserDialogConfig = FileChooserDialogConfig())
    : ListRecyclerAdapter<File, DirectoryContentViewHolder>() {


    init {
        selectedFilesManager.addSelectedFilesChangedListeners {
            notifyDataSetChanged()
        }
    }


    override fun getListItemLayoutId() = R.layout.file_chooser_dialog_list_item_file

    override fun createViewHolder(itemView: View): DirectoryContentViewHolder {
        return DirectoryContentViewHolder(itemView)
    }

    override fun bindItemToView(viewHolder: DirectoryContentViewHolder, item: File) {
        viewHolder.txtFilename.text = item.name

        previewImageService.setPreviewImage(viewHolder, item)

        val isSelected = selectedFilesManager.isFileSelected(item)
        viewHolder.itemView.isActivated = isSelected && config.showSelectedItemsInItemSelectedBackgroundColor
        viewHolder.imgIsSelected.visibility = if(isSelected && config.showCheckMarkForSelectedItems) View.VISIBLE else View.INVISIBLE
    }

}