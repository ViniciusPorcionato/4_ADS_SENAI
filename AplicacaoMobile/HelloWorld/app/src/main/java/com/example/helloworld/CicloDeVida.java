package com.example.helloworld;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

public class CicloDeVida extends AppCompatActivity {
    private static final String TAG = "LogCicloDeVida";
    private int contador = 0;
    private TextView txtSaida;

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "Entrou no onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Entrou no onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "Entrou no onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "Entrou no onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "Entrou no onDestroy()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "Entrou no onRestart()");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e(TAG, "Entrou no onSaveInstanceState()");
        outState.putInt("valor", contador);
        outState.putString("msg", "To Salvo!");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            contador = savedInstanceState.getInt("valor");
            txtSaida.setText(String.valueOf(contador));
            Toast.makeText(this, savedInstanceState.getString("msg"), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ciclo_vida);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.i(TAG, "Entrou no onCreate()");


        Button btMais = findViewById(R.id.btnMais);
        txtSaida = findViewById(R.id.txtSaida);

        btMais.setOnClickListener(v -> {
            contador++;
            txtSaida.setText(String.valueOf(contador));
        });
    }
}
