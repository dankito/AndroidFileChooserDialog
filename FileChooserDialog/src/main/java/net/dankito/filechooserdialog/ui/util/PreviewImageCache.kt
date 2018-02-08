package net.dankito.filechooserdialog.ui.util

import android.graphics.Bitmap
import java.io.File
import java.util.concurrent.ConcurrentHashMap


class PreviewImageCache {

    private val cache = ConcurrentHashMap<File, Bitmap>()


    fun getCachedPreviewImage(file: File): Bitmap? {
        return cache[file]
    }

    fun cachePreviewImage(file: File, previewImage: Bitmap) {
        cache.put(file, previewImage)
    }

}