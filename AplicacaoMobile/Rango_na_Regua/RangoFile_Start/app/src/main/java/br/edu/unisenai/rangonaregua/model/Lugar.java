package br.edu.unisenai.rangonaregua.model;

import java.io.Serializable;


public class Lugar implements Serializable {

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

    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public double getPrecoMedio() { return precoMedio; }
    public String getObservacao() { return observacao; }
    public int getVotos() { return votos; }

    public void setVotos(int votos) { this.votos = votos; }
}
