package com.cs160.finalproj.slientDisco.support.utils;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.cs160.finalproj.slientDisco.R;

public class UIUtils {

    public static void setBitmapCover(@NonNull ImageView imageView, @Nullable Bitmap trackCover) {
        if (trackCover != null) {
            imageView.setImageBitmap(trackCover);
        } else {
            imageView.setImageResource(R.drawable.ic_music_note);
        }
    }

    public static void setImageDrawable(@NonNull ImageView imageView, int drawable) {
        imageView.setImageResource(drawable);
    }

    public static void colorizeImage(@NonNull ImageView imageView, boolean asActive) {
        imageView.setColorFilter(ContextCompat.getColor(imageView.getContext(), asActive ? R.color.black : R.color.gray));
    }

}
