package com.manofern.audioserviceaula.services;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;

public class AudioPlayer {
    private final Context context;
    private final MediaPlayer mediaPlayer;
    private final MediaSessionCompat mediaSession;
    private Uri lastAudioUri;
    private int currentPosition = 0;

    // Construtor usado na aplicação real
    public AudioPlayer(Context context) {
        this(context, new MediaPlayer(), new MediaSessionCompat(context, "AudioPlayer"));
    }

    // Construtor para testes com mocks
    public AudioPlayer(Context context, MediaPlayer mediaPlayer, MediaSessionCompat mediaSession) {
        this.context = context;
        this.mediaPlayer = mediaPlayer;
        this.mediaSession = mediaSession;
    }

    // Método atualizado para receber Uri diretamente
    public void play(Uri audioUri) {
        try {
            if (audioUri != null) lastAudioUri = audioUri;

            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, lastAudioUri);
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
