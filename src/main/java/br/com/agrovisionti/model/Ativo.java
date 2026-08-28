package br.com.agrovisionti.model;

import java.sql.Timestamp;

public class Ativo {

    private int id;
    private String tipo;
    private String marca;
    private String modelo;
    private String numeroSerie;
    private int unidadeId;
    private String unidadeNome; // não é coluna da tabela — vem do JOIN com unidades, só para exibição
    private Integer responsavelId;
    private String status;
    private String observacoes;
    private Timestamp dataCadastro;

    public Ativo() {
    }

    public Ativo(int id, String tipo, String marca, String modelo,
                 String numeroSerie, int unidadeId,
                 Integer responsavelId, String status,
                 String observacoes, Timestamp dataCadastro) {

        this.id = id;
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.numeroSerie = numeroSerie;
        this.unidadeId = unidadeId;
        this.responsavelId = responsavelId;
        this.status = status;
        this.observacoes = observacoes;
        this.dataCadastro = dataCadastro;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public int getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(int unidadeId) {
        this.unidadeId = unidadeId;
    }

    public String getUnidadeNome() {
        return unidadeNome;
    }

    public void setUnidadeNome(String unidadeNome) {
        this.unidadeNome = unidadeNome;
    }

    public Integer getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(Integer responsavelId) {
        this.responsavelId = responsavelId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Timestamp getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Timestamp dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}