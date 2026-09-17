package br.edu.unisenai.rangonaregua;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.GoogleAuthProvider;


public class LoginActivity extends AppCompatActivity {


    private EditText etEmail;
    private EditText etSenha ;
    private Button btEntrar;
    private Button btCriarConta;
    private Button btRecuperar;
    private SignInButton btGoogle;
    private FirebaseAuth autenticar;
    private ActivityResultLauncher<Intent> signInLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        task.addOnSuccessListener(googleAccount -> {
                            AuthCredential credential = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);
                            autenticar.signInWithCredential(credential);

                            Intent rota = new Intent(this, MainActivity.class);
                            startActivity(rota);
                            finish();
                        });
                    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        btEntrar = findViewById(R.id.btEntrar);
        btCriarConta = findViewById(R.id.btCriarConta);
        btRecuperar = findViewById(R.id.btRecuperar);

        // Abrir conexão com o serviço de autenticação
        autenticar = FirebaseAuth.getInstance();
        // -- fim

        btCriarConta.setOnClickListener(v -> criarConta());
        btEntrar.setOnClickListener(v -> entrar());
        btRecuperar.setOnClickListener(v -> recuperarSenha());
        btGoogle.setOnClickListener(v -> google());
    }
    private void criarConta() {
        if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError("Digite seu email");
        } else if (etSenha.getText().toString().isEmpty()) {
            etSenha.setError("Digite sua senha");
        } else {
            autenticar.createUserWithEmailAndPassword(etEmail.getText().toString(),
                            etSenha.getText().toString())
                    .addOnFailureListener(e -> {
                        if(e instanceof FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(this, "Email ou senha inválidos", Toast.LENGTH_SHORT).show();
                        } else if(e instanceof FirebaseAuthEmailException) {
                            Toast.makeText(this, "Email não cadastrado ou inválido", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnSuccessListener(authResult -> {
                        Intent rota = new Intent(this, MainActivity.class);
                        startActivity(rota);
                        finish();
                    });
        }
    }
    private void entrar() {
        if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError("Digite seu email");
        } else if (etSenha.getText().toString().isEmpty()) {
            etSenha.setError("Digite sua senha");
        } else {
            autenticar.signInWithEmailAndPassword(etEmail.getText().toString(),
                            etSenha.getText().toString())
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnSuccessListener(authResult -> {
                        Intent rota = new Intent(this, MainActivity.class);
                        startActivity(rota);
                        finish();
                    });
        }
    }

    private void google() {
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signInLauncher.launch(signInIntent);
    }

    private void recuperarSenha() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Digite seu email para recuperar a senha");
            etEmail.requestFocus();
            return;
        }

        autenticar.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Email de recuperação enviado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
