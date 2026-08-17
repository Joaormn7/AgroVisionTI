package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.UsuarioDAO;
import br.com.agrovisionti.model.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelUsuarios extends JPanel {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Window janelaProprietaria;

    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnDesativar;
    private JButton btnReativar;
    private JButton btnAtualizar;

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblQuantidade;

    public PainelUsuarios(Window janelaProprietaria) {
        this.janelaProprietaria = janelaProprietaria;
        setLayout(new BorderLayout());
        setBackground(Cores.FUNDO);
        criarComponentes();
        configurarEventos();
        carregarTabela();
    }

    private void criarComponentes() {
        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setBackground(Cores.FUNDO);
        conteudo.setBorder(BorderFactory.createEmptyBorder(26, 32, 20, 32));

        JPanel areaSuperior = new JPanel(new BorderLayout(20, 14));
        areaSuperior.setOpaque(false);

        JLabel titulo = new JLabel("Gerenciamento de Usuários");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Cores.TITULO);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);

        btnNovo = new JButton("Novo Usuário");
        btnEditar = new JButton("Editar");
        btnDesativar = new JButton("Desativar");
        btnReativar = new JButton("Reativar");
        btnAtualizar = new JButton("Atualizar");

        botoes.add(btnNovo);
        botoes.add(btnEditar);
        botoes.add(btnDesativar);
        botoes.add(btnReativar);
        botoes.add(btnAtualizar);

        areaSuperior.add(titulo, BorderLayout.WEST);
        areaSuperior.add(botoes, BorderLayout.EAST);

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "E-mail", "Perfil", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(30);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tabela);

        conteudo.add(areaSuperior, BorderLayout.NORTH);
        conteudo.add(scroll, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBorder(BorderFactory.createEmptyBorder(12, 32, 12, 32));
        rodape.setBackground(Cores.FUNDO);

        lblQuantidade = new JLabel("0 usuário(s) cadastrado(s)");
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rodape.add(lblQuantidade, BorderLayout.WEST);

        add(conteudo, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnNovo.addActionListener(e -> new TelaCadastroUsuario(janelaProprietaria, this::carregarTabela));
        btnEditar.addActionListener(e -> editarSelecionado());
        btnDesativar.addActionListener(e -> alterarStatusSelecionado(false));
        btnReativar.addActionListener(e -> alterarStatusSelecionado(true));
        btnAtualizar.addActionListener(e -> carregarTabela());
    }

    public void carregarTabela() {
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        modeloTabela.setRowCount(0);

        for (Usuario usuario : usuarios) {
            modeloTabela.addRow(new Object[]{
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getEmail(),
                    usuario.getPerfil(),
                    usuario.isAtivo() ? "Ativo" : "Desativado"
            });
        }

        lblQuantidade.setText(usuarios.size() + " usuário(s) cadastrado(s)");
    }

    private int obterIdSelecionado() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela.");
            return -1;
        }

        return Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
    }

    private void editarSelecionado() {
        int id = obterIdSelecionado();

        if (id == -1) return;

        Usuario usuario = usuarioDAO.listarTodos().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);

        if (usuario == null) {
            JOptionPane.showMessageDialog(this, "Usuário não encontrado.");
            return;
        }

        new TelaCadastroUsuario(janelaProprietaria, usuario, this::carregarTabela);
    }

    private void alterarStatusSelecionado(boolean ativar) {
        int id = obterIdSelecionado();

        if (id == -1) return;

        if (ativar) {
            usuarioDAO.reativar(id);
        } else {
            int opcao = JOptionPane.showConfirmDialog(this, "Deseja realmente desativar este usuário?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);

            if (opcao != JOptionPane.YES_OPTION) return;

            usuarioDAO.desativar(id);
        }

        carregarTabela();
    }
}