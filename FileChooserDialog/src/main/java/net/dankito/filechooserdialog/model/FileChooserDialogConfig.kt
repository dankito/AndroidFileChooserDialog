package net.dankito.filechooserdialog.model

import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.service.IDirectoryContentRetriever
import net.dankito.filechooserdialog.service.LocalFilesystemDirectoryContentRetriever
import java.io.File


data class FileChooserDialogConfig @JvmOverloads constructor(
        val extensionsFilters: List<String> = listOf(),
        val initialDirectory: File? = null,
        val suggestedFilenameForSaveFileDialog: String? = null,
        val directoryContentRetriever: IDirectoryContentRetriever = LocalFilesystemDirectoryContentRetriever(),
        val showHorizontalItemDividers: Boolean = true,
        val showCheckMarkForSelectedItems: Boolean = true,
        val showSelectedItemsInItemSelectedBackgroundColor: Boolean = true,
        val showFolderShortcutsView: Boolean = true,
        val tryToLoadThumbnailForImageFiles: Boolean = true,
        val tryToLoadThumbnailForVideoFiles: Boolean = true,
        val tryToLoadAlbumArtForAudioFiles: Boolean = true,
        val permissionToReadExternalStorageRationaleResourceId: Int = R.string.rationale_permission_to_read_external_storage_message
)