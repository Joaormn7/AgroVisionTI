package br.com.agrovisionti.model;

import java.time.LocalDateTime;

public class Termo {

    private int id;
    private int ativoId;
    private int responsavelId;
    private LocalDateTime dataEmissao;
    private String status;
    private String observacoes;

    public Termo() {
    }

    // Construtor de conveniência para cadastro: status sempre nasce "Pendente"
    public Termo(int ativoId, int responsavelId, String observacoes) {
        this.ativoId = ativoId;
        this.responsavelId = responsavelId;
        this.observacoes = observacoes;
        this.status = "Pendente";
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

    public int getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(int responsavelId) {
        this.responsavelId = responsavelId;
    }

    public LocalDateTime getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(LocalDateTime dataEmissao) {
        this.dataEmissao = dataEmissao;
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
}