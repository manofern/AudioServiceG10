package com.manofern.audioserviceaula.services;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import android.support.v4.media.session.MediaSessionCompat;

public class AudioPlayer {
    private final Context context;
    private final MediaPlayer mediaPlayer;
    private final MediaSessionCompat mediaSession;
    private String lastAudioPath;
    private int currentPosition = 0;

    public AudioPlayer(Context context) {
        this.context = context;
        this.mediaPlayer = new MediaPlayer();
        this.mediaSession = new MediaSessionCompat(context, "AudioPlayer");
    }

    public void play(String uriString) {
        try {
            if (uriString != null) lastAudioPath = uriString;

            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, Uri.parse(lastAudioPath));
            mediaPlayer.prepare();

            if (currentPosition > 0) {
                mediaPlayer.seekTo(currentPosition);
            }

            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }

    public void stop() {
        mediaPlayer.stop();
    }

    public void release() {
        mediaPlayer.release();
        mediaSession.release();
    }

    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    public MediaSessionCompat getMediaSession() {
        return mediaSession;
    }
}
