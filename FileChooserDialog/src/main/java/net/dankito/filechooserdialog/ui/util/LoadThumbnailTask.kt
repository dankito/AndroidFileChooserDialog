package net.dankito.filechooserdialog.ui.util

import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.widget.RecyclerView
import android.widget.ImageView
import net.dankito.filechooserdialog.service.ThumbnailService
import java.io.File


class LoadThumbnailTask(private val viewHolder: RecyclerView.ViewHolder, private val imageView: ImageView, private val file: File, private val mimeType: String,
                        private val thumbnailService: ThumbnailService,
                        private val previewImageCache: PreviewImageCache) : AsyncTask<Void, Void, Bitmap?>() {

    private val adapterPosition = viewHolder.adapterPosition


    override fun doInBackground(vararg p0: Void?): Bitmap? {
        return thumbnailService.getThumbnail(file, mimeType, 71, 40) // 71 : 40 = 16 : 9
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)

        if(result != null) {
            if(viewHolder.adapterPosition == adapterPosition) { // viewHolder still holds the same file
                imageView.clearColorFilter()
                imageView.setImageBitmap(result)
            }

            previewImageCache.cachePreviewImage(file, result)
        }
    }

}