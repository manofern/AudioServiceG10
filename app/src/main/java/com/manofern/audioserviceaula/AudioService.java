package com.manofern.audioserviceaula;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.support.v4.media.session.MediaSessionCompat;

import java.util.Objects;

public class AudioService extends Service {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private MediaSessionCompat mediaSession;
    private int currentPosition = 0;
    private String lastAudioPath = null;


    private static final String CHANNEL_ID = "AudioServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mediaSession = new MediaSessionCompat(this, "AudioService");

        createNotificationChannel();

        // Cria a notificação inicial e inicia em primeiro plano
        updateNotification("Aguardando reprodução");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null) {
            String action = intent.getAction();

            switch (Objects.requireNonNull(action)) {
                case "PLAY": {
                    String path = intent.getStringExtra("path");
                    playAudio(path); // pode ser null se vier da notificação
                    break;
                }
                case "PAUSE": {
                    pauseAudio();
                    break;
                }
                case "STOP": {
                    stopAudio();
                    break;
                }
            }
        }

        return START_STICKY;
    }


    private void playAudio(String uriString) {
        try {
            if (uriString != null) {
                lastAudioPath = uriString;
            }

            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, Uri.parse(lastAudioPath));
            mediaPlayer.prepare();

            if (currentPosition > 0) {
                mediaPlayer.seekTo(currentPosition);
            }

            mediaPlayer.start();
            updateNotification("Reproduzindo...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            updateNotification("Pausado");
        }
    }


    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        stopForeground(true);
        stopSelf();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaSession != null) {
            mediaSession.release();
            mediaSession = null;
        }
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Audio Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setDescription("Canal de serviço de reprodução de áudio");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    private void updateNotification(String statusText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(statusText)
                .setContentText("Clique para voltar ao app")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mediaSession.getSessionToken()));

        // Ação STOP (sempre presente)
        Intent stopIntent = new Intent(this, AudioService.class);
        stopIntent.setAction("STOP");
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this, 2, stopIntent, PendingIntent.FLAG_IMMUTABLE
        );

        // Verifica se o mediaPlayer está tocando
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            // Ação PAUSE
            Intent pauseIntent = new Intent(this, AudioService.class);
            pauseIntent.setAction("PAUSE");
            PendingIntent pausePendingIntent = PendingIntent.getService(
                    this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE
            );

            builder.addAction(new NotificationCompat.Action(
                    android.R.drawable.ic_media_pause, "Pausar", pausePendingIntent));
        } else {
            // Ação PLAY
            Intent playIntent = new Intent(this, AudioService.class);
            playIntent.setAction("PLAY");
            PendingIntent playPendingIntent = PendingIntent.getService(
                    this, 3, playIntent, PendingIntent.FLAG_IMMUTABLE
            );

            builder.addAction(new NotificationCompat.Action(
                    android.R.drawable.ic_media_play, "Reproduzir", playPendingIntent));
        }

        builder.addAction(new NotificationCompat.Action(
                android.R.drawable.ic_menu_close_clear_cancel, "Parar", stopPendingIntent));

        Notification notification = builder.build();
        startForeground(1, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
