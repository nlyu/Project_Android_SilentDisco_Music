package com.cs160.finalproj.slientDisco.support;

import android.support.annotation.NonNull;

public enum MusicExtensions {

    _3GP(".3gp"),
    _MP4(".mp4"),
    _M4A(".m4a"),
    _AAC(".aac"),
    _TS(".ts"),
    _FLAC(".flac"),
    _XMF(".xmf"),
    _MXMF(".mxmf"),
    _RTTTL(".rtttl"),
    _RTX(".rtx"),
    _OTA(".ota"),
    _IMY(".imy"),
    _MP3(".mp3"),
    _MKV(".mkv"),
    _WAV(".wav"),
    _OGG(".ogg");

    private String extension;

    MusicExtensions(String extension) {
        this.extension = extension;
    }

    @NonNull
    public String getExtension() {
        return extension;
    }

}
