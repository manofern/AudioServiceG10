package com.manofern.audioserviceaula;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import com.manofern.audioserviceaula.notifications.AudioNotification;
import com.manofern.audioserviceaula.services.AudioPlayer;
import com.manofern.audioserviceaula.services.AudioService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AudioServiceTest {

    @Mock
    Context mockContext;

    @Mock
    AudioPlayer mockAudioPlayer;

    @Mock
    AudioNotification mockAudioNotification;

    @Mock
    Intent mockIntent;

    @Mock
    Uri mockUri;

    private AudioService audioService;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        audioService = new AudioService(); // cria a instância da AudioService
        audioService.audioPlayer = mockAudioPlayer; // injeta o mockAudioPlayer
        audioService.audioNotification = mockAudioNotification; // injeta o mockAudioNotification
    }

    @Test
    public void testOnCreate() {
        // Testa o comportamento no onCreate()
        audioService.onCreate();

        // Verifica se o AudioPlayer e AudioNotification foram inicializados
        verify(mockAudioPlayer).getMediaSession();
        verify(mockAudioNotification).update("Aguardando reprodução");
    }

    @Test
    public void testOnStartCommandPlay() {
        // Simula a ação PLAY com um URI mockado
        when(mockIntent.getAction()).thenReturn("PLAY");
        when(mockIntent.getParcelableExtra("uri")).thenReturn(mockUri);

        // Chama o método onStartCommand
        audioService.onStartCommand(mockIntent, 0, 0);

        // Verifica se o método play foi chamado com o URI correto
        verify(mockAudioPlayer).play(mockUri);
        verify(mockAudioNotification).update("Reproduzindo...");
    }

    @Test
    public void testOnStartCommandPause() {
        // Simula a ação PAUSE
        when(mockIntent.getAction()).thenReturn("PAUSE");

        // Chama o método onStartCommand
        audioService.onStartCommand(mockIntent, 0, 0);

        // Verifica se o método pause foi chamado
        verify(mockAudioPlayer).pause();
        verify(mockAudioNotification).update("Pausado");
    }

    @Test
    public void testOnStartCommandStop() {
        // Simula a ação STOP
        when(mockIntent.getAction()).thenReturn("STOP");

        // Chama o método onStartCommand
        audioService.onStartCommand(mockIntent, 0, 0);

        // Verifica se o método stop foi chamado
        verify(mockAudioPlayer).stop();
    }

    @Test
    public void testOnDestroy() {
        // Chama o onDestroy() do serviço
        audioService.onDestroy();

        // Verifica se o método release() foi chamado para limpar os recursos do AudioPlayer
        verify(mockAudioPlayer).release();
    }

    @Test
    public void testOnBind() {
        // Verifica se o método onBind retorna null (pois o serviço não é ligado)
        IBinder binder = audioService.onBind(mockIntent);
        assert(binder == null);
    }
}
