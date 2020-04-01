package com.wangrui.imagee.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wangrui.imagee.R;

/**
 * created by WR
 * 时间：2019-08-16 18:34
 */
public class ImageLoader {

    public static void image(ImageView imageView, String url, int placeholder) {
        if (url.contains(".gif")) {
            gif(imageView, url, placeholder);
        } else {
            Glide.with(imageView.getContext())
                    .load(url)
                    .error(placeholder)
                    .placeholder(placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    }

    public static void gif(ImageView imageView, String url, int placeholder) {
        Glide.with(imageView.getContext())
                .asGif()
                .load(url)
                .placeholder(placeholder)
                .into(imageView);
    }

    public static void imageSmall(ImageView imageView, String url) {
        image(imageView, url, R.drawable.ic_image_holder_small);
    }
}
