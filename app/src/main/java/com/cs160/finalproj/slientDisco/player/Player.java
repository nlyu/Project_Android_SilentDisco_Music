package com.cs160.finalproj.slientDisco.player;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.cs160.finalproj.slientDisco.support.utils.PlayerUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String MUSIC_FOLDER_PATH = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            .getPath() + "/";

    private MediaPlayer defaultMediaPlayer;

    private int currentTime;
    private boolean isPausing = true;
    private boolean endPlaying = false;

    private static Player playerInstance;
    private static final Object LOCK = new Object();

    private Player() {
        defaultMediaPlayer = new MediaPlayer();
        defaultMediaPlayer.setOnCompletionListener(mp -> endPlaying = true);
    }

    public static Player getInstance() {
        if (playerInstance == null) {
            synchronized (LOCK) {
                playerInstance = new Player();
            }
        }
        return playerInstance;
    }

    public void play(@NonNull Track track) {
        if (!defaultMediaPlayer.isPlaying()) {
            try {
                System.out.println("log,,,,,,,:" + MUSIC_FOLDER_PATH);
                FileInputStream fileInputStream = new FileInputStream(new File(MUSIC_FOLDER_PATH
                        + track.getTitle()));

                defaultMediaPlayer.reset();
                defaultMediaPlayer.setDataSource(fileInputStream.getFD());
                defaultMediaPlayer.prepare();
                defaultMediaPlayer.start();

                isPausing = false;
                endPlaying = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void resume() {
            isPausing = false;
    }

    public void pause() {
            isPausing = true;
    }

    public void stop() {
        if (defaultMediaPlayer.isPlaying()) {
            defaultMediaPlayer.stop();
        }
    }

    public void toTime(int time) {
        currentTime = time;
        defaultMediaPlayer.seekTo(time);
    }

    public int getTrackTimePosition() {
        return defaultMediaPlayer.getCurrentPosition();
    }

    public int getTrackEndTime() {
        return defaultMediaPlayer.getDuration();
    }

    public List<Track> checkDirectory() {
        List<Track> tracks = new ArrayList<>();
        System.out.println("CheckDir: " + MUSIC_FOLDER_PATH);
        File[] musicDirectoryList = new File(MUSIC_FOLDER_PATH).listFiles();

        if (musicDirectoryList != null) {
            for (File track : musicDirectoryList) {
                if (PlayerUtils.isMusicFile(track)) {
                    tracks.add(new Track(track.getName()));
                }
            }
        }
        return tracks;
    }

    public Bitmap getCover(@NonNull Track track, @NonNull Context context) {
        Bitmap cover;
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        byte[] rawCover;
        BitmapFactory.Options options = new BitmapFactory.Options();
        mediaMetadataRetriever.setDataSource(context, Uri.fromFile(new File(MUSIC_FOLDER_PATH + track.getTitle())));
        rawCover = mediaMetadataRetriever.getEmbeddedPicture();
        if (null != rawCover) {
            cover = BitmapFactory.decodeByteArray(rawCover, 0, rawCover.length, options);
            return cover;
        }
        return null;
    }

    public boolean isPausing() {
        return isPausing;
    }

    public boolean endPlaying() {
        return endPlaying;
    }

}
