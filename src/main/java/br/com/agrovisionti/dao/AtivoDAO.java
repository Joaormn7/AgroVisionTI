package br.com.agrovisionti.dao;

import br.com.agrovisionti.connection.Conexao;
import br.com.agrovisionti.model.Ativo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AtivoDAO {

    public void salvar(Ativo ativo) {

        String sql = """
                INSERT INTO ativos
                (tipo, marca, modelo, numero_serie, unidade,
                 responsavel_id, status, observacoes)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, ativo.getTipo());
            stmt.setString(2, ativo.getMarca());
            stmt.setString(3, ativo.getModelo());
            stmt.setString(4, ativo.getNumeroSerie());
            stmt.setString(5, ativo.getUnidade());

            if (ativo.getResponsavelId() != null) {
                stmt.setInt(6, ativo.getResponsavelId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, ativo.getStatus());
            stmt.setString(8, ativo.getObservacoes());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar ativo.", e);
        }
    }

    public List<Ativo> listar() {

        List<Ativo> ativos = new ArrayList<>();

        String sql = "SELECT * FROM ativos WHERE excluido = false ORDER BY id DESC";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {
                ativos.add(mapearAtivo(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar ativos.", e);
        }

        return ativos;
    }

    public Ativo buscarPorId(int id) {

        String sql = "SELECT * FROM ativos WHERE id = ?";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearAtivo(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar ativo.", e);
        }

        return null;
    }

    public List<Ativo> pesquisar(String pesquisa) {

        List<Ativo> ativos = new ArrayList<>();

        String sql = """
                SELECT *
                FROM ativos
                WHERE excluido = false
                  AND (tipo LIKE ?
                   OR marca LIKE ?
                   OR modelo LIKE ?
                   OR unidade LIKE ?
                   OR status LIKE ?)
                ORDER BY id DESC
                """;

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            String filtro = "%" + pesquisa + "%";

            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, filtro);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                ativos.add(mapearAtivo(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao pesquisar ativos.", e);
        }

        return ativos;
    }

    public void atualizar(Ativo ativo) {

        String sql = """
                UPDATE ativos
                SET tipo = ?,
                    marca = ?,
                    modelo = ?,
                    numero_serie = ?,
                    unidade = ?,
                    responsavel_id = ?,
                    status = ?,
                    observacoes = ?
                WHERE id = ?
                """;

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setString(1, ativo.getTipo());
            stmt.setString(2, ativo.getMarca());
            stmt.setString(3, ativo.getModelo());
            stmt.setString(4, ativo.getNumeroSerie());
            stmt.setString(5, ativo.getUnidade());

            if (ativo.getResponsavelId() != null) {
                stmt.setInt(6, ativo.getResponsavelId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, ativo.getStatus());
            stmt.setString(8, ativo.getObservacoes());
            stmt.setInt(9, ativo.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar ativo.", e);
        }
    }

    // Soft delete: marca o ativo como excluído em vez de apagar a linha do banco.
    // Preserva a integridade do histórico de movimentações (FK ativo_id) e evita
    // erro de violação de chave estrangeira ao excluir um ativo já movimentado.
    public void excluir(int id) {

        String sql = "UPDATE ativos SET excluido = true WHERE id = ?";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir ativo.", e);
        }
    }

    private Ativo mapearAtivo(ResultSet rs) throws SQLException {

        Ativo ativo = new Ativo();

        ativo.setId(rs.getInt("id"));
        ativo.setTipo(rs.getString("tipo"));
        ativo.setMarca(rs.getString("marca"));
        ativo.setModelo(rs.getString("modelo"));
        ativo.setNumeroSerie(rs.getString("numero_serie"));
        ativo.setUnidade(rs.getString("unidade"));

        int responsavelId = rs.getInt("responsavel_id");
        ativo.setResponsavelId(rs.wasNull() ? null : responsavelId);

        ativo.setStatus(rs.getString("status"));
        ativo.setObservacoes(rs.getString("observacoes"));

        return ativo;
    }
}