package com.manofern.audioserviceaula;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;

import com.manofern.audioserviceaula.services.AudioPlayer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AudioPlayerTest {

    @Mock
    Context mockContext;

    @Mock
    MediaPlayer mockMediaPlayer;

    @Mock
    MediaSessionCompat mockMediaSession;

    @Mock
    Uri mockUri;

    private AudioPlayer audioPlayer;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        audioPlayer = new AudioPlayer(mockContext, mockMediaPlayer, mockMediaSession);
    }

    @Test
    public void testPlayAudio() throws IOException {
        Uri mockUri = mock(Uri.class);

        // Simula o comportamento de setDataSource
        doNothing().when(mockMediaPlayer).setDataSource(mockContext, mockUri);

        // Simula o comportamento de prepare() e start()
        doNothing().when(mockMediaPlayer).prepare();
        doNothing().when(mockMediaPlayer).start();

        // Chama o método play com um URI simulado
        audioPlayer.play(mockUri);

        verify(mockMediaPlayer).reset();
        verify(mockMediaPlayer).setDataSource(mockContext, mockUri);
        verify(mockMediaPlayer).prepare();
        verify(mockMediaPlayer).start();
    }

    @Test
    public void testPlayAudio_withPreviousPosition() throws IOException {
        // Simula que o áudio já tocou e pausou antes, guardando uma posição
        audioPlayer.play(mockUri); // Toca uma primeira vez para definir lastAudioUri
        when(mockMediaPlayer.isPlaying()).thenReturn(true);
        when(mockMediaPlayer.getCurrentPosition()).thenReturn(1000); // Simula uma posição salva
        audioPlayer.pause(); // Pausa para salvar a currentPosition

        // Configura o comportamento para a segunda chamada de play
        doNothing().when(mockMediaPlayer).setDataSource(mockContext, mockUri);
        doNothing().when(mockMediaPlayer).prepare();
        doNothing().when(mockMediaPlayer).start();

        // Executa o método play novamente
        audioPlayer.play(mockUri); // Deveria usar a currentPosition salva

        // Verifica as chamadas
        verify(mockMediaPlayer, times(2)).reset(); // reset é chamado em cada play
        verify(mockMediaPlayer, times(2)).setDataSource(mockContext, mockUri);
        verify(mockMediaPlayer, times(2)).prepare();
        verify(mockMediaPlayer).seekTo(1000); // Verifica se o seekTo foi chamado com a posição correta
        verify(mockMediaPlayer, times(2)).start();
    }

    @Test
    public void testPauseAudio_whenPlaying() {
        // Configura o mock para simular que o áudio está tocando
        when(mockMediaPlayer.isPlaying()).thenReturn(true);
        when(mockMediaPlayer.getCurrentPosition()).thenReturn(5000); // Posição arbitrária

        // Executa o método a ser testado
        audioPlayer.pause();

        // Verifica se getCurrentPosition e pause foram chamados no mockMediaPlayer
        verify(mockMediaPlayer).getCurrentPosition();
        verify(mockMediaPlayer).pause();
    }

    @Test
    public void testPauseAudio_whenNotPlaying() {
        // Configura o mock para simular que o áudio NÃO está tocando
        when(mockMediaPlayer.isPlaying()).thenReturn(false);

        // Executa o método a ser testado
        audioPlayer.pause();

        // Verifica que getCurrentPosition e pause NÃO foram chamados
        verify(mockMediaPlayer, never()).getCurrentPosition();
        verify(mockMediaPlayer, never()).pause();
    }

    @Test
    public void testPauseAudio() {
        when(mockMediaPlayer.isPlaying()).thenReturn(true);
        audioPlayer.pause();
        verify(mockMediaPlayer).getCurrentPosition();
        verify(mockMediaPlayer).pause();
    }

    @Test
    public void testStopAudio() {
        audioPlayer.stop();
        verify(mockMediaPlayer).stop();
    }

    @Test
    public void testReleaseAudio() {
        audioPlayer.release();
        verify(mockMediaPlayer).release();
        verify(mockMediaSession).release();
    }

    @Test
    public void testIsPlaying() {
        when(mockMediaPlayer.isPlaying()).thenReturn(true);
        boolean isPlaying = audioPlayer.isPlaying();
        assert(isPlaying);
    }

    @Test
    public void testIsPlaying_returnsTrue() {
        when(mockMediaPlayer.isPlaying()).thenReturn(true);
        assertTrue(audioPlayer.isPlaying());
    }

    @Test
    public void testIsPlaying_returnsFalse() {
        when(mockMediaPlayer.isPlaying()).thenReturn(false);
        assertFalse(audioPlayer.isPlaying());
    }

    @Test
    public void testGetMediaSession() {
        MediaSessionCompat session = audioPlayer.getMediaSession();
        assert(session == mockMediaSession);
    }
}
