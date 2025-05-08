package com.manofern.audioserviceaula.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.manofern.audioserviceaula.notifications.AudioNotification;

import java.util.Objects;

public class AudioService extends Service {

    private AudioPlayer audioPlayer;
    private AudioNotification audioNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        audioPlayer = new AudioPlayer(this);
        audioNotification = new AudioNotification(this, audioPlayer.getMediaSession());
        audioNotification.update("Aguardando reprodução");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            switch (Objects.requireNonNull(action)) {
                case "PLAY":
                    audioPlayer.play(intent.getStringExtra("path"));
                    audioNotification.update("Reproduzindo...");
                    break;
                case "PAUSE":
                    audioPlayer.pause();
                    audioNotification.update("Pausado");
                    break;
                case "STOP":
                    audioPlayer.stop();
                    stopForeground(true);
                    stopSelf();
                    break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioPlayer.release();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
