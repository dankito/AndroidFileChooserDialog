package net.dankito.filechooserdialog.ui.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.support.v4.content.ContextCompat
import java.io.File


class FolderUtils(private val context: Context) {

    /**
     * Tries to find Camera folder in DCIM dir
     */
    fun getCameraPhotosDirectory(): File {
        val dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)

        var cameraInDcimDirTry = File(dcimDir, "Camera")
        if(cameraInDcimDirTry.exists()) {
            return cameraInDcimDirTry
        }

        cameraInDcimDirTry = File(dcimDir, "CAMERA")
        if(cameraInDcimDirTry.exists()) {
            return cameraInDcimDirTry
        }

        cameraInDcimDirTry = File(dcimDir, "camera")
        if(cameraInDcimDirTry.exists()) {
            return cameraInDcimDirTry
        }

        return dcimDir
    }


    fun findSdCardDirectory(): File? {
        val externalStorageDirectories = getExternalStorageDirectories()

        externalStorageDirectories.forEach { directory ->
            if(isMounted(directory)) {
                if(isSdCard(directory)) {
                    return directory
                }
            }
        }

        return null
    }

    fun getExternalStorageDirectories(): List<File> {
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

    fun isMounted(directory: File): Boolean {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            @Suppress("DEPRECATION")
            return Environment.getStorageState(directory) == Environment.MEDIA_MOUNTED
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Environment.getExternalStorageState(directory) == Environment.MEDIA_MOUNTED
        }

        return true // TODO: what to return as fallback value for Android versions below KitKat?
    }

    fun isSdCard(directory: File): Boolean {
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

    fun getRootOfDirectory(file: File?): File? {
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

}