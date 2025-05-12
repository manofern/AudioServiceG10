package com.manofern.audioserviceaula;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.manofern.audioserviceaula.notifications.AudioNotification;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import android.support.v4.media.session.MediaSessionCompat;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AudioNotificationTest {

    @Mock
    Service mockService;

    @Mock
    NotificationManager mockNotificationManager;

    @Mock
    MediaSessionCompat mockMediaSession;

    private AudioNotification audioNotification;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Simular o retorno de NotificationManager
        when(mockService.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockNotificationManager);

        // Instanciar a classe com mocks
        audioNotification = new AudioNotification(mockService, mockMediaSession);
    }

    @Test
    public void testUpdateCallsStartForeground() {
        // Act
        audioNotification.update("Testando");

        // Verifica se startForeground foi chamado com qualquer notification
        verify(mockService).startForeground(eq(1), any(Notification.class));
    }

    @Test
    public void testNotificationChannelIsCreatedIfOreoOrAbove() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // Verifica se o canal foi criado
            verify(mockNotificationManager).createNotificationChannel(any());
        }
    }
}
