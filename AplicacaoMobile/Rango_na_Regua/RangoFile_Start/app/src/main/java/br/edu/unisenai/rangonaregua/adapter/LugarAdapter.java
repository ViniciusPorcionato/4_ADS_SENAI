package br.edu.unisenai.rangonaregua.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.edu.unisenai.rangonaregua.R;
import br.edu.unisenai.rangonaregua.model.Lugar;

public class LugarAdapter extends RecyclerView.Adapter<LugarAdapter.ViewHolder> {

    private List<Lugar> listaLugares;

    private static final int CARD_LIDER = 0;
    private static final int CARD_NORMAL = 1;

    public LugarAdapter(List<Lugar> listaLugares) {
        this.listaLugares = listaLugares;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? CARD_LIDER : CARD_NORMAL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View tela = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lugar, parent, false);
        return new ViewHolder(tela);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lugar item = listaLugares.get(position);

        holder.txtPosicao.setText(String.valueOf(position + 1));
        holder.txtNome.setText(item.getNome());
        holder.txtCategoria.setText(item.getCategoria());
        holder.txtPreco.setText(String.format("R$ %.2f", item.getPrecoMedio()));
        holder.txtVotos.setText(item.getVotos() + " votos");

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPosicao, txtNome, txtCategoria, txtPreco, txtVotos;
        Button btnVoltar;
        public ViewHolder(View itemView) {
            super(itemView);
            txtPosicao = itemView.findViewById(R.id.txtPosicao);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);
            txtPreco = itemView.findViewById(R.id.txtPreco);
            txtVotos = itemView.findViewById(R.id.txtVotos);
            btnVoltar = itemView.findViewById(R.id.btnVotar);
        }
    }

}
