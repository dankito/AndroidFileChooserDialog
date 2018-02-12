package net.dankito.filechooserdialog.demo

import android.os.Build
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import net.dankito.deepthought.android.service.permissions.PermissionsManager
import net.dankito.filechooserdialog.FileChooserDialog
import net.dankito.filechooserdialog.model.FileChooserDialogType
import net.dankito.filechooserdialog.service.MimeTypeService
import net.dankito.filechooserdialog.service.PreviewImageService
import net.dankito.filechooserdialog.service.SelectedFilesManager
import net.dankito.filechooserdialog.service.ThumbnailService
import net.dankito.filechooserdialog.ui.adapter.DirectoryContentAdapter


class MainActivity : AppCompatActivity() {

    private val permissionsManager = PermissionsManager(this)

    private val mimeTypeService = MimeTypeService()

    private val thumbnailService = ThumbnailService(this, mimeTypeService)

    private val previewImageService = PreviewImageService(thumbnailService, mimeTypeService)

    private val selectedSingleFileAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectSingleFile))

    private val selectedMultipleFilesAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectMultipleFiles))

    private val selectedSingleFileInFullscreenDialogAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectSingleFile))

    private val selectedMultipleFilesInFullscreenDialogAdapter = DirectoryContentAdapter(previewImageService, SelectedFilesManager(FileChooserDialogType.SelectMultipleFiles))


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

        rcySelectedSingleFileInFullscreenDialog.adapter = selectedSingleFileInFullscreenDialogAdapter
        rcySelectedMultipleFilesInFullscreenDialog.adapter = selectedMultipleFilesInFullscreenDialogAdapter


        setupShowFileChooserDialogButtons()
    }

    private fun setupShowFileChooserDialogButtons() {
        btnSelectSingleFile.setOnClickListener {
            FileChooserDialog().showOpenSingleFileDialog(this, permissionsManager) { didUserSelectFile, selectedFile ->
                selectedSingleFileAdapter.items = if (selectedFile != null) listOf(selectedFile) else listOf()
            }
        }

        btnSelectMultipleFiles.setOnClickListener {
            FileChooserDialog().showOpenMultipleFilesDialog(this, permissionsManager) { didUserSelectFiles, selectedFiles ->
                selectedMultipleFilesAdapter.items = selectedFiles ?: listOf()
            }
        }

        btnSelectSingleFileInFullscreenDialog.setOnClickListener {
            FileChooserDialog().showOpenSingleFileFullscreenDialog(this, permissionsManager) { didUserSelectFile, selectedFile ->
                selectedSingleFileInFullscreenDialogAdapter.items = if (selectedFile != null) listOf(selectedFile) else listOf()
            }
        }

        btnSelectMultipleFilesInFullscreenDialog.setOnClickListener {
            FileChooserDialog().showOpenMultipleFilesFullscreenDialog(this, permissionsManager) { didUserSelectFiles, selectedFiles ->
                selectedMultipleFilesInFullscreenDialogAdapter.items = selectedFiles ?: listOf()
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
