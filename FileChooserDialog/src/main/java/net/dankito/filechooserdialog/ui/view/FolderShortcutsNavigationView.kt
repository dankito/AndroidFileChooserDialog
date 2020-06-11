package net.dankito.filechooserdialog.ui.view

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.AttributeSet
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.file_chooser_dialog_dialog_file_chooser.view.*
import net.dankito.filechooserdialog.R
import net.dankito.utils.android.extensions.asActivity
import net.dankito.utils.android.extensions.createColorStateList
import net.dankito.utils.android.extensions.getResourceIdForAttributeId
import net.dankito.utils.android.io.AndroidFolderUtils
import net.dankito.utils.android.ui.view.IHandlesBackButtonPress
import java.io.File


open class FolderShortcutsNavigationView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NavigationView(context, attrs, defStyleAttr), IHandlesBackButtonPress {

    var folderShortcutSelectedListener: ((File) -> Unit)? = null


    private lateinit var drawerLayout: DrawerLayout

    private var sdCardDirectory: File? = null

    private val folderUtils = AndroidFolderUtils(context)


    init {
        setup()
    }


    protected open fun setup() {
        setupNavigationMenu()
    }

    protected open fun setupNavigationMenu() {
        this.itemIconTintList = context.createColorStateList(getIconsTintColorId())

        this.menu?.findItem(R.id.navFolderShortcutDocuments)?.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT // Documents folder is only available on KitKat and newer

        this.setNavigationItemSelectedListener { navigationItemSelected(it) }

        setExternalStorageMenuItems()
    }

    protected open fun getIconsTintColorId(): Int {
        return context.getResourceIdForAttributeId(R.attr.FileChooserDialogNavigationMenuItemsIconTintColor, R.color.colorAccent)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setupDrawerLayout() // now parent is set
    }

    protected open fun setupDrawerLayout() {
        drawerLayout = parent as DrawerLayout

        context.asActivity()?.let { activity ->
            val toggle = ActionBarDrawerToggle(
                    activity, drawerLayout, drawerLayout.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
        }
    }


    protected open fun setExternalStorageMenuItems() {
        folderUtils.findSdCardDirectory()?.let {
            sdCardDirectory = it
            this.menu?.findItem(R.id.navFolderShortcutSdCard)?.isVisible = true
        }
    }


    override fun handlesBackButtonPress(): Boolean {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawerLayout()

            return true
        }

        return false
    }


    protected open fun navigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.navFolderShortcutInternalStorage -> folderShortcutSelected(Environment.getExternalStorageDirectory())
            R.id.navFolderShortcutSdCard -> sdCardDirectory?.let { folderShortcutSelected(it) }
            R.id.navFolderShortcutDownloads -> folderShortcutSelected(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))
            R.id.navFolderShortcutCameraPhotos -> folderShortcutSelected(folderUtils.getCameraPhotosDirectory())
            R.id.navFolderShortcutPictures -> folderShortcutSelected(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))
            R.id.navFolderShortcutMusic -> folderShortcutSelected(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC))
            R.id.navFolderShortcutMovies -> folderShortcutSelected(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES))
            R.id.navFolderShortcutDocuments -> if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                folderShortcutSelected(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS))
        }

        closeDrawerLayout()
        return true
    }

    protected open fun folderShortcutSelected(directory: File) {
        folderShortcutSelectedListener?.invoke(directory)
    }


    protected open fun closeDrawerLayout() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

}