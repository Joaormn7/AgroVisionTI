package br.com.agrovisionti.dao;

import br.com.agrovisionti.connection.Conexao;
import br.com.agrovisionti.model.Movimentacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    public boolean cadastrar(Movimentacao movimentacao) {
        String sqlInserirMovimentacao = "INSERT INTO movimentacoes "
                + "(ativo_id, unidade_origem, unidade_destino, responsavel_origem, responsavel_destino, observacoes) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        String sqlAtualizarAtivo = "UPDATE ativos SET unidade = ?, responsavel = ? WHERE id = ?";

        try (Connection conexao = Conexao.obterConexao()) {
            conexao.setAutoCommit(false);

            inserirMovimentacao(conexao, sqlInserirMovimentacao, movimentacao);
            atualizarLocalizacaoAtivo(conexao, sqlAtualizarAtivo, movimentacao);

            conexao.commit();
            return true;

        } catch (SQLException erro) {
            erro.printStackTrace();
            return false;
        }
    }

    private void inserirMovimentacao(Connection conexao, String sql, Movimentacao movimentacao) throws SQLException {
        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setInt(1, movimentacao.getAtivoId());
            statement.setString(2, movimentacao.getUnidadeOrigem());
            statement.setString(3, movimentacao.getUnidadeDestino());
            statement.setString(4, movimentacao.getResponsavelOrigem());
            statement.setString(5, movimentacao.getResponsavelDestino());
            statement.setString(6, movimentacao.getObservacoes());
            statement.executeUpdate();
        }
    }

    private void atualizarLocalizacaoAtivo(Connection conexao, String sql, Movimentacao movimentacao) throws SQLException {
        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setString(1, movimentacao.getUnidadeDestino());
            statement.setString(2, movimentacao.getResponsavelDestino());
            statement.setInt(3, movimentacao.getAtivoId());
            statement.executeUpdate();
        }
    }

    public List<Movimentacao> listarTodas() {
        String sql = "SELECT * FROM movimentacoes ORDER BY data_movimentacao DESC";
        return executarConsulta(sql, null);
    }

    public List<Movimentacao> listarPorAtivo(int ativoId) {
        String sql = "SELECT * FROM movimentacoes WHERE ativo_id = ? ORDER BY data_movimentacao DESC";
        return executarConsulta(sql, ativoId);
    }

    private List<Movimentacao> executarConsulta(String sql, Integer ativoId) {
        List<Movimentacao> movimentacoes = new ArrayList<>();

        try (Connection conexao = Conexao.obterConexao();
             PreparedStatement statement = conexao.prepareStatement(sql)) {

            if (ativoId != null) {
                statement.setInt(1, ativoId);
            }

            try (ResultSet resultado = statement.executeQuery()) {
                while (resultado.next()) {
                    movimentacoes.add(mapearMovimentacao(resultado));
                }
            }

        } catch (SQLException erro) {
            erro.printStackTrace();
        }

        return movimentacoes;
    }

    private Movimentacao mapearMovimentacao(ResultSet resultado) throws SQLException {
        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setId(resultado.getInt("id"));
        movimentacao.setAtivoId(resultado.getInt("ativo_id"));
        movimentacao.setUnidadeOrigem(resultado.getString("unidade_origem"));
        movimentacao.setUnidadeDestino(resultado.getString("unidade_destino"));
        movimentacao.setResponsavelOrigem(resultado.getString("responsavel_origem"));
        movimentacao.setResponsavelDestino(resultado.getString("responsavel_destino"));
        movimentacao.setObservacoes(resultado.getString("observacoes"));

        Timestamp dataMovimentacao = resultado.getTimestamp("data_movimentacao");
        if (dataMovimentacao != null) {
            movimentacao.setDataMovimentacao(dataMovimentacao.toLocalDateTime());
        }

        return movimentacao;
    }
}