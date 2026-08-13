package com.example.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Principal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btOla = findViewById(R.id.btnOla);
        Button btCiclo = findViewById(R.id.btnCiclo);

        btOla.setOnClickListener(v -> {
            Intent rota = new Intent(this, MainActivity.class);
            startActivity(rota);
        });

        btCiclo.setOnClickListener(v -> {
            Intent rota = new Intent(this, CicloDeVida.class);
            startActivity(rota);
        });


    }
}