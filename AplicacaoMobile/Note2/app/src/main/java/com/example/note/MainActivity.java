package com.example.note;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnAdicionar;
    private AdapterNota adapter;
    private ArrayList<Nota> listaNotas = new ArrayList<>();

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

        btnAdicionar = findViewById(R.id.button);
        btnAdicionar.setOnClickListener(v -> {
           adicionar();
        });

//        Configurar o RecyclerView
        RecyclerView rvNotas = findViewById(R.id.rvNotas);
        rvNotas.setLayoutManager(new LinearLayoutManager(this));
//        Carregar notas Adapter
        adapter = new AdapterNota(listaNotas);
        rvNotas.setAdapter(adapter);

    }

    private void adicionar(){
        View tela = LayoutInflater.from(this)
                .inflate(R.layout.caixa_adicionar, null, false);
        EditText campoTitulo = tela.findViewById(R.id.campoTitulo);
        EditText campoDescricao = tela.findViewById(R.id.campo_descricao);

        new AlertDialog.Builder(this)
                .setTitle("Adicionar Tarefa")
                .setView(tela)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Adicionar", (dialog, which) -> {
                    String titulo = campoTitulo.getText().toString();
                    String descricao = campoDescricao.getText().toString();
                    listaNotas.add(new Nota(titulo, descricao));
                    Toast.makeText(this, "Nota adicionada com sucesso!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }).show();
    }
}