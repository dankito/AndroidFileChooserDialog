package net.dankito.filechooserdialog.service

import android.os.Environment
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
import net.dankito.filechooserdialog.ui.extensions.setTintColor
import net.dankito.filechooserdialog.ui.util.LoadThumbnailTask
import net.dankito.filechooserdialog.ui.util.PreviewImageCache
import net.dankito.mime.MimeTypeCategorizer
import net.dankito.mime.MimeTypeDetector
import net.dankito.mime.MimeTypePicker
import java.io.File


class PreviewImageService(private val thumbnailService: ThumbnailService, private val mimeTypeDetector: MimeTypeDetector, private val mimeTypePicker: MimeTypePicker,
                          private val mimeTypeCategorizer: MimeTypeCategorizer) {

    private val previewImageCache = PreviewImageCache()


    fun setPreviewImage(viewHolder: DirectoryContentViewHolder, file: File) {
        viewHolder.imgPreviewImage.clearColorFilter()
        val cachedPreviewImage = previewImageCache.getCachedPreviewImage(file)

        if(cachedPreviewImage != null) {
            viewHolder.imgPreviewImage.setImageBitmap(cachedPreviewImage)
        }
        else {
            viewHolder.imgPreviewImage.setImageBitmap(null) // reset preview image (don't wait till preview image is calculated to show it, as otherwise it may show previous file's preview image

            getPreviewImageForFile(viewHolder, file)
        }
    }

    private fun getPreviewImageForFile(viewHolder: DirectoryContentViewHolder, file: File) {
        val mimeType = mimeTypePicker.getBestPick(mimeTypeDetector, file)

        if(mimeType == null) {
            if(file.isDirectory) {
                setPreviewImageForFolder(viewHolder, file)
            }
            else { // fallback
                setPreviewImageToResource(viewHolder, R.drawable.file_chooser_dialog_ic_file_default)
            }
        }
        else {
            setPreviewImageForFile(viewHolder, file, mimeType)
        }
    }

    private fun setPreviewImageForFolder(viewHolder: DirectoryContentViewHolder, folder: File) {
        when(folder) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) ->
                    setPreviewImageToResource(viewHolder, R.drawable.file_chooser_dialog_ic_folder_download)
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)->
                setPreviewImageToResource(viewHolder, R.drawable.file_chooser_dialog_ic_folder_image)
            else ->
                setPreviewImageToResource(viewHolder, R.drawable.file_chooser_dialog_ic_folder_default)
        }
    }

    private fun setPreviewImageForFile(viewHolder: DirectoryContentViewHolder, file: File, mimeType: String) {
        if(canLoadThumbnailForFile(mimeType)) {
            LoadThumbnailTask(viewHolder, file, mimeType, thumbnailService, previewImageCache).execute()
        }
        else {
            setPreviewImageToResource(viewHolder, getIconForFile(mimeType))

            if(mimeTypeCategorizer.isAudioFile(mimeType)) { // set default icon for audio files above as fallback
                LoadThumbnailTask(viewHolder, file, mimeType, thumbnailService, previewImageCache).execute() // then check if audio file's album art can be loaded from MediaStore
            }
        }
    }

    private fun getIconForFile(mimeType: String): Int {
        return when {
            mimeTypeCategorizer.isImageFile(mimeType) -> R.drawable.file_chooser_dialog_ic_file_image
            mimeTypeCategorizer.isAudioFile(mimeType) -> R.drawable.file_chooser_dialog_ic_file_music
            mimeTypeCategorizer.isVideoFile(mimeType) -> R.drawable.file_chooser_dialog_ic_file_video
            mimeTypeCategorizer.isPdfFile(mimeType) -> R.drawable.file_chooser_dialog_ic_file_pdf
            mimeTypeCategorizer.isMicrosoftWordFile(mimeType) || mimeTypeCategorizer.isOpenOfficeWriterFile(mimeType)
                -> R.drawable.file_chooser_dialog_ic_file_word
            mimeTypeCategorizer.isMicrosoftExcelFile(mimeType) || mimeTypeCategorizer.isOpenOfficeCalcFile(mimeType)
                -> R.drawable.file_chooser_dialog_ic_file_excel
            mimeTypeCategorizer.isMicrosoftPowerPointFile(mimeType) || mimeTypeCategorizer.isOpenOfficeImpressFile(mimeType)
                -> R.drawable.file_chooser_dialog_ic_file_powerpoint
            mimeTypeCategorizer.isMarkUpFile(mimeType) -> R.drawable.file_chooser_dialog_ic_file_xml
            mimeTypeCategorizer.isDocument(mimeType) -> R.drawable.file_chooser_dialog_ic_file_document
            else -> R.drawable.file_chooser_dialog_ic_file_default
        }
    }

    private fun setPreviewImageToResource(viewHolder: DirectoryContentViewHolder, imageResourceId: Int) {
        viewHolder.imgPreviewImage.setImageResource(imageResourceId)

        viewHolder.imgPreviewImage.setTintColor(R.color.file_chooser_dialog_file_icon_tint_color)
    }

    private fun canLoadThumbnailForFile(mimeType: String): Boolean {
        return mimeTypeCategorizer.isImageFile(mimeType) || mimeTypeCategorizer.isVideoFile(mimeType)
    }

}