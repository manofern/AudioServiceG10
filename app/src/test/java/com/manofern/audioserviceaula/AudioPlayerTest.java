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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AudioPlayerTest {

    @Mock
    Context mockContext;

    @Mock
    MediaPlayer mockMediaPlayer;

    @Mock
    MediaSessionCompat mockMediaSession;

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
    public void testGetMediaSession() {
        MediaSessionCompat session = audioPlayer.getMediaSession();
        assert(session == mockMediaSession);
    }
}
