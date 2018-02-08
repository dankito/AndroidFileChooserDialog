package net.dankito.filechooserdialog

import android.support.v4.app.FragmentActivity
import net.dankito.filechooserdialog.ui.dialog.FullscreenFileChooserDialog
import java.io.File


class FileChooserDialog {

    companion object {

        fun showOpenSingleFileDialog(activity: FragmentActivity, selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit): FullscreenFileChooserDialog {
            val dialog = FullscreenFileChooserDialog()

            dialog.showOpenSingleFileDialog(activity, selectSingleFileCallback)

            return dialog
        }

        fun showOpenMultipleFilesDialog(activity: FragmentActivity, selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit): FullscreenFileChooserDialog {
            val dialog = FullscreenFileChooserDialog()

            dialog.showOpenMultipleFilesDialog(activity, selectMultipleFilesCallback)

            return dialog
        }

    }


}