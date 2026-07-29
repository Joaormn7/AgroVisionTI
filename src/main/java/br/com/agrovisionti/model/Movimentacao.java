package br.com.agrovisionti.model;

import java.time.LocalDateTime;

public class Movimentacao {

    private int id;
    private int ativoId;
    private String unidadeOrigem;
    private String unidadeDestino;
    private String responsavelOrigem;
    private String responsavelDestino;
    private LocalDateTime dataMovimentacao;
    private String observacoes;

    public Movimentacao() {
    }

    public Movimentacao(int ativoId, String unidadeOrigem, String unidadeDestino,
                        String responsavelOrigem, String responsavelDestino, String observacoes) {
        this.ativoId = ativoId;
        this.unidadeOrigem = unidadeOrigem;
        this.unidadeDestino = unidadeDestino;
        this.responsavelOrigem = responsavelOrigem;
        this.responsavelDestino = responsavelDestino;
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

    public String getResponsavelOrigem() {
        return responsavelOrigem;
    }

    public void setResponsavelOrigem(String responsavelOrigem) {
        this.responsavelOrigem = responsavelOrigem;
    }

    public String getResponsavelDestino() {
        return responsavelDestino;
    }

    public void setResponsavelDestino(String responsavelDestino) {
        this.responsavelDestino = responsavelDestino;
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