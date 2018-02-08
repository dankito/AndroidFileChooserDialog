package net.dankito.filechooserdialog

import android.support.v4.app.FragmentManager
import net.dankito.filechooserdialog.ui.dialog.FullscreenFileChooserDialog
import java.io.File


class FileChooserDialog {

    companion object {

        fun showOpenSingleFileDialog(fragmentManager: FragmentManager, selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit): FullscreenFileChooserDialog {
            val dialog = FullscreenFileChooserDialog()

            dialog.showOpenSingleFileDialog(fragmentManager, selectSingleFileCallback)

            return dialog
        }

        fun showOpenMultipleFilesDialog(fragmentManager: FragmentManager, selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit): FullscreenFileChooserDialog {
            val dialog = FullscreenFileChooserDialog()

            dialog.showOpenMultipleFilesDialog(fragmentManager, selectMultipleFilesCallback)

            return dialog
        }

    }


}