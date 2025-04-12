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

import android.support.v4.media.session.MediaSessionCompat;

import java.util.Objects;

public class AudioService extends Service {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private MediaSessionCompat mediaSession;
    private Notification notification;
    private int currentPosition = 0;

    private static final String CHANNEL_ID = "AudioServiceChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mediaSession = new MediaSessionCompat(this,"AudioService");

        createNotificationChannel();

        notification = createNotification();

        startForeground(1,notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intent != null) {
            String action = intent.getAction();

            switch (Objects.requireNonNull(action)) {
                case "PLAY": {
                    playAudio(intent.getStringExtra("path"));
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
                default:
                    break;
            }
        }

        return START_STICKY;
    }

    private void playAudio(String uriString){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, Uri.parse(uriString));
            mediaPlayer.prepare();

            if (currentPosition > 0){
                mediaPlayer.seekTo(currentPosition); // Retoma onde parou
            }

            mediaPlayer.start();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void pauseAudio(){
        if (mediaPlayer.isPlaying())
            currentPosition = mediaPlayer.getCurrentPosition(); // Salva a posicao atual
            mediaPlayer.pause();
    }

    private void stopAudio(){
        mediaPlayer.stop();
        stopForeground(true);
        stopSelf();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        mediaPlayer = null;
        mediaSession.release();
        mediaSession = null;
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Audio Service Channel",
                    NotificationManager.IMPORTANCE_LOW // Use LOW para notificações persistentes sem som
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );

        // Ações da notificação
        Intent pauseIntent = new Intent(this, AudioService.class);
        pauseIntent.setAction("PAUSE");
        PendingIntent pausePendingIntent = PendingIntent.getService(
                this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE
        );

        Intent stopIntent = new Intent(this, AudioService.class);
        stopIntent.setAction("STOP");
        PendingIntent stopPendingIntent = PendingIntent.getService(
                this, 2, stopIntent, PendingIntent.FLAG_IMMUTABLE
        );

        return new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Reproduzindo música")
                .setContentText("Clique para voltar ao app")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(contentIntent)
                .addAction(new Notification.Action.Builder(
                        android.R.drawable.ic_media_pause, "Pausar", pausePendingIntent).build())
                .addAction(new Notification.Action.Builder(
                        android.R.drawable.ic_menu_close_clear_cancel, "Parar", stopPendingIntent).build())
                .setOngoing(true) // Notificação persistente
                .setStyle(new Notification.MediaStyle())
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
