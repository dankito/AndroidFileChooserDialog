package net.dankito.filechooserdialog

import android.support.v4.app.FragmentActivity
import net.dankito.filechooserdialog.model.Options
import net.dankito.filechooserdialog.ui.dialog.AlertFileChooserDialog
import net.dankito.filechooserdialog.ui.dialog.FullscreenFileChooserDialog
import net.dankito.filechooserdialog.ui.dialog.IFileChooserDialog
import java.io.File


class FileChooserDialog {

    companion object {

        @JvmOverloads
        fun showOpenSingleFileDialog(activity: FragmentActivity, options: Options = Options(), selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit):
                IFileChooserDialog {
            val dialog = AlertFileChooserDialog()

            dialog.showOpenSingleFileDialog(activity, options, selectSingleFileCallback)

            return dialog
        }

        @JvmOverloads
        fun showOpenMultipleFilesDialog(activity: FragmentActivity, options: Options = Options(), selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit): IFileChooserDialog {
            val dialog = AlertFileChooserDialog()

            dialog.showOpenMultipleFilesDialog(activity, options, selectMultipleFilesCallback)

            return dialog
        }

        @JvmOverloads
        fun showOpenSingleFileDialogInFullscreen(activity: FragmentActivity, options: Options = Options(), selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit): IFileChooserDialog {
            val dialog = FullscreenFileChooserDialog()

            dialog.showOpenSingleFileDialog(activity, options, selectSingleFileCallback)

            return dialog
        }

        @JvmOverloads
        fun showOpenMultipleFilesDialogInFullscreen(activity: FragmentActivity, options: Options = Options(), selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit): IFileChooserDialog {
            val dialog = FullscreenFileChooserDialog()

            dialog.showOpenMultipleFilesDialog(activity, options, selectMultipleFilesCallback)

            return dialog
        }

    }


}