package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.model.Ativo;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaAtivos extends JFrame {
    private final AtivoDAO ativoDAO = new AtivoDAO();

    private JTextField txtPesquisa;
    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnHistorico;
    private JButton btnAtualizar;
    private JButton btnVoltar;

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblQuantidade;

    public TelaAtivos() {
        setTitle("AgroVisionTI - Ativos");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        criarComponentes();
        configurarEventos();
        carregarTabela();
        setVisible(true);
    }

    private void criarComponentes() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(new Color(245, 247, 250));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(37, 99, 235));
        topo.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));

        JLabel tituloTopo = new JLabel("AgroVisionTI - Ativos");
        tituloTopo.setForeground(Color.WHITE);
        tituloTopo.setFont(new Font("Segoe UI", Font.BOLD, 23));

        btnVoltar = new JButton("Voltar");

        topo.add(tituloTopo, BorderLayout.WEST);
        topo.add(btnVoltar, BorderLayout.EAST);

        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setBackground(new Color(245, 247, 250));
        conteudo.setBorder(BorderFactory.createEmptyBorder(26, 40, 20, 40));

        JPanel areaSuperior = new JPanel(new BorderLayout(20, 14));
        areaSuperior.setOpaque(false);

        JLabel titulo = new JLabel("Gerenciamento de Ativos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(new Color(30, 41, 59));

        JPanel pesquisa = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pesquisa.setOpaque(false);

        JLabel lblPesquisa = new JLabel("Pesquisar:");
        lblPesquisa.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtPesquisa = new JTextField();
        txtPesquisa.setPreferredSize(new Dimension(330, 36));

        pesquisa.add(lblPesquisa);
        pesquisa.add(txtPesquisa);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);

        btnNovo = new JButton("Novo Ativo");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnHistorico = new JButton("Ver Histórico");
        btnAtualizar = new JButton("Atualizar");

        botoes.add(btnNovo);
        botoes.add(btnEditar);
        botoes.add(btnExcluir);
        botoes.add(btnHistorico);
        botoes.add(btnAtualizar);

        JPanel linha = new JPanel(new BorderLayout());
        linha.setOpaque(false);
        linha.add(pesquisa, BorderLayout.WEST);
        linha.add(botoes, BorderLayout.EAST);

        areaSuperior.add(titulo, BorderLayout.NORTH);
        areaSuperior.add(linha, BorderLayout.SOUTH);

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Tipo", "Marca", "Modelo", "Nº Série", "Unidade", "Responsável", "Status"}, 0) {
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
        rodape.setBorder(BorderFactory.createEmptyBorder(12, 40, 18, 40));
        rodape.setBackground(new Color(245, 247, 250));

        lblQuantidade = new JLabel("0 ativos cadastrados");
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rodape.add(lblQuantidade, BorderLayout.WEST);

        raiz.add(topo, BorderLayout.NORTH);
        raiz.add(conteudo, BorderLayout.CENTER);
        raiz.add(rodape, BorderLayout.SOUTH);

        add(raiz);
    }

    private void configurarEventos() {
        btnVoltar.addActionListener(e -> {
            dispose();
            new TelaDashboard();
        });

        btnNovo.addActionListener(e -> new TelaCadastroAtivo(this));
        btnEditar.addActionListener(e -> editarAtivoSelecionado());
        btnExcluir.addActionListener(e -> excluirAtivoSelecionado());
        btnHistorico.addActionListener(e -> verHistoricoSelecionado());
        btnAtualizar.addActionListener(e -> carregarTabela());

        txtPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { pesquisar(); }
            public void removeUpdate(DocumentEvent e) { pesquisar(); }
            public void changedUpdate(DocumentEvent e) { pesquisar(); }
        });
    }

    public void carregarTabela() {
        preencherTabela(ativoDAO.listar());
    }

    private void pesquisar() {
        String termo = txtPesquisa.getText().trim();

        if (termo.isEmpty()) {
            carregarTabela();
            return;
        }

        preencherTabela(ativoDAO.pesquisar(termo));
    }

    private void preencherTabela(List<Ativo> ativos) {
        modeloTabela.setRowCount(0);

        for (Ativo ativo : ativos) {
            modeloTabela.addRow(new Object[]{
                    ativo.getId(),
                    ativo.getTipo(),
                    ativo.getMarca(),
                    ativo.getModelo(),
                    ativo.getNumeroSerie(),
                    ativo.getUnidade(),
                    ativo.getResponsavel(),
                    ativo.getStatus()
            });
        }

        lblQuantidade.setText(ativos.size() + " ativo(s) encontrado(s)");
    }

    private int obterIdSelecionado() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um ativo na tabela.");
            return -1;
        }

        return Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
    }

    private void editarAtivoSelecionado() {
        int id = obterIdSelecionado();

        if (id == -1) return;

        Ativo ativo = ativoDAO.buscarPorId(id);

        if (ativo == null) {
            JOptionPane.showMessageDialog(this, "Ativo não encontrado.");
            return;
        }

        new TelaCadastroAtivo(this, ativo);
    }

    private void excluirAtivoSelecionado() {
        int id = obterIdSelecionado();

        if (id == -1) return;

        int opcao = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este ativo?", "Confirmação", JOptionPane.YES_NO_OPTION);

        if (opcao == JOptionPane.YES_OPTION) {
            ativoDAO.excluir(id);
            carregarTabela();
            JOptionPane.showMessageDialog(this, "Ativo excluído com sucesso.");
        }
    }

    private void verHistoricoSelecionado() {
        int id = obterIdSelecionado();

        if (id == -1) return;

        new TelaHistorico(id);
    }
}