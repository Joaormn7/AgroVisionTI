package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.dao.ColaboradorDAO;
import br.com.agrovisionti.model.Ativo;
import br.com.agrovisionti.model.Colaborador;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelAtivos extends JPanel {

    private final AtivoDAO ativoDAO = new AtivoDAO();
    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private final Window janelaProprietaria;
    private final boolean podeEditar;

    private JTextField txtPesquisa;
    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnHistorico;
    private JButton btnAtualizar;

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblQuantidade;

    public PainelAtivos(Window janelaProprietaria, boolean podeEditar) {
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

        JLabel titulo = new JLabel("Gerenciamento de Ativos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Cores.TITULO);

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

        if (!podeEditar) {
            btnNovo.setEnabled(false);
            btnEditar.setEnabled(false);
            btnExcluir.setEnabled(false);
        }

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
        rodape.setBorder(BorderFactory.createEmptyBorder(12, 32, 12, 32));
        rodape.setBackground(Cores.FUNDO);

        lblQuantidade = new JLabel("0 ativos cadastrados");
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rodape.add(lblQuantidade, BorderLayout.WEST);

        add(conteudo, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnNovo.addActionListener(e -> new TelaCadastroAtivo(janelaProprietaria, this));
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
                    descreverResponsavel(ativo.getResponsavelId()),
                    ativo.getStatus()
            });
        }

        lblQuantidade.setText(ativos.size() + " ativo(s) encontrado(s)");
    }

    private String descreverResponsavel(Integer responsavelId) {
        if (responsavelId == null) {
            return "-";
        }

        Colaborador colaborador = colaboradorDAO.buscarPorId(responsavelId);

        return colaborador != null ? colaborador.getNome() : "Colaborador #" + responsavelId;
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

        new TelaCadastroAtivo(janelaProprietaria, this, ativo);
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