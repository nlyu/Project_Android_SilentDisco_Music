package com.cs160.finalproj.slientDisco.player;

import android.support.annotation.NonNull;

public class Track {

    private String title;

    public String getTitle() {
        return title;
    }

    Track(@NonNull String title) {
        this.title = title;
    }

}
