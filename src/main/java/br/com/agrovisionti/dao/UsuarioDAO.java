package br.com.agrovisionti.dao;

import br.com.agrovisionti.connection.Conexao;
import br.com.agrovisionti.model.Usuario;
import br.com.agrovisionti.util.SenhaUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // Retorna o usuário autenticado (com perfil e dados) em caso de sucesso, ou null se falhar.
    // Usuários desativados não conseguem logar, mesmo com senha correta.
    public Usuario autenticar(String email, String senhaDigitada) {

        String sql = "SELECT * FROM usuarios WHERE email = ? AND ativo = true";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return null;
            }

            Usuario usuario = mapearUsuario(rs);

            if (SenhaUtil.verificar(senhaDigitada, usuario.getSenha(), usuario.getSalt())) {
                return usuario;
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar usuário.", e);
        }
    }

    // Mantido por compatibilidade com o fluxo de login atual
    public boolean validarLogin(String email, String senhaDigitada) {
        return autenticar(email, senhaDigitada) != null;
    }

    public void cadastrar(Usuario usuario, String senhaPlano) {
        String salt = SenhaUtil.gerarSalt();
        String hash = SenhaUtil.gerarHash(senhaPlano, salt);

        String sql = "INSERT INTO usuarios (nome, email, senha, salt, perfil) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, hash);
            stmt.setString(4, salt);
            stmt.setString(5, usuario.getPerfil());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar usuário.", e);
        }
    }

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nome";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários.", e);
        }

        return usuarios;
    }

    // Atualiza nome/email/perfil, sem mexer na senha
    public void atualizar(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, perfil = ? WHERE id = ?";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getPerfil());
            stmt.setInt(4, usuario.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar usuário.", e);
        }
    }

    public void definirSenha(String email, String novaSenha) {
        String salt = SenhaUtil.gerarSalt();
        String hash = SenhaUtil.gerarHash(novaSenha, salt);

        String sql = "UPDATE usuarios SET senha = ?, salt = ? WHERE email = ?";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setString(1, hash);
            stmt.setString(2, salt);
            stmt.setString(3, email);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao definir senha.", e);
        }
    }

    public void desativar(int id) {
        alterarStatus(id, false);
    }

    public void reativar(int id) {
        alterarStatus(id, true);
    }

    private void alterarStatus(int id, boolean ativo) {
        String sql = "UPDATE usuarios SET ativo = ? WHERE id = ?";

        try (
                Connection conexao = Conexao.obterConexao();
                PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
            stmt.setBoolean(1, ativo);
            stmt.setInt(2, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao alterar status do usuário.", e);
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setSalt(rs.getString("salt"));
        usuario.setPerfil(rs.getString("perfil"));
        usuario.setAtivo(rs.getBoolean("ativo"));
        return usuario;
    }
}