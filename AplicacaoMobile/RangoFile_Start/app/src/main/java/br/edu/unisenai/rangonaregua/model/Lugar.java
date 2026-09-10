package br.edu.unisenai.rangonaregua.model;

import com.google.firebase.firestore.DocumentId;

import java.io.Serializable;


public class Lugar implements Serializable {

    @DocumentId
    private String id;
    private String nome;
    private String categoria;
    private double precoMedio;
    private String observacao;
    private int votos;

    public Lugar(String nome, String categoria, double precoMedio, String observacao, int votos) {
        this.nome = nome;
        this.categoria = categoria;
        this.precoMedio = precoMedio;
        this.observacao = observacao;
        this.votos = votos;
    }

    public Lugar() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public double getPrecoMedio() { return precoMedio; }
    public String getObservacao() { return observacao; }
    public int getVotos() { return votos; }

    public void setVotos(int votos) { this.votos = votos; }
}
