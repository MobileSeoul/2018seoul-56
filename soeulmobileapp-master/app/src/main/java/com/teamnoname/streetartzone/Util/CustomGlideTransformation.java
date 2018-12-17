package com.teamnoname.streetartzone.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;

import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

@GlideExtension
public class CustomGlideTransformation {

    private CustomGlideTransformation(){
    }

    @NonNull
    @GlideOption
    public static RequestOptions roundedCorners(RequestOptions options, @NonNull Context context, int cornerRadius) {
        int px = Math.round(cornerRadius * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return options.transforms(new RoundedCorners(px));
    }
}
