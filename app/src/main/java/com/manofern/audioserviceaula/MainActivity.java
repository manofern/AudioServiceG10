package com.manofern.audioserviceaula;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_AUDIO_REQUEST = 1;
    private String selectedAudioUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnPlay = findViewById(R.id.btnPlay);
        Button btnPause = findViewById(R.id.btnPause);
        Button btnStop = findViewById(R.id.btnStop);
        Button btnAddTrack = findViewById(R.id.btnAddTrack);

        btnAddTrack.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(intent, "Escolha uma faixa de áudio"), PICK_AUDIO_REQUEST);
        });

        btnPlay.setOnClickListener(v -> {
            Log.i("AudioServiceMainActivity", "btnPlay");

            if (selectedAudioUri != null) {
                Intent intent = new Intent(MainActivity.this, AudioService.class);
                intent.setAction("PLAY");
                intent.putExtra("path", selectedAudioUri); // URI como string
                startService(intent);
            } else {
                Log.e("AudioServiceMainActivity", "Nenhuma faixa selecionada");
            }
        });

        btnPause.setOnClickListener(v -> {
            Log.i("AudioServiceMainActivity", "btnPause");
            Intent intent = new Intent(MainActivity.this, AudioService.class);
            intent.setAction("PAUSE");
            startService(intent);
        });

        btnStop.setOnClickListener(v -> {
            Log.i("AudioServiceMainActivity", "btnStop");
            Intent intent = new Intent(MainActivity.this, AudioService.class);
            intent.setAction("STOP");
            startService(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                selectedAudioUri = uri.toString();
                Log.i("AudioServiceMainActivity", "URI selecionada: " + selectedAudioUri);
            }
        }
    }
}
