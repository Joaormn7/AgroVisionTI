package br.com.agrovisionti.model;

import java.time.LocalDateTime;

public class Movimentacao {

    private int id;
    private int ativoId;
    private Integer unidadeOrigemId;
    private String unidadeOrigemNome; // não é coluna da tabela — vem do JOIN com unidades, só para exibição
    private int unidadeDestinoId;
    private String unidadeDestinoNome; // idem
    private Integer responsavelOrigemId;
    private int responsavelDestinoId;
    private LocalDateTime dataMovimentacao;
    private String observacoes;

    public Movimentacao() {
    }

    public Movimentacao(int ativoId, Integer unidadeOrigemId, int unidadeDestinoId,
                        Integer responsavelOrigemId, int responsavelDestinoId, String observacoes) {
        this.ativoId = ativoId;
        this.unidadeOrigemId = unidadeOrigemId;
        this.unidadeDestinoId = unidadeDestinoId;
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

    public Integer getUnidadeOrigemId() {
        return unidadeOrigemId;
    }

    public void setUnidadeOrigemId(Integer unidadeOrigemId) {
        this.unidadeOrigemId = unidadeOrigemId;
    }

    public String getUnidadeOrigemNome() {
        return unidadeOrigemNome;
    }

    public void setUnidadeOrigemNome(String unidadeOrigemNome) {
        this.unidadeOrigemNome = unidadeOrigemNome;
    }

    public int getUnidadeDestinoId() {
        return unidadeDestinoId;
    }

    public void setUnidadeDestinoId(int unidadeDestinoId) {
        this.unidadeDestinoId = unidadeDestinoId;
    }

    public String getUnidadeDestinoNome() {
        return unidadeDestinoNome;
    }

    public void setUnidadeDestinoNome(String unidadeDestinoNome) {
        this.unidadeDestinoNome = unidadeDestinoNome;
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