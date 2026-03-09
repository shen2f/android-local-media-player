package com.shen.mediaplayer.utils.image

import android.widget.ImageView
import coil.load
import android.net.Uri

object ImageLoader {

    fun loadImage(
        imageView: ImageView,
        uri: Uri,
        cornerRadius: Float = 0f
    ) {
        imageView.load(uri) {
            if (cornerRadius > 0f) {
                transformations(coil.transform.RoundedCornersTransformation(cornerRadius * imageView.resources.displayMetrics.density))
            }
            crossfade(true)
        }
    }
}
