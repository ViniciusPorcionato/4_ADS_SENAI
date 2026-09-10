package br.edu.unisenai.rangonaregua;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etSenha = findViewById(R.id.etSenha);
        Button btnEntrar = findViewById(R.id.btEntrar);
        Button btnCriarConta = findViewById(R.id.btCriarConta);

        btnEntrar.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();

            boolean valido = true;

            etEmail.setError(null);
            etSenha.setError(null);

            if (email.isEmpty()) {
                etEmail.setError("Informe seu e-mail.");
                valido = false;
            }

            if (senha.isEmpty()) {
                etSenha.setError("Informe sua senha.");
                valido = false;
            } else if (senha.length() < 6) {
                etSenha.setError("A senha deve conter pelo menos 6 caracteres.");
                valido = false;
            }

            if (!valido) {
                Toast.makeText(this, "Corrija os campos antes de continuar.", Toast.LENGTH_SHORT).show();
                return;
            }

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnCriarConta.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }
}
