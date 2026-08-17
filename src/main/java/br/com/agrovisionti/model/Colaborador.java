package br.com.agrovisionti.model;

public class Colaborador {

    private int id;
    private String nome;
    private String cpf;
    private String cargo;
    private String setor;
    private int unidadeId;
    private boolean ativo;

    public Colaborador() {
    }

    public Colaborador(String nome, String cpf, String cargo, String setor, int unidadeId) {
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.setor = setor;
        this.unidadeId = unidadeId;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public int getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(int unidadeId) {
        this.unidadeId = unidadeId;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    // Usado pelo JComboBox (útil já agora, e essencial na Fase 2,
    // quando Colaborador passar a ser selecionado em combos de
    // Ativo/Movimentacao/Termo)
    @Override
    public String toString() {
        return nome;
    }
}