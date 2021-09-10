package co.steezy.miek.android

import android.widget.ImageView
import co.steezy.miek.android.interfaces.BitmapLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions

/**
 * A [BitmapLoader] with transformation for [Glide] image library.
 *
 * @see GlideBitmapLoader.createUsing
 */
class GlideBitmapLoader(private val requestManager: RequestManager, private val transformation: BitmapTransformation) : BitmapLoader {
    companion object {
        @JvmStatic
        fun createUsing(cropView: CropView): BitmapLoader {
            return createUsing(cropView, Glide.with(cropView.context))
        }

        @JvmName("createUsing1")
        @JvmOverloads
        fun createUsing(cropView: CropView, requestManager: RequestManager = Glide.with(cropView.context)): BitmapLoader {
            return GlideBitmapLoader(requestManager,
                GlideFillViewportTransformation.createUsing(cropView.viewportWidth, cropView.viewportHeight))
        }
    }

    override fun load(model: Any?, view: ImageView) {
        val requestOptions = RequestOptions()
        requestOptions.skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .transform(transformation)
        requestManager.asBitmap()
            .load(model)
            .apply(requestOptions)
    }
}