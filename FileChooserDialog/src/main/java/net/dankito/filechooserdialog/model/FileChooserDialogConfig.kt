package net.dankito.filechooserdialog.model

import net.dankito.filechooserdialog.R


data class FileChooserDialogConfig @JvmOverloads constructor(
        val extensionsFilters: List<String> = listOf(),
        val showHorizontalItemDividers: Boolean = true,
        val showCheckMarkForSelectedItems: Boolean = true,
        val showSelectedItemsInItemSelectedBackgroundColor: Boolean = true,
        val permissionToReadExternalStorageRationaleResourceId: Int = R.string.rationale_permission_to_read_external_storage_message
)