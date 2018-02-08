package net.dankito.filechooserdialog.ui.util

import android.graphics.Bitmap
import android.os.AsyncTask
import net.dankito.filechooserdialog.service.ThumbnailService
import net.dankito.filechooserdialog.ui.adapter.viewholder.DirectoryContentViewHolder
import java.io.File


class LoadPreviewImageTask(private val viewHolder: DirectoryContentViewHolder, private val file: File, private val thumbnailService: ThumbnailService)
    : AsyncTask<Void, Void, Bitmap?>() {

    private val itemId = viewHolder.itemId


    override fun doInBackground(vararg p0: Void?): Bitmap? {
        return thumbnailService.getThumbnail(file, 71, 40)
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)

        if(viewHolder.itemId == itemId) { // viewHolder still holds the same file
            viewHolder.imgThumbnail.setImageBitmap(result)
        }
    }

}