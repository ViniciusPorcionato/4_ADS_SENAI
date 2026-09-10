package br.edu.unisenai.rangonaregua;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import br.edu.unisenai.rangonaregua.adapter.LugarAdapter;
import br.edu.unisenai.rangonaregua.data.Catalogo;
import br.edu.unisenai.rangonaregua.model.Lugar;


public class MainActivity extends AppCompatActivity {

    static List<Lugar> listaLugares = new ArrayList<>();
    private LugarAdapter adapter;

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

        FloatingActionButton btNovo = findViewById(R.id.fabNovo);
        btNovo.setOnClickListener(v -> {
            Intent intent = new Intent(this, NovoLugarActivity.class);
            startActivity(intent);
        });

        // Carrear base de dados
        listaLugares = Catalogo.inicial();

        //Configurar o RecycleView
        RecyclerView rvLugares = findViewById(R.id.rvLugares);
        rvLugares.setLayoutManager(new LinearLayoutManager(this));

        adapter = new LugarAdapter(listaLugares);
        rvLugares.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
