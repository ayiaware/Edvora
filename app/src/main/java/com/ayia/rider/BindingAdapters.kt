package com.ayia.rider

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation


@BindingAdapter("app:setUserImage")
fun setUserImage(iv:ImageView, url:String?){
    Picasso.get()
        .load(url)
        .error(R.drawable.background_user_error_image)
        .placeholder(R.drawable.background_user_default_image)
        .transform(CircleTransformation())
        .into(iv)
}


@BindingAdapter("app:setMapImage")
fun setMapImage(iv:ImageView, url:String?){
    Picasso.get()
        .load(url)
        .error(R.drawable.background_map_error_image)
        .placeholder(R.drawable.background_map_default_image)
        .transform(RoundedCornersTransformation(5,0))
        .into(iv)
}


@BindingAdapter("app:isNotGone")
fun isNotGone(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}

