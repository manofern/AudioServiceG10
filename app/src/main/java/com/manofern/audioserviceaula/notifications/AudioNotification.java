package com.manofern.audioserviceaula.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.manofern.audioserviceaula.R;
import com.manofern.audioserviceaula.services.AudioService;
import com.manofern.audioserviceaula.ui.MainActivity;

import android.support.v4.media.session.MediaSessionCompat;

public class AudioNotification {

    private final Context context;
    private final NotificationManager manager;
    private final MediaSessionCompat mediaSession;
    private static final String CHANNEL_ID = "AudioServiceChannel";

    public AudioNotification(Context context, MediaSessionCompat mediaSession) {
        this.context = context;
        this.mediaSession = mediaSession;
        this.manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    public void update(String statusText) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(statusText)
                .setContentText("Clique para voltar ao app")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.getSessionToken()));

        builder.addAction(new NotificationCompat.Action(
                android.R.drawable.ic_media_play, "Reproduzir", createServicePendingIntent("PLAY")));
        builder.addAction(new NotificationCompat.Action(
                android.R.drawable.ic_media_pause, "Pausar", createServicePendingIntent("PAUSE")));
        builder.addAction(new NotificationCompat.Action(
                android.R.drawable.ic_menu_close_clear_cancel, "Parar", createServicePendingIntent("STOP")));

        Notification notification = builder.build();
        if (context instanceof Service) {
            ((Service) context).startForeground(1, notification);
        }
    }

    private PendingIntent createServicePendingIntent(String action) {
        Intent intent = new Intent(context, AudioService.class);
        intent.setAction(action);
        return PendingIntent.getService(context, action.hashCode(), intent, PendingIntent.FLAG_IMMUTABLE);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID, "Audio Service Channel", NotificationManager.IMPORTANCE_LOW
            );
            serviceChannel.setDescription("Canal de serviço de reprodução de áudio");
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
