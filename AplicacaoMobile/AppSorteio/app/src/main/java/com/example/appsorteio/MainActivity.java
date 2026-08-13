package com.example.appsorteio;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    
    private static final int INTERVALO = 60;
    private int ultimoNumero = -1;
    private Random random;
    private Button btnSorteio;
    private Button btnLimpar;
    private TextView tvResultado;

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
        
        random = new Random();
        
        btnSorteio = findViewById(R.id.btnSorteio);
        btnLimpar = findViewById(R.id.btnLimpar);
        tvResultado = findViewById(R.id.tvResultado);
        
        btnSorteio.setOnClickListener(v -> sortearNumero());
        btnLimpar.setOnClickListener(v -> limparResultado());
    }
    
    private void sortearNumero() {
        int numero;
        do {
            numero = random.nextInt(INTERVALO) + 1;
        } while (numero == ultimoNumero);
        
        ultimoNumero = numero;
        tvResultado.setText("Resultado: " + numero);
    }
    
    private void limparResultado() {
        ultimoNumero = -1;
        tvResultado.setText("Resultado: ");
    }
}