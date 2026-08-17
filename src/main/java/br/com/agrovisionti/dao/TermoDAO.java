package br.com.agrovisionti.dao;

import br.com.agrovisionti.connection.Conexao;
import br.com.agrovisionti.model.Termo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TermoDAO {

    // Emite um novo termo, sempre nascendo com status "Pendente"
    public void cadastrar(Termo termo) {

        String sql = "INSERT INTO termos (ativo_id, responsavel_id, status, observacoes) "
                + "VALUES (?, ?, ?, ?)";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setInt(1, termo.getAtivoId());
            stmt.setInt(2, termo.getResponsavelId());
            stmt.setString(3, termo.getStatus() != null ? termo.getStatus() : "Pendente");
            stmt.setString(4, termo.getObservacoes());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar termo.", e);
        }
    }

    // Retorna todos os termos, mais recentes primeiro
    public List<Termo> listarTodos() {

        List<Termo> termos = new ArrayList<>();
        String sql = "SELECT * FROM termos ORDER BY data_emissao DESC";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                termos.add(mapearTermo(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar termos.", e);
        }

        return termos;
    }

    // Retorna o histórico de termos de um ativo específico
    public List<Termo> listarPorAtivo(int ativoId) {

        List<Termo> termos = new ArrayList<>();
        String sql = "SELECT * FROM termos WHERE ativo_id = ? ORDER BY data_emissao DESC";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setInt(1, ativoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    termos.add(mapearTermo(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar termos do ativo.", e);
        }

        return termos;
    }

    // Transição de status: Pendente -> Assinado (não existe update genérico de propósito,
    // termo é documento formal, só essa transição específica é permitida)
    public void marcarComoAssinado(int id) {

        String sql = "UPDATE termos SET status = 'Assinado' WHERE id = ?";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao marcar termo como assinado.", e);
        }
    }

    private Termo mapearTermo(ResultSet rs) throws SQLException {

        Termo termo = new Termo();
        termo.setId(rs.getInt("id"));
        termo.setAtivoId(rs.getInt("ativo_id"));
        termo.setResponsavelId(rs.getInt("responsavel_id"));
        termo.setStatus(rs.getString("status"));
        termo.setObservacoes(rs.getString("observacoes"));

        Timestamp dataEmissao = rs.getTimestamp("data_emissao");
        if (dataEmissao != null) {
            termo.setDataEmissao(dataEmissao.toLocalDateTime());
        }

        return termo;
    }
}