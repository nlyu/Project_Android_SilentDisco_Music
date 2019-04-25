package com.cs160.finalproj.slientDisco.support.utils;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.cs160.finalproj.slientDisco.support.MusicExtensions;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class PlayerUtils {

    public static boolean isMusicFile(@NonNull File file) {
        for (MusicExtensions musicExtension: MusicExtensions.values()) {
            if (file.getAbsolutePath().endsWith(musicExtension.getExtension())) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("DefaultLocale")
    public static String toMinutes(int trackTime) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(trackTime),
                TimeUnit.MILLISECONDS.toSeconds(trackTime) % TimeUnit.MINUTES.toSeconds(1));
    }

    public static int toSeconds(int trackTime) {
        return (int) (TimeUnit.MILLISECONDS.toSeconds(trackTime));
    }

    public static int toMilliseconds(int trackTime) {
        return (int) TimeUnit.SECONDS.toMillis(trackTime);
    }

}
