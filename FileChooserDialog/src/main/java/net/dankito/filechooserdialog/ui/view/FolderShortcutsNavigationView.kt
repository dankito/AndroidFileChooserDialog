package net.dankito.filechooserdialog.ui.view

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Environment
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.github.mjdev.libaums.UsbMassStorageDevice
import kotlinx.android.synthetic.main.dialog_file_chooser.view.*
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.UsbFileWrapper
import net.dankito.filechooserdialog.ui.extensions.setItemsTintColor
import java.io.File


class FolderShortcutsNavigationView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : NavigationView(context, attrs, defStyleAttr) {

    companion object {
        private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    }


    var folderShortcutSelectedListener: ((File) -> Unit)? = null


    private lateinit var drawerLayout: DrawerLayout

    private var sdCardDirectory: File? = null


    init {
        setup()
    }


    private fun setup() {
        setupNavigationMenu()
    }

    private fun setupNavigationMenu() {
        this.menu.setItemsTintColor(context, R.color.file_chooser_dialog_navigation_menu_items_icon_tint_color)

        this.menu?.findItem(R.id.navFolderShortcutDocuments)?.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT // Documents folder is only available on KitKat and newer

        this.setNavigationItemSelectedListener { navigationItemSelected(it) }

        setExternalStorageMenuItems()

        setUsbDrivesMenuItems()
    }

    private fun setExternalStorageMenuItems() {
        val externalStorageDirectories = getExternalStorageDirectories()

        externalStorageDirectories.forEach { directory ->
            if(isMounted(directory)) {
                if(isSdCard(directory)) {
                    sdCardDirectory = directory // TODO: can there ever be more than one SD card?
                    this.menu?.findItem(R.id.navFolderShortcutSdCard)?.isVisible = true
                }
            }
        }
    }

    private fun getExternalStorageDirectories(): List<File> {
        val externalStorages = ContextCompat.getExternalFilesDirs(context, null)
        val externalStorageDirectories = externalStorages.map { getRootOfDirectory(it) }.filterNotNull().toMutableSet() // to set to avoid duplicates

        // for older Android devices
        System.getenv("SECONDARY_STORAGE")?.let { secondaryStorageValue ->
            val secondaryStoragePaths = secondaryStorageValue.split(':').map { it.replace(":", "") }

            secondaryStoragePaths.forEach { secondaryStoragePath ->
                val file = File(secondaryStoragePath)
                if(file.exists() && file.isDirectory && file.listFiles() != null) {
                    externalStorageDirectories.add(file)
                }
            }
        }

        return externalStorageDirectories.toList()
    }

    private fun isMounted(directory: File): Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return Environment.getStorageState(directory) == Environment.MEDIA_MOUNTED
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Environment.getExternalStorageState(directory) == Environment.MEDIA_MOUNTED
        }

        return true // TODO: what to return as fallback value for Android versions below KitKat?
    }

    private fun isSdCard(directory: File): Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val isRemovable = Environment.isExternalStorageRemovable(directory)
            val isEmulated = Environment.isExternalStorageEmulated(directory)

            if(isRemovable && isEmulated == false) { // an SD card
                return true
            }

            return false
        }
        else {
            return Environment.getExternalStorageDirectory() != directory
        }
    }

    private fun getRootOfDirectory(file: File?): File? {
        try {
            // Path is in format /storage.../Android....
            // Get everything before /Android
            if(file != null) {
                return File(file.path.split("/Android")[0])
            }
        } catch(e: Exception) {
            // TODO: log error
        }

        return null
    }

    private fun setUsbDrivesMenuItems() {
        try {
            val usbDevices = UsbMassStorageDevice.getMassStorageDevices(context)

            usbDevices.forEach { usbDevice ->
                val usbDeviceMenuItemTitle =
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && usbDevice.usbDevice.manufacturerName.isNullOrBlank() == false)
                            context.getString(R.string.folder_shortcut_usb_device_with_product_name_menu_item_title, usbDevice.usbDevice.deviceId, usbDevice.usbDevice
                                    .manufacturerName)
                        else context.getString(R.string.folder_shortcut_usb_device_menu_item_title, usbDevice.usbDevice.deviceId)

                this.menu?.add(usbDeviceMenuItemTitle)?.let { deviceMenuItem ->
                    deviceMenuItem.setOnMenuItemClickListener {
                        showUsbDevicePartitions(usbDevice)
                        true
                    }
                }
            }
        } catch(e: Exception) {
            // TODO: log error
        }
    }

    private fun showUsbDevicePartitions(usbDevice: UsbMassStorageDevice) {
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager

        val permissionIntent = PendingIntent.getBroadcast(context, 0, Intent(ACTION_USB_PERMISSION), 0)
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        context.registerReceiver(RequestUsbPermissionBroadcastReceiver({ isGranted -> showUsbDevicePartitions(usbDevice, isGranted) } ), filter)
        usbManager.requestPermission(usbDevice.usbDevice, permissionIntent)
    }

    private fun showUsbDevicePartitions(usbDevice: UsbMassStorageDevice, isPermissionGranted: Boolean) {
        if(isPermissionGranted) {
            showUsbDevicePartitionsWithGrantedPermissions(usbDevice)
        }
    }

    private fun showUsbDevicePartitionsWithGrantedPermissions(usbDevice: UsbMassStorageDevice) {
        usbDevice.init()

        if(usbDevice.partitions.isNotEmpty()) {
            val partitionFileSystem = usbDevice.partitions[0].fileSystem

            folderShortcutSelected(UsbFileWrapper(partitionFileSystem.rootDirectory))
        }
        else {
            Toast.makeText(context, R.string.mount_usb_mass_storage_error, Toast.LENGTH_LONG).show()
        }
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setupDrawerLayout() // now parent is set
    }

    private fun setupDrawerLayout() {
        drawerLayout = parent as DrawerLayout

        val toggle = ActionBarDrawerToggle(
                context as Activity, drawerLayout, drawerLayout.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
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
            R.id.navFolderShortcutCameraPhotos -> folderShortcutSelected(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM))
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


    inner class RequestUsbPermissionBroadcastReceiver(private val permissionResultReceived: (Boolean) -> Unit) : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if(ACTION_USB_PERMISSION == intent.action) {
                synchronized (this) {
                    val isPermissionGranted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)

                    permissionResultReceived(isPermissionGranted)
                }
            }
        }

    }

}