package net.dankito.filechooserdialog.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import net.dankito.filechooserdialog.FileChooserDialog
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.service.MimeTypeService
import net.dankito.filechooserdialog.service.PreviewImageService
import net.dankito.filechooserdialog.service.SelectedFilesManager
import net.dankito.filechooserdialog.service.ThumbnailService
import net.dankito.filechooserdialog.ui.adapter.DirectoryContentAdapter
import net.dankito.filechooserdialog.ui.dialog.IFileChooserDialog


class MainActivity : AppCompatActivity() {

    private var mimeTypeService = MimeTypeService()

    private var thumbnailService = ThumbnailService(mimeTypeService)

    private var previewImageService = PreviewImageService(thumbnailService, mimeTypeService)

    private var selectedSingleFileAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectSingleFile))

    private var selectedMultipleFilesAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectMultipleFiles))

    private var selectedSingleFileInFullscreenDialogAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectSingleFile))

    private var selectedMultipleFilesInFullscreenDialogAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectMultipleFiles))

    private var fileChooserDialog: IFileChooserDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)


        rcySelectedSingleFile.adapter = selectedSingleFileAdapter
        rcySelectedMultipleFiles.adapter = selectedMultipleFilesAdapter

        rcySelectedSingleFileInFullscreenDialog.adapter = selectedSingleFileInFullscreenDialogAdapter
        rcySelectedMultipleFilesInFullscreenDialog.adapter = selectedMultipleFilesInFullscreenDialogAdapter


        btnSelectSingleFile.setOnClickListener {
            fileChooserDialog = FileChooserDialog.showOpenSingleFileDialog(this) { didUserSelectFile, selectedFile ->
                selectedSingleFileAdapter.items = if(selectedFile != null) listOf(selectedFile) else listOf()
                fileChooserDialog = null
            }
        }

        btnSelectMultipleFiles.setOnClickListener {
            fileChooserDialog = FileChooserDialog.showOpenMultipleFilesDialog(this) { didUserSelectFiles, selectedFiles ->
                selectedMultipleFilesAdapter.items = selectedFiles ?: listOf()
                fileChooserDialog = null
            }
        }

        btnSelectSingleFileInFullscreenDialog.setOnClickListener {
            fileChooserDialog = FileChooserDialog.showOpenSingleFileDialogInFullscreen(this) { didUserSelectFile, selectedFile ->
                selectedSingleFileInFullscreenDialogAdapter.items = if(selectedFile != null) listOf(selectedFile) else listOf()
                fileChooserDialog = null
            }
        }

        btnSelectMultipleFilesInFullscreenDialog.setOnClickListener {
            fileChooserDialog = FileChooserDialog.showOpenMultipleFilesDialogInFullscreen(this) { didUserSelectFiles, selectedFiles ->
                selectedMultipleFilesInFullscreenDialogAdapter.items = selectedFiles ?: listOf()
                fileChooserDialog = null
            }
        }
    }

    override fun onBackPressed() {
        if(fileChooserDialog == null || fileChooserDialog?.handlesBackButtonPress() == false) {
            super.onBackPressed()
        }
    }

}
