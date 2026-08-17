package br.com.agrovisionti.model;

public class Unidade {

    private int id;
    private String nome;
    private String tipo;
    private String cnpj;
    private boolean ativo;

    public Unidade() {
    }

    public Unidade(String nome, String tipo, String cnpj) {
        this.nome = nome;
        this.tipo = tipo;
        this.cnpj = cnpj;
        this.ativo = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // Usado pelo JComboBox pra exibir o nome em vez do endereço do objeto
    @Override
    public String toString() {
        return nome;
    }
}