package com.aula.playlist.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aula.playlist.R;
import com.aula.playlist.model.Musica;

import java.util.List;

public class MusicaAdapter extends RecyclerView.Adapter<MusicaAdapter.MusicaViewHolder> {

    public interface OnItemClickListener {
        void onVotar(Musica musica);
        void onExcluir(Musica musica);
    }

    private List<Musica> musicas;
    private final OnItemClickListener ouvinte;

    public MusicaAdapter(List<Musica> musicas, OnItemClickListener ouvinte) {
        this.musicas = musicas;
        this.ouvinte = ouvinte;
    }

    @Override
    public int getItemCount() {
        return musicas.size();
    }

    @NonNull
    @Override
    public MusicaViewHolder onCreateViewHolder(@NonNull ViewGroup pai, int tipoVisualizacao) {
        View item = LayoutInflater.from(pai.getContext())
                .inflate(R.layout.item_musica, pai, false);
        return new MusicaViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicaViewHolder suporte, int posicao) {
        suporte.preencher(musicas.get(posicao), ouvinte);
    }

    public void setMusicas(List<Musica> musicas) {
        this.musicas = musicas;
        notifyDataSetChanged();
    }

    public static class MusicaViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtTitulo;
        private final TextView txtArtista;
        private final TextView txtIndicadoPor;
        private final TextView txtVotos;
        private final ImageButton btnVotar;
        private final ImageButton btnExcluir;

        public MusicaViewHolder(@NonNull View item) {
            super(item);
            txtTitulo = item.findViewById(R.id.txtTitulo);
            txtArtista = item.findViewById(R.id.txtArtista);
            txtIndicadoPor = item.findViewById(R.id.txtIndicadoPor);
            txtVotos = item.findViewById(R.id.txtVotos);
            btnVotar = item.findViewById(R.id.btnVotar);
            btnExcluir = item.findViewById(R.id.btnExcluir);
        }

        public void preencher(Musica musica, OnItemClickListener ouvinte) {
            txtTitulo.setText(musica.getTitulo());
            txtArtista.setText(musica.getArtista());
            txtIndicadoPor.setText(itemView.getContext()
                    .getString(R.string.musica_indicada_por, musica.getIndicadoPor()));
            txtVotos.setText(String.valueOf(musica.getVotos()));
            btnVotar.setOnClickListener(tela -> ouvinte.onVotar(musica));
            btnExcluir.setOnClickListener(tela -> ouvinte.onExcluir(musica));
        }
    }
}
