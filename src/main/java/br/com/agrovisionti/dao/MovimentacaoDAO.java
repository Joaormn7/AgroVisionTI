package br.com.agrovisionti.dao;

import br.com.agrovisionti.connection.Conexao;
import br.com.agrovisionti.model.Movimentacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoDAO {

    // Registra uma movimentação e atualiza a unidade/responsável atual do ativo.
    // As duas operações acontecem na mesma transação: ou as duas dão certo, ou nenhuma é salva.
    public boolean cadastrar(Movimentacao movimentacao) {
        String sqlInserirMovimentacao = "INSERT INTO movimentacoes "
                + "(ativo_id, unidade_origem_id, unidade_destino_id, responsavel_origem_id, responsavel_destino_id, observacoes) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        // Agora unidade_destino_id já chega como int — não precisa mais de subquery por nome.
        String sqlAtualizarAtivo = "UPDATE ativos SET unidade_id = ?, responsavel_id = ? WHERE id = ?";

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

            if (movimentacao.getUnidadeOrigemId() != null) {
                statement.setInt(2, movimentacao.getUnidadeOrigemId());
            } else {
                statement.setNull(2, Types.INTEGER);
            }

            statement.setInt(3, movimentacao.getUnidadeDestinoId());

            if (movimentacao.getResponsavelOrigemId() != null) {
                statement.setInt(4, movimentacao.getResponsavelOrigemId());
            } else {
                statement.setNull(4, Types.INTEGER);
            }

            statement.setInt(5, movimentacao.getResponsavelDestinoId());
            statement.setString(6, movimentacao.getObservacoes());
            statement.executeUpdate();
        }
    }

    private void atualizarLocalizacaoAtivo(Connection conexao, String sql, Movimentacao movimentacao) throws SQLException {
        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setInt(1, movimentacao.getUnidadeDestinoId());
            statement.setInt(2, movimentacao.getResponsavelDestinoId());
            statement.setInt(3, movimentacao.getAtivoId());
            statement.executeUpdate();
        }
    }

    // Retorna o histórico completo de movimentações, mais recentes primeiro.
    public List<Movimentacao> listarTodas() {
        String sql = """
                SELECT m.*, uo.nome AS unidade_origem_nome, ud.nome AS unidade_destino_nome
                FROM movimentacoes m
                LEFT JOIN unidades uo ON m.unidade_origem_id = uo.id
                JOIN unidades ud ON m.unidade_destino_id = ud.id
                ORDER BY m.data_movimentacao DESC
                """;
        return executarConsulta(sql, null);
    }

    // Retorna o histórico de movimentações de um ativo específico.
    public List<Movimentacao> listarPorAtivo(int ativoId) {
        String sql = """
                SELECT m.*, uo.nome AS unidade_origem_nome, ud.nome AS unidade_destino_nome
                FROM movimentacoes m
                LEFT JOIN unidades uo ON m.unidade_origem_id = uo.id
                JOIN unidades ud ON m.unidade_destino_id = ud.id
                WHERE m.ativo_id = ?
                ORDER BY m.data_movimentacao DESC
                """;
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

        int unidadeOrigemId = resultado.getInt("unidade_origem_id");
        movimentacao.setUnidadeOrigemId(resultado.wasNull() ? null : unidadeOrigemId);
        movimentacao.setUnidadeOrigemNome(resultado.getString("unidade_origem_nome"));

        movimentacao.setUnidadeDestinoId(resultado.getInt("unidade_destino_id"));
        movimentacao.setUnidadeDestinoNome(resultado.getString("unidade_destino_nome"));

        int responsavelOrigemId = resultado.getInt("responsavel_origem_id");
        movimentacao.setResponsavelOrigemId(resultado.wasNull() ? null : responsavelOrigemId);

        movimentacao.setResponsavelDestinoId(resultado.getInt("responsavel_destino_id"));
        movimentacao.setObservacoes(resultado.getString("observacoes"));

        Timestamp dataMovimentacao = resultado.getTimestamp("data_movimentacao");
        if (dataMovimentacao != null) {
            movimentacao.setDataMovimentacao(dataMovimentacao.toLocalDateTime());
        }

        return movimentacao;
    }
}