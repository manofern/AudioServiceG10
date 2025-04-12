package com.manofern.audioserviceaula;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String AUDIO_PATH = "/sdcard/Music/musica.mp3"; // <-- Ajuste o caminho conforme necessário

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

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("AudioServiceMainActivity", "btnPlay");

                Intent intent = new Intent(MainActivity.this, AudioService.class);
                intent.setAction("PLAY");
                intent.putExtra("path", AUDIO_PATH);
                startService(intent);
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("AudioServiceMainActivity", "btnPause");

                Intent intent = new Intent(MainActivity.this, AudioService.class);
                intent.setAction("PAUSE");
                startService(intent);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("AudioServiceMainActivity", "btnStop");

                Intent intent = new Intent(MainActivity.this, AudioService.class);
                intent.setAction("STOP");
                startService(intent);
            }
        });
    }
}
