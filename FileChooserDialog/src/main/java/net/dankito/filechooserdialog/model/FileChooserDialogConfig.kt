package net.dankito.filechooserdialog.model

import java.io.File
import net.dankito.filechooserdialog.R


data class FileChooserDialogConfig @JvmOverloads constructor(
        val extensionsFilters: List<String> = listOf(),
        val initialDirectory: File? = null,
        val showHorizontalItemDividers: Boolean = true,
        val showCheckMarkForSelectedItems: Boolean = true,
        val showSelectedItemsInItemSelectedBackgroundColor: Boolean = true,
        val tryToLoadThumbnailForImageFiles: Boolean = true,
        val tryToLoadThumbnailForVideoFiles: Boolean = true,
        val tryToLoadAlbumArtForAudioFiles: Boolean = true,
        val permissionToReadExternalStorageRationaleResourceId: Int = R.string.rationale_permission_to_read_external_storage_message
)