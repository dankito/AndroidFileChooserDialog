package net.dankito.filechooserdialog

import android.support.v4.app.FragmentActivity
import net.dankito.deepthought.android.service.permissions.IPermissionsManager
import net.dankito.filechooserdialog.model.FileChooserDialogConfig
import net.dankito.filechooserdialog.ui.dialog.AlertFileChooserDialog
import net.dankito.filechooserdialog.ui.dialog.FullscreenFileChooserDialog
import net.dankito.filechooserdialog.ui.dialog.IFileChooserDialog
import java.io.File


class FileChooserDialog {

    companion object {

        @JvmOverloads
        fun showOpenSingleFileDialog(activity: FragmentActivity, permissionsManager: IPermissionsManager? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                     selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit):
                IFileChooserDialog {
            val dialog = AlertFileChooserDialog()

            dialog.showOpenSingleFileDialog(activity, permissionsManager, config, selectSingleFileCallback)

            return dialog
        }

        @JvmOverloads
        fun showOpenMultipleFilesDialog(activity: FragmentActivity, permissionsManager: IPermissionsManager? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                        selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit): IFileChooserDialog {
            val dialog = AlertFileChooserDialog()

            dialog.showOpenMultipleFilesDialog(activity, permissionsManager, config, selectMultipleFilesCallback)

            return dialog
        }

        @JvmOverloads
        fun showOpenSingleFileDialogInFullscreen(activity: FragmentActivity, permissionsManager: IPermissionsManager? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                                 selectSingleFileCallback: (didUserSelectFile: Boolean, File?) -> Unit): IFileChooserDialog {
            val dialog = FullscreenFileChooserDialog()

            dialog.showOpenSingleFileDialog(activity, permissionsManager, config, selectSingleFileCallback)

            return dialog
        }

        @JvmOverloads

        fun showOpenMultipleFilesDialogInFullscreen(activity: FragmentActivity, permissionsManager: IPermissionsManager? = null, config: FileChooserDialogConfig = FileChooserDialogConfig(),
                                                    selectMultipleFilesCallback: (didUserSelectFiles: Boolean, List<File>?) -> Unit): IFileChooserDialog {
            val dialog = FullscreenFileChooserDialog()

            dialog.showOpenMultipleFilesDialog(activity, permissionsManager, config, selectMultipleFilesCallback)

            return dialog
        }

    }


}