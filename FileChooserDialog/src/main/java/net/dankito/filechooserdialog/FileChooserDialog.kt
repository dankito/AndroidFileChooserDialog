package net.dankito.filechooserdialog

import android.support.v4.app.FragmentActivity
import net.dankito.filechooserdialog.ui.dialog.AlertFileChooserDialog
import net.dankito.filechooserdialog.ui.dialog.FullscreenFileChooserDialog
import net.dankito.filechooserdialog.ui.dialog.IFileChooserDialog
import java.io.File


class FileChooserDialog {

    companion object {

        fun showOpenSingleFileDialog(activity: FragmentActivity, selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit): IFileChooserDialog {
            val dialog = AlertFileChooserDialog()

            dialog.showOpenSingleFileDialog(activity, selectSingleFileCallback)

            return dialog
        }

        fun showOpenMultipleFilesDialog(activity: FragmentActivity, selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit): IFileChooserDialog {
            val dialog = AlertFileChooserDialog()

            dialog.showOpenMultipleFilesDialog(activity, selectMultipleFilesCallback)

            return dialog
        }

        fun showOpenSingleFileDialogInFullscreen(activity: FragmentActivity, selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit): IFileChooserDialog {
            val dialog = FullscreenFileChooserDialog()

            dialog.showOpenSingleFileDialog(activity, selectSingleFileCallback)

            return dialog
        }

        fun showOpenMultipleFilesDialogInFullscreen(activity: FragmentActivity, selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit): IFileChooserDialog {
            val dialog = FullscreenFileChooserDialog()

            dialog.showOpenMultipleFilesDialog(activity, selectMultipleFilesCallback)

            return dialog
        }

    }


}