package com.sridhar.telematics.assessment.utils

import android.view.View
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("imageUrl")
fun loadImage(view: View, imageUrl: String) {
    val imageView: CircleImageView = view as CircleImageView
    Glide.with(imageView.context).load(imageUrl).into(imageView)
}