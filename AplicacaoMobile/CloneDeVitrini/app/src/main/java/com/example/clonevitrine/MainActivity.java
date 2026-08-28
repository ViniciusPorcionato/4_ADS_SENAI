package com.example.clonevitrine;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnNetflix = findViewById(R.id.btn_netflix_go);
        btnNetflix.setOnClickListener(v -> {
            Intent rota = new Intent(this, Netflix.class);
            startActivity(rota);
        });

        Button btnHbo = findViewById(R.id.btn_hbo_go);
        btnHbo.setOnClickListener(v -> {
            Intent rota = new Intent(this, Hbo.class);
            startActivity(rota);
        });
    }
}