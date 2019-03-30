package net.dankito.filechooserdialog.ui.adapter

import android.view.View
import android.widget.ImageView
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.filechooserdialog.service.PreviewImageService
import net.dankito.filechooserdialog.service.SelectedFilesManager
import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
import net.dankito.utils.android.extensions.getResourceIdForAttributeId
import net.dankito.utils.android.extensions.setTintColor
import net.dankito.utils.android.ui.view.recyclerview.ListRecyclerAdapter
import java.io.File


open class DirectoryContentAdapter(private val previewImageService: PreviewImageService, private val selectedFilesManager: SelectedFilesManager,
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

        previewImageService.setPreviewImage(viewHolder, viewHolder.imgPreviewImage, item, config)

        val isSelected = selectedFilesManager.isFileSelected(item)
        viewHolder.itemView.isActivated = isSelected && config.showSelectedItemsInItemSelectedBackgroundColor

        viewHolder.imgIsSelected.apply {
            visibility = if (isSelected && config.showCheckMarkForSelectedItems) View.VISIBLE else View.INVISIBLE

            setTintColor(getIsSelectedCheckMarkTintColorId())
        }
    }

    protected open fun ImageView.getIsSelectedCheckMarkTintColorId(): Int {
        return context.getResourceIdForAttributeId(R.attr.FileChooserDialogFileIconTintColor, R.color.colorAccent)
    }

}