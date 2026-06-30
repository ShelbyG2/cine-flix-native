package tech.unrealistic.cineflix.helpers

import android.content.Context
import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.request.bitmapConfig
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object PaletteHelper {

    suspend fun extractPalette (context: Context, url: String): Palette?=
        withContext(Dispatchers.IO){
            try {
                val request = ImageRequest.Builder(context)
                    .data(url)
                    .bitmapConfig(Bitmap.Config.ARGB_8888)
                    .allowHardware(false)
                    .size(64,96)
                    .build()

                val bitmap =context.imageLoader
                    .execute(request)
                    .image
                    ?.toBitmap()
                    ?: return@withContext null
                Palette.from(bitmap).maximumColorCount(16).generate()
            }catch (e: Exception){
                null
            }
        }


}