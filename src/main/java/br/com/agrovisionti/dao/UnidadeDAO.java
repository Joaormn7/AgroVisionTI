package br.com.agrovisionti.dao;

import br.com.agrovisionti.connection.Conexao;
import br.com.agrovisionti.model.Unidade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnidadeDAO {

    public void salvar(Unidade unidade) {
        String sql = "INSERT INTO unidades (nome, tipo, cnpj) VALUES (?, ?, ?)";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, unidade.getNome());
            stmt.setString(2, unidade.getTipo());
            stmt.setString(3, unidade.getCnpj());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar unidade.", e);
        }
    }

    // Lista só as unidades ativas — usada para popular combos de seleção
    public List<Unidade> listarAtivas() {
        List<Unidade> unidades = new ArrayList<>();
        String sql = "SELECT * FROM unidades WHERE ativo = true ORDER BY nome";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                unidades.add(mapearUnidade(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar unidades.", e);
        }

        return unidades;
    }

    // Lista todas, incluindo desativadas — usada na tela de gerenciamento
    public List<Unidade> listarTodas() {
        List<Unidade> unidades = new ArrayList<>();
        String sql = "SELECT * FROM unidades ORDER BY nome";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                unidades.add(mapearUnidade(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar unidades.", e);
        }

        return unidades;
    }

    public void atualizar(Unidade unidade) {
        String sql = "UPDATE unidades SET nome = ?, tipo = ?, cnpj = ? WHERE id = ?";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, unidade.getNome());
            stmt.setString(2, unidade.getTipo());
            stmt.setString(3, unidade.getCnpj());
            stmt.setInt(4, unidade.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar unidade.", e);
        }
    }

    // Soft delete, seguindo o mesmo padrão adotado em Ativos
    public void desativar(int id) {
        alterarStatus(id, false);
    }

    public void reativar(int id) {
        alterarStatus(id, true);
    }

    private void alterarStatus(int id, boolean ativo) {
        String sql = "UPDATE unidades SET ativo = ? WHERE id = ?";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setBoolean(1, ativo);
            stmt.setInt(2, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar status da unidade.", e);
        }
    }

    private Unidade mapearUnidade(ResultSet rs) throws SQLException {
        Unidade unidade = new Unidade();
        unidade.setId(rs.getInt("id"));
        unidade.setNome(rs.getString("nome"));
        unidade.setTipo(rs.getString("tipo"));
        unidade.setCnpj(rs.getString("cnpj"));
        unidade.setAtivo(rs.getBoolean("ativo"));
        return unidade;
    }
}