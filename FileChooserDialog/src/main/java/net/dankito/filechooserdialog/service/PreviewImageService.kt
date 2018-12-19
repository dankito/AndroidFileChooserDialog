package net.dankito.filechooserdialog.service

import android.content.Context
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.filechooserdialog.ui.util.LoadThumbnailTask
import net.dankito.filechooserdialog.ui.util.PreviewImageCache
import net.dankito.mime.MimeTypeCategorizer
import net.dankito.mime.MimeTypeDetector
import net.dankito.utils.android.extensions.getResourceIdForAttributeId
import net.dankito.utils.android.extensions.setTintColor
import java.io.File


class PreviewImageService(private val thumbnailService: ThumbnailService, private val mimeTypeDetector: MimeTypeDetector,
                          private val mimeTypeCategorizer: MimeTypeCategorizer) {

    private val previewImageCache = PreviewImageCache()


    fun setPreviewImage(viewHolder: RecyclerView.ViewHolder, imageView: ImageView, file: File, config: FileChooserDialogConfig) {
        imageView.clearColorFilter()
        val cachedPreviewImage = previewImageCache.getCachedPreviewImage(file)

        if(cachedPreviewImage != null) {
            imageView.setImageBitmap(cachedPreviewImage)
        }
        else {
            imageView.setImageBitmap(null) // reset preview image (don't wait till preview image is calculated to show it, as otherwise it may show previous file's preview image

            getPreviewImageForFile(viewHolder, imageView, file, config)
        }
    }

    private fun getPreviewImageForFile(viewHolder: RecyclerView.ViewHolder, imageView: ImageView, file: File, config: FileChooserDialogConfig) {
        val mimeType = mimeTypeDetector.getBestPickForFile(file)

        if(mimeType == null) {
            if(file.isDirectory) {
                setPreviewImageForFolder(imageView, file)
            }
            else { // fallback
                setPreviewImageToResource(imageView, R.drawable.file_chooser_dialog_ic_file_default)
            }
        }
        else {
            setPreviewImageForFile(viewHolder, imageView, file, mimeType, config)
        }
    }

    private fun setPreviewImageForFolder(imageView: ImageView, folder: File) {
        when(folder) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) ->
                    setPreviewImageToResource(imageView, R.drawable.file_chooser_dialog_ic_folder_download)
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)->
                setPreviewImageToResource(imageView, R.drawable.file_chooser_dialog_ic_folder_image)
            else ->
                setPreviewImageToResource(imageView, R.drawable.file_chooser_dialog_ic_folder_default)
        }
    }

    private fun setPreviewImageForFile(viewHolder: RecyclerView.ViewHolder, imageView: ImageView, file: File, mimeType: String, config: FileChooserDialogConfig) {
        setPreviewImageToResource(imageView, getIconForFile(mimeType)) // first set default file icon ...

        if(shouldTryToLoadThumbnailForFile(mimeType, config)) {
            LoadThumbnailTask(viewHolder, imageView, file, mimeType, thumbnailService, previewImageCache).execute() // ... then check if may a thumbnail can be loaded
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

    private fun setPreviewImageToResource(imageView: ImageView, imageResourceId: Int) {
        imageView.setImageResource(imageResourceId)

        imageView.setTintColor(getFileIconTintColorId(imageView.context))
    }

    private fun getFileIconTintColorId(context: Context): Int {
        return context.getResourceIdForAttributeId(R.attr.FileChooserDialogFileIconTintColor, R.color.colorAccent)
    }

    private fun shouldTryToLoadThumbnailForFile(mimeType: String, config: FileChooserDialogConfig): Boolean {
        return (config.tryToLoadThumbnailForImageFiles && mimeTypeCategorizer.isImageFile(mimeType))
                || (config.tryToLoadThumbnailForVideoFiles && mimeTypeCategorizer.isVideoFile(mimeType))
                || (config.tryToLoadAlbumArtForAudioFiles && mimeTypeCategorizer.isAudioFile(mimeType))
    }

}