package net.dankito.filechooserdialog.ui.view

import android.content.Context
import android.os.Build
import android.os.Environment
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.AttributeSet
import android.view.MenuItem
import kotlinx.android.synthetic.main.file_chooser_dialog_dialog_file_chooser.view.*
import net.dankito.filechooserdialog.R
import net.dankito.utils.android.extensions.asActivity
import net.dankito.utils.android.extensions.createColorStateList
import net.dankito.utils.android.io.AndroidFolderUtils
import java.io.File


class FolderShortcutsNavigationView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NavigationView(context, attrs, defStyleAttr) {

    var folderShortcutSelectedListener: ((File) -> Unit)? = null


    private lateinit var drawerLayout: DrawerLayout

    private var sdCardDirectory: File? = null

    private val folderUtils = AndroidFolderUtils(context)


    init {
        setup()
    }


    private fun setup() {
        setupNavigationMenu()
    }

    private fun setupNavigationMenu() {
        this.itemIconTintList = context.createColorStateList(R.color.file_chooser_dialog_navigation_menu_items_icon_tint_color)

        this.menu?.findItem(R.id.navFolderShortcutDocuments)?.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT // Documents folder is only available on KitKat and newer

        this.setNavigationItemSelectedListener { navigationItemSelected(it) }

        setExternalStorageMenuItems()
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setupDrawerLayout() // now parent is set
    }

    private fun setupDrawerLayout() {
        drawerLayout = parent as DrawerLayout

        context.asActivity()?.let { activity ->
            val toggle = ActionBarDrawerToggle(
                    activity, drawerLayout, drawerLayout.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
        }
    }


    private fun setExternalStorageMenuItems() {
        folderUtils.findSdCardDirectory()?.let {
            sdCardDirectory = it
            this.menu?.findItem(R.id.navFolderShortcutSdCard)?.isVisible = true
        }
    }


    fun handlesBackButtonPress(): Boolean {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            closeDrawerLayout()

            return true
        }

        return false
    }


    private fun navigationItemSelected(item: MenuItem): Boolean {
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

    private fun folderShortcutSelected(directory: File) {
        folderShortcutSelectedListener?.invoke(directory)
    }


    private fun closeDrawerLayout() {
        drawerLayout.closeDrawer(GravityCompat.START)
    }

}