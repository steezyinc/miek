package co.steezy.miek.android

import android.content.Context
import android.util.AttributeSet

class CropViewConfig {
    companion object{
        const val DEFAULT_VIEWPORT_RATIO = 0f
        const val DEFAULT_MAXIMUM_SCALE = 10f
        const val DEFAULT_MINIMUM_SCALE = 0f
        const val DEFAULT_IMAGE_QUALITY = 100
        const val DEFAULT_VIEWPORT_OVERLAY_PADDING = 0
        const val DEFAULT_VIEWPORT_OVERLAY_COLOR = -0x38000000 // Black with 200 alpha
        const val DEFAULT_SHAPE = Shape.RECTANGLE

        @JvmStatic
        fun from(context: Context, attrs: AttributeSet?): CropViewConfig {
            val cropViewConfig = CropViewConfig()
            if (attrs == null) return cropViewConfig

            val attributes = context.obtainStyledAttributes(attrs, R.styleable.CropView)
            cropViewConfig.viewportRatio = attributes.getFloat(R.styleable.CropView_cropviewViewportRatio, DEFAULT_VIEWPORT_RATIO)
            cropViewConfig.maxScale = attributes.getFloat(R.styleable.CropView_cropviewMaxScale, DEFAULT_MAXIMUM_SCALE)
            cropViewConfig.minScale = attributes.getFloat(R.styleable.CropView_cropviewMinScale, DEFAULT_MINIMUM_SCALE)
            cropViewConfig.viewportOverlayColor = attributes.getColor(R.styleable.CropView_cropviewViewportOverlayColor, DEFAULT_VIEWPORT_OVERLAY_COLOR)
            cropViewConfig.viewportOverlayPadding = attributes.getDimensionPixelSize(R.styleable.CropView_cropviewViewportOverlayPadding, DEFAULT_VIEWPORT_OVERLAY_PADDING)
            @Shape
            cropViewConfig.shape = attributes.getInt(R.styleable.CropView_cropviewShape, DEFAULT_SHAPE)

            attributes.recycle()

            return cropViewConfig
        }
    }

    var viewportRatio = DEFAULT_VIEWPORT_RATIO
    var maxScale = DEFAULT_MAXIMUM_SCALE
    var minScale = DEFAULT_MINIMUM_SCALE
    var viewportOverlayPadding = DEFAULT_VIEWPORT_OVERLAY_PADDING
    var viewportOverlayColor = DEFAULT_VIEWPORT_OVERLAY_COLOR

    @Shape
    var shape = DEFAULT_SHAPE
}