package br.com.agrovisionti.model;

import java.time.LocalDateTime;

public class Movimentacao {

    private int id;
    private int ativoId;
    private String unidadeOrigem;
    private String unidadeDestino;
    private Integer responsavelOrigemId;
    private int responsavelDestinoId;
    private LocalDateTime dataMovimentacao;
    private String observacoes;

    public Movimentacao() {
    }

    public Movimentacao(int ativoId, String unidadeOrigem, String unidadeDestino,
                        Integer responsavelOrigemId, int responsavelDestinoId, String observacoes) {
        this.ativoId = ativoId;
        this.unidadeOrigem = unidadeOrigem;
        this.unidadeDestino = unidadeDestino;
        this.responsavelOrigemId = responsavelOrigemId;
        this.responsavelDestinoId = responsavelDestinoId;
        this.observacoes = observacoes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAtivoId() {
        return ativoId;
    }

    public void setAtivoId(int ativoId) {
        this.ativoId = ativoId;
    }

    public String getUnidadeOrigem() {
        return unidadeOrigem;
    }

    public void setUnidadeOrigem(String unidadeOrigem) {
        this.unidadeOrigem = unidadeOrigem;
    }

    public String getUnidadeDestino() {
        return unidadeDestino;
    }

    public void setUnidadeDestino(String unidadeDestino) {
        this.unidadeDestino = unidadeDestino;
    }

    public Integer getResponsavelOrigemId() {
        return responsavelOrigemId;
    }

    public void setResponsavelOrigemId(Integer responsavelOrigemId) {
        this.responsavelOrigemId = responsavelOrigemId;
    }

    public int getResponsavelDestinoId() {
        return responsavelDestinoId;
    }

    public void setResponsavelDestinoId(int responsavelDestinoId) {
        this.responsavelDestinoId = responsavelDestinoId;
    }

    public LocalDateTime getDataMovimentacao() {
        return dataMovimentacao;
    }

    public void setDataMovimentacao(LocalDateTime dataMovimentacao) {
        this.dataMovimentacao = dataMovimentacao;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}