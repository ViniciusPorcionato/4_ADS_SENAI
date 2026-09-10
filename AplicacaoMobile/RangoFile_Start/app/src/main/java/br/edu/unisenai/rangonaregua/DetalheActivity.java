package br.edu.unisenai.rangonaregua;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import br.edu.unisenai.rangonaregua.model.Lugar;

import java.text.NumberFormat;
import java.util.Locale;


public class DetalheActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalhe);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Lugar dados = (Lugar) getIntent().getSerializableExtra("obj");

        TextView txtNome = findViewById(R.id.txtDetalheNome);
        TextView txtCategoria = findViewById(R.id.txtDetalheCategoria);
        TextView txtPreco = findViewById(R.id.txtDetalhePreco);
        TextView txtVotos = findViewById(R.id.txtDetalheVotos);
        TextView txtObservacao = findViewById(R.id.txtDetalheObservacao);

        txtNome.setText(dados.getNome());
        txtCategoria.setText(dados.getCategoria());
        txtPreco.setText("R$ " + dados.getPrecoMedio());
        txtVotos.setText(dados.getVotos() + " votos");
        txtObservacao.setText(dados.getObservacao());
    }
}
