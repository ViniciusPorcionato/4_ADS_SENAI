package com.aula.playlist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.playlist.adapter.MusicaAdapter;
import com.aula.playlist.model.Musica;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements MusicaAdapter.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private static final String COLECAO_PLAYLIST = "playlist";
    private static final String CAMPO_VOTOS = "votos";
    private static final String PREFERENCIAS_USUARIO = "nomeUsuario";
    private static final String CHAVE_NOME_USUARIO = "nomeUsuario";

    private String meuNome;
    private CollectionReference colecaoPlaylist;
    private MusicaAdapter adaptador;
    private TextView txtVazio;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Carregar dados do usuário
        carregarNome();

        // Ação do botão de adicionar
        FloatingActionButton fab = findViewById(R.id.fabAdicionar);
        fab.setOnClickListener(v -> mostrarDialogoNovaMusica());

        // Adicionar SUA IMPLEMENTAÇÃO AQUI
        colecaoPlaylist = FirebaseFirestore.getInstance().collection(COLECAO_PLAYLIST);
        txtVazio = findViewById(R.id.txtVazio);

        RecyclerView listaMusicas = findViewById(R.id.listaMusicas);
        listaMusicas.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new MusicaAdapter(new ArrayList<>(), this);
        listaMusicas.setAdapter(adaptador);
    }

    private void carregarNome() {
        SharedPreferences localNome = getSharedPreferences(PREFERENCIAS_USUARIO, MODE_PRIVATE);
        meuNome = localNome.getString(CHAVE_NOME_USUARIO, null);

        if (meuNome != null && !meuNome.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.boas_vindas_usuario, meuNome),
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Ativar a caixa de texto para digitar o nome
        EditText campo = new EditText(this);
        campo.setHint(R.string.dica_nome_usuario);

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle(R.string.titulo_nome_usuario)
                .setMessage(R.string.mensagem_nome_usuario)
                .setView(campo)
                .setCancelable(false)
                .setPositiveButton(R.string.pronto, null)
                .create();

        // Validar antes de fechar o diálogo, para impedir um nome vazio.
        dialogo.setOnShowListener(dialogInterface ->
                dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String nomeDigitado = campo.getText().toString().trim();
                    if (TextUtils.isEmpty(nomeDigitado)) {
                        campo.setError(getString(R.string.nome_usuario_obrigatorio));
                        return;
                    }

                    meuNome = nomeDigitado;
                    localNome.edit().putString(CHAVE_NOME_USUARIO, meuNome).apply();
                    dialogo.dismiss();
                }));
        dialogo.show();
    }


    private void mostrarDialogoNovaMusica() {
        View form = LayoutInflater.from(this).inflate(R.layout.tela_musica, null);
        EditText campoTitulo = form.findViewById(R.id.campoTitulo);
        EditText campoArtista = form.findViewById(R.id.campoArtista);

        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setTitle(R.string.titulo_indicar_musica)
                .setView(form)
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.adicionar, null)
                .create();

        // Manter o formulário aberto quando os campos estiverem vazios.
        dialogo.setOnShowListener(dialogInterface ->
                dialogo.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    String titulo = campoTitulo.getText().toString().trim();
                    String artista = campoArtista.getText().toString().trim();

                    if (TextUtils.isEmpty(titulo) || TextUtils.isEmpty(artista)) {
                        Toast.makeText(this, R.string.campos_musica_obrigatorios,
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    adicionarMusica(titulo, artista);
                    dialogo.dismiss();
                }));
        dialogo.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        escutarPlaylist();
    }

    private void escutarPlaylist() {
        // O listener associado à Activity é removido automaticamente em onStop.
        colecaoPlaylist.orderBy(CAMPO_VOTOS, Query.Direction.DESCENDING)
                .addSnapshotListener(this, (documentos, erro) -> {
                    if (erro != null) {
                        mostrarErroFirestore(R.string.erro_carregar_playlist, erro);
                        return;
                    }
                    if (documentos == null) {
                        return;
                    }

                    List<Musica> musicas = documentos.toObjects(Musica.class);
                    adaptador.setMusicas(musicas);
                    txtVazio.setVisibility(musicas.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void adicionarMusica(String titulo, String artista) {
        colecaoPlaylist.add(new Musica(titulo, artista, meuNome))
                .addOnFailureListener(erro -> mostrarErroFirestore(R.string.erro_adicionar_musica, erro));
    }

    @Override
    public void onVotar(Musica musica) {
        colecaoPlaylist.document(musica.getId()).update(CAMPO_VOTOS, FieldValue.increment(1))
                .addOnFailureListener(erro -> mostrarErroFirestore(R.string.erro_votar_musica, erro));
    }

    @Override
    public void onExcluir(Musica musica) {
        if (!TextUtils.equals(musica.getIndicadoPor(), meuNome)) {
            Toast.makeText(this, getString(R.string.aviso_dono_musica, musica.getIndicadoPor()),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.excluir)
                .setMessage(getString(R.string.pergunta_excluir_musica, musica.getTitulo()))
                .setNegativeButton(R.string.nao, null)
                .setPositiveButton(R.string.excluir, (d, w) -> excluirMusica(musica))
                .show();
    }

    private void excluirMusica(Musica musica) {
        colecaoPlaylist.document(musica.getId()).delete()
                .addOnSuccessListener(resultado -> Snackbar.make(findViewById(R.id.main),
                        R.string.musica_removida, Snackbar.LENGTH_SHORT).show())
                .addOnFailureListener(erro -> mostrarErroFirestore(R.string.erro_excluir_musica, erro));
    }

    private void mostrarErroFirestore(@StringRes int recursoMensagem, Exception erro) {
        Log.e(TAG, getString(recursoMensagem), erro);
        Snackbar.make(findViewById(R.id.main), recursoMensagem, Snackbar.LENGTH_LONG).show();
    }
}