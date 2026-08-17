package br.com.agrovisionti.dao;

import br.com.agrovisionti.connection.Conexao;
import br.com.agrovisionti.model.Colaborador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorDAO {

    public void salvar(Colaborador colaborador) {

        String sql = "INSERT INTO colaboradores (nome, cpf, cargo, setor, unidade_id) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, colaborador.getNome());
            stmt.setString(2, colaborador.getCpf());
            stmt.setString(3, colaborador.getCargo());
            stmt.setString(4, colaborador.getSetor());
            stmt.setInt(5, colaborador.getUnidadeId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar colaborador.", e);
        }
    }

    // Lista só os ativos — usada para popular combos de seleção (Fase 2)
    public List<Colaborador> listarAtivos() {
        return executarListagem("SELECT * FROM colaboradores WHERE ativo = true ORDER BY nome");
    }

    // Lista todos, incluindo desativados — usada na tela de gerenciamento
    public List<Colaborador> listarTodos() {
        return executarListagem("SELECT * FROM colaboradores ORDER BY nome");
    }

    public List<Colaborador> pesquisar(String termo) {

        List<Colaborador> colaboradores = new ArrayList<>();

        String sql = """
                SELECT *
                FROM colaboradores
                WHERE nome LIKE ?
                   OR cpf LIKE ?
                   OR cargo LIKE ?
                   OR setor LIKE ?
                ORDER BY nome
                """;

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            String filtro = "%" + termo + "%";

            for (int i = 1; i <= 4; i++) {
                stmt.setString(i, filtro);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    colaboradores.add(mapearColaborador(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao pesquisar colaboradores.", e);
        }

        return colaboradores;
    }

    public Colaborador buscarPorId(int id) {

        String sql = "SELECT * FROM colaboradores WHERE id = ?";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearColaborador(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar colaborador.", e);
        }

        return null;
    }

    public void atualizar(Colaborador colaborador) {

        String sql = "UPDATE colaboradores SET nome = ?, cpf = ?, cargo = ?, setor = ?, unidade_id = ? "
                + "WHERE id = ?";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, colaborador.getNome());
            stmt.setString(2, colaborador.getCpf());
            stmt.setString(3, colaborador.getCargo());
            stmt.setString(4, colaborador.getSetor());
            stmt.setInt(5, colaborador.getUnidadeId());
            stmt.setInt(6, colaborador.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar colaborador.", e);
        }
    }

    // Soft delete, mesmo padrão de Ativo/Unidade/Usuario
    public void desativar(int id) {
        alterarStatus(id, false);
    }

    public void reativar(int id) {
        alterarStatus(id, true);
    }

    private void alterarStatus(int id, boolean ativo) {

        String sql = "UPDATE colaboradores SET ativo = ? WHERE id = ?";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setBoolean(1, ativo);
            stmt.setInt(2, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar status do colaborador.", e);
        }
    }

    private List<Colaborador> executarListagem(String sql) {

        List<Colaborador> colaboradores = new ArrayList<>();

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                colaboradores.add(mapearColaborador(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar colaboradores.", e);
        }

        return colaboradores;
    }

    private Colaborador mapearColaborador(ResultSet rs) throws SQLException {

        Colaborador colaborador = new Colaborador();
        colaborador.setId(rs.getInt("id"));
        colaborador.setNome(rs.getString("nome"));
        colaborador.setCpf(rs.getString("cpf"));
        colaborador.setCargo(rs.getString("cargo"));
        colaborador.setSetor(rs.getString("setor"));
        colaborador.setUnidadeId(rs.getInt("unidade_id"));
        colaborador.setAtivo(rs.getBoolean("ativo"));

        return colaborador;
    }
}