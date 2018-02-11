package net.dankito.filechooserdialog.ui.util

import android.graphics.Bitmap
import android.os.AsyncTask
import net.dankito.filechooserdialog.service.ThumbnailService
import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
import java.io.File


class LoadThumbnailTask(private val viewHolder: DirectoryContentViewHolder, private val file: File, private val mimeType: String, private val thumbnailService: ThumbnailService,
                        private val previewImageCache: PreviewImageCache) : AsyncTask<Void, Void, Bitmap?>() {

    private val adapterPosition = viewHolder.adapterPosition


    override fun doInBackground(vararg p0: Void?): Bitmap? {
        return thumbnailService.getThumbnail(file, mimeType, 71, 40) // 71 : 40 = 16 : 9
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)

        if(result != null) {
            if(viewHolder.adapterPosition == adapterPosition) { // viewHolder still holds the same file
                viewHolder.imgPreviewImage.setImageBitmap(result)
            }

            previewImageCache.cachePreviewImage(file, result)
        }
    }

}