package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.UnidadeDAO;
import br.com.agrovisionti.model.Unidade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelUnidades extends JPanel {

    private final UnidadeDAO unidadeDAO = new UnidadeDAO();
    private final Window janelaProprietaria;
    private final boolean podeEditar;

    private JButton btnNova;
    private JButton btnEditar;
    private JButton btnDesativar;
    private JButton btnReativar;
    private JButton btnAtualizar;

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblQuantidade;

    public PainelUnidades(Window janelaProprietaria, boolean podeEditar) {
        this.janelaProprietaria = janelaProprietaria;
        this.podeEditar = podeEditar;
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

        JLabel titulo = new JLabel("Gerenciamento de Unidades");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Cores.TITULO);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);

        btnNova = new JButton("Nova Unidade");
        btnEditar = new JButton("Editar");
        btnDesativar = new JButton("Desativar");
        btnReativar = new JButton("Reativar");
        btnAtualizar = new JButton("Atualizar");

        botoes.add(btnNova);
        botoes.add(btnEditar);
        botoes.add(btnDesativar);
        botoes.add(btnReativar);
        botoes.add(btnAtualizar);

        if (!podeEditar) {
            btnNova.setEnabled(false);
            btnEditar.setEnabled(false);
            btnDesativar.setEnabled(false);
            btnReativar.setEnabled(false);
        }

        areaSuperior.add(titulo, BorderLayout.WEST);
        areaSuperior.add(botoes, BorderLayout.EAST);

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Tipo", "Status"}, 0) {
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

        lblQuantidade = new JLabel("0 unidade(s) cadastrada(s)");
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rodape.add(lblQuantidade, BorderLayout.WEST);

        add(conteudo, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnNova.addActionListener(e -> new TelaCadastroUnidade(janelaProprietaria, this::carregarTabela));
        btnEditar.addActionListener(e -> editarSelecionada());
        btnDesativar.addActionListener(e -> alterarStatusSelecionada(false));
        btnReativar.addActionListener(e -> alterarStatusSelecionada(true));
        btnAtualizar.addActionListener(e -> carregarTabela());
    }

    public void carregarTabela() {
        List<Unidade> unidades = unidadeDAO.listarTodas();
        modeloTabela.setRowCount(0);

        for (Unidade unidade : unidades) {
            modeloTabela.addRow(new Object[]{
                    unidade.getId(),
                    unidade.getNome(),
                    unidade.getTipo(),
                    unidade.isAtivo() ? "Ativa" : "Desativada"
            });
        }

        lblQuantidade.setText(unidades.size() + " unidade(s) cadastrada(s)");
    }

    private int obterIdSelecionado() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma unidade na tabela.");
            return -1;
        }

        return Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
    }

    private void editarSelecionada() {
        int id = obterIdSelecionado();

        if (id == -1) return;

        Unidade unidade = unidadeDAO.listarTodas().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);

        if (unidade == null) {
            JOptionPane.showMessageDialog(this, "Unidade não encontrada.");
            return;
        }

        new TelaCadastroUnidade(janelaProprietaria, unidade, this::carregarTabela);
    }

    private void alterarStatusSelecionada(boolean ativar) {
        int id = obterIdSelecionado();

        if (id == -1) return;

        if (ativar) {
            unidadeDAO.reativar(id);
        } else {
            unidadeDAO.desativar(id);
        }

        carregarTabela();
    }
}