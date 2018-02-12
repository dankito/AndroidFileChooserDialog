package net.dankito.filechooserdialog.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import android.os.Build
import android.view.Menu
import android.widget.Toast
import com.github.mjdev.libaums.UsbMassStorageDevice
import net.dankito.filechooserdialog.R
import net.dankito.filechooserdialog.model.UsbFileWrapper


class UsbMassStorageService(private val context: Context) {

    companion object {
        private const val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
    }


    fun setUsbDrivesMenuItems(menu: Menu) {
        try {
            val usbDevices = UsbMassStorageDevice.getMassStorageDevices(context)

            usbDevices.forEach { usbDevice ->
                val usbDeviceMenuItemTitle =
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && usbDevice.usbDevice.manufacturerName.isNullOrBlank() == false)
                            context.getString(R.string.folder_shortcut_usb_device_with_product_name_menu_item_title, usbDevice.usbDevice.deviceId, usbDevice.usbDevice
                                    .manufacturerName)
                        else context.getString(R.string.folder_shortcut_usb_device_menu_item_title, usbDevice.usbDevice.deviceId)

                menu.add(usbDeviceMenuItemTitle)?.let { deviceMenuItem ->
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

            showUsbDevicePartitionToUser(UsbFileWrapper(partitionFileSystem.rootDirectory))
        }
        else {
            Toast.makeText(context, R.string.mount_usb_mass_storage_error, Toast.LENGTH_LONG).show()
        }
    }

    private fun showUsbDevicePartitionToUser(usbFileWrapper: UsbFileWrapper) {
        // TODO
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