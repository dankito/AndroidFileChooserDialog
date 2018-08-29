package net.dankito.filechooserdialog.demo

import android.os.Build
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import net.dankito.filechooserdialog.FileChooserDialog
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.utils.android.permissions.PermissionsService
import net.dankito.filechooserdialog.service.PreviewImageService
import net.dankito.filechooserdialog.service.SelectedFilesManager
import net.dankito.filechooserdialog.service.ThumbnailService
import net.dankito.filechooserdialog.ui.adapter.DirectoryContentAdapter
import net.dankito.mime.MimeTypeCategorizer
import net.dankito.mime.MimeTypeDetector


class MainActivity : AppCompatActivity() {

    private val permissionsService = PermissionsService(this)

    private val mimeTypeDetector = MimeTypeDetector()

    private val mimeTypeCategorizer = MimeTypeCategorizer()

    private val thumbnailService = ThumbnailService(this, mimeTypeDetector, mimeTypeCategorizer)

    private val previewImageService = PreviewImageService(thumbnailService, mimeTypeDetector, mimeTypeCategorizer)

    private val selectedSingleFileAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectSingleFile))

    private val selectedMultipleFilesAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectMultipleFiles))

    private val selectedFolderAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectFolder))

    private val selectedSingleFileInFullscreenDialogAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectSingleFile))

    private val selectedMultipleFilesInFullscreenDialogAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectMultipleFiles))

    private val selectedFolderInFullscreenDialogAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectFolder))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, theme) // don't know why, after removing fitsSystemWindows status bar color had to be set manually
        }


        rcySelectedSingleFile.adapter = selectedSingleFileAdapter
        rcySelectedMultipleFiles.adapter = selectedMultipleFilesAdapter
        rcySelectedFolder.adapter = selectedFolderAdapter

        rcySelectedSingleFileInFullscreenDialog.adapter = selectedSingleFileInFullscreenDialogAdapter
        rcySelectedMultipleFilesInFullscreenDialog.adapter = selectedMultipleFilesInFullscreenDialogAdapter
        rcySelectedFolderInFullscreenDialog.adapter = selectedFolderInFullscreenDialogAdapter


        setupShowFileChooserDialogButtons()
    }

    private fun setupShowFileChooserDialogButtons() {
        btnSelectSingleFile.setOnClickListener {
            FileChooserDialog().showOpenSingleFileDialog(this, permissionsService) { _, selectedFile ->
                selectedSingleFileAdapter.items = if (selectedFile != null) listOf(selectedFile) else listOf()
            }
        }

        btnSelectMultipleFiles.setOnClickListener {
            FileChooserDialog().showOpenMultipleFilesDialog(this, permissionsService) { _, selectedFiles ->
                selectedMultipleFilesAdapter.items = selectedFiles ?: listOf()
            }
        }

        btnSelectFolder.setOnClickListener {
            FileChooserDialog().showSelectFolderDialog(this, permissionsService) { _, selectedFile ->
                selectedFolderAdapter.items = if (selectedFile != null) listOf(selectedFile) else listOf()
            }
        }

        btnSelectSingleFileInFullscreenDialog.setOnClickListener {
            FileChooserDialog().showOpenSingleFileFullscreenDialog(this, permissionsService) { _, selectedFile ->
                selectedSingleFileInFullscreenDialogAdapter.items = if (selectedFile != null) listOf(selectedFile) else listOf()
            }
        }

        btnSelectMultipleFilesInFullscreenDialog.setOnClickListener {
            FileChooserDialog().showOpenMultipleFilesFullscreenDialog(this, permissionsService) { _, selectedFiles ->
                selectedMultipleFilesInFullscreenDialogAdapter.items = selectedFiles ?: listOf()
            }
        }

        btnSelectFolderInFullscreenDialog.setOnClickListener {
            FileChooserDialog().showSelectFolderFullscreenDialog(this, permissionsService) { _, selectedFile ->
                selectedFolderInFullscreenDialogAdapter.items = if (selectedFile != null) listOf(selectedFile) else listOf()
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsService.onRequestPermissionsResult(requestCode, permissions, grantResults)

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
