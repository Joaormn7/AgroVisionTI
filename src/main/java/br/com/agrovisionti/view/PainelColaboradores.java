package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.ColaboradorDAO;
import br.com.agrovisionti.dao.UnidadeDAO;
import br.com.agrovisionti.model.Colaborador;
import br.com.agrovisionti.model.Unidade;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelColaboradores extends JPanel {

    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private final UnidadeDAO unidadeDAO = new UnidadeDAO();
    private final Window janelaProprietaria;
    private final boolean podeEditar;

    private JTextField txtPesquisa;
    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnDesativar;
    private JButton btnReativar;
    private JButton btnAtualizar;

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblQuantidade;

    public PainelColaboradores(Window janelaProprietaria, boolean podeEditar) {
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

        JLabel titulo = new JLabel("Gerenciamento de Colaboradores");
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

        btnNovo = new JButton("Novo Colaborador");
        btnEditar = new JButton("Editar");
        btnDesativar = new JButton("Desativar");
        btnReativar = new JButton("Reativar");
        btnAtualizar = new JButton("Atualizar");

        botoes.add(btnNovo);
        botoes.add(btnEditar);
        botoes.add(btnDesativar);
        botoes.add(btnReativar);
        botoes.add(btnAtualizar);

        if (!podeEditar) {
            btnNovo.setEnabled(false);
            btnEditar.setEnabled(false);
            btnDesativar.setEnabled(false);
            btnReativar.setEnabled(false);
        }

        JPanel linha = new JPanel(new BorderLayout());
        linha.setOpaque(false);
        linha.add(pesquisa, BorderLayout.WEST);
        linha.add(botoes, BorderLayout.EAST);

        areaSuperior.add(titulo, BorderLayout.NORTH);
        areaSuperior.add(linha, BorderLayout.SOUTH);

        modeloTabela = new DefaultTableModel(new Object[]{
                "ID", "Nome", "CPF", "Cargo", "Setor", "Unidade", "Status"
        }, 0) {
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

        lblQuantidade = new JLabel("0 colaborador(es) cadastrado(s)");
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rodape.add(lblQuantidade, BorderLayout.WEST);

        add(conteudo, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnNovo.addActionListener(e -> new TelaCadastroColaborador(janelaProprietaria, this::carregarTabela));
        btnEditar.addActionListener(e -> editarSelecionado());
        btnDesativar.addActionListener(e -> alterarStatusSelecionado(false));
        btnReativar.addActionListener(e -> alterarStatusSelecionado(true));
        btnAtualizar.addActionListener(e -> carregarTabela());

        txtPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { pesquisar(); }
            public void removeUpdate(DocumentEvent e) { pesquisar(); }
            public void changedUpdate(DocumentEvent e) { pesquisar(); }
        });
    }

    public void carregarTabela() {
        preencherTabela(colaboradorDAO.listarTodos());
    }

    private void pesquisar() {
        String termo = txtPesquisa.getText().trim();

        if (termo.isEmpty()) {
            carregarTabela();
            return;
        }

        preencherTabela(colaboradorDAO.pesquisar(termo));
    }

    private void preencherTabela(List<Colaborador> colaboradores) {
        modeloTabela.setRowCount(0);

        for (Colaborador colaborador : colaboradores) {
            modeloTabela.addRow(new Object[]{
                    colaborador.getId(),
                    colaborador.getNome(),
                    colaborador.getCpf(),
                    colaborador.getCargo(),
                    colaborador.getSetor(),
                    descreverUnidade(colaborador.getUnidadeId()),
                    colaborador.isAtivo() ? "Ativo" : "Desativado"
            });
        }

        lblQuantidade.setText(colaboradores.size() + " colaborador(es) encontrado(s)");
    }

    private String descreverUnidade(int unidadeId) {
        Unidade unidade = unidadeDAO.listarTodas().stream()
                .filter(u -> u.getId() == unidadeId)
                .findFirst()
                .orElse(null);

        return unidade != null ? unidade.getNome() : "Unidade #" + unidadeId;
    }

    private int obterIdSelecionado() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um colaborador na tabela.");
            return -1;
        }

        return Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
    }

    private void editarSelecionado() {
        int id = obterIdSelecionado();

        if (id == -1) return;

        Colaborador colaborador = colaboradorDAO.buscarPorId(id);

        if (colaborador == null) {
            JOptionPane.showMessageDialog(this, "Colaborador não encontrado.");
            return;
        }

        new TelaCadastroColaborador(janelaProprietaria, colaborador, this::carregarTabela);
    }

    private void alterarStatusSelecionado(boolean ativar) {
        int id = obterIdSelecionado();

        if (id == -1) return;

        if (ativar) {
            colaboradorDAO.reativar(id);
        } else {
            int opcao = JOptionPane.showConfirmDialog(this, "Deseja realmente desativar este colaborador?",
                    "Confirmação", JOptionPane.YES_NO_OPTION);

            if (opcao != JOptionPane.YES_OPTION) return;

            colaboradorDAO.desativar(id);
        }

        carregarTabela();
    }
}