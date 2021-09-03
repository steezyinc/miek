package co.steezy.miek.android

import android.widget.ImageView

interface BitmapLoader {
    fun load(model: Any?, view: ImageView)
}