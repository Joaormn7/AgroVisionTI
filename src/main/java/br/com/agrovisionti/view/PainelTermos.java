package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.dao.ColaboradorDAO;
import br.com.agrovisionti.dao.TermoDAO;
import br.com.agrovisionti.model.Ativo;
import br.com.agrovisionti.model.Colaborador;
import br.com.agrovisionti.model.Termo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PainelTermos extends JPanel {

    private final TermoDAO termoDAO = new TermoDAO();
    private final AtivoDAO ativoDAO = new AtivoDAO();
    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private final Window janelaProprietaria;
    private final boolean podeEditar;
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JButton btnNovoTermo;
    private JButton btnMarcarAssinado;
    private JButton btnAtualizar;

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblQuantidade;

    public PainelTermos(Window janelaProprietaria, boolean podeEditar) {
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

        JLabel titulo = new JLabel("Termos de Responsabilidade");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Cores.TITULO);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);

        btnNovoTermo = new JButton("Novo Termo");
        btnMarcarAssinado = new JButton("Marcar como Assinado");
        btnAtualizar = new JButton("Atualizar");

        botoes.add(btnNovoTermo);
        botoes.add(btnMarcarAssinado);
        botoes.add(btnAtualizar);

        if (!podeEditar) {
            btnNovoTermo.setEnabled(false);
            btnMarcarAssinado.setEnabled(false);
        }

        areaSuperior.add(titulo, BorderLayout.WEST);
        areaSuperior.add(botoes, BorderLayout.EAST);

        modeloTabela = new DefaultTableModel(new Object[]{
                "ID", "Ativo", "Responsável", "Status", "Data Emissão", "Observações"
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

        lblQuantidade = new JLabel("0 termo(s) cadastrado(s)");
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rodape.add(lblQuantidade, BorderLayout.WEST);

        add(conteudo, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnNovoTermo.addActionListener(e -> new TelaCadastroTermo(janelaProprietaria, this::carregarTabela));
        btnMarcarAssinado.addActionListener(e -> marcarSelecionadoComoAssinado());
        btnAtualizar.addActionListener(e -> carregarTabela());
    }

    public void carregarTabela() {
        List<Termo> termos = termoDAO.listarTodos();
        modeloTabela.setRowCount(0);

        for (Termo termo : termos) {
            modeloTabela.addRow(new Object[]{
                    termo.getId(),
                    descreverAtivo(termo.getAtivoId()),
                    descreverColaborador(termo.getResponsavelId()),
                    termo.getStatus(),
                    termo.getDataEmissao() != null ? termo.getDataEmissao().format(FORMATO_DATA) : "-",
                    termo.getObservacoes()
            });
        }

        lblQuantidade.setText(termos.size() + " termo(s) cadastrado(s)");
    }

    private void marcarSelecionadoComoAssinado() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um termo na tabela.");
            return;
        }

        String statusAtual = modeloTabela.getValueAt(linha, 3).toString();

        if ("Assinado".equals(statusAtual)) {
            JOptionPane.showMessageDialog(this, "Este termo já está assinado.");
            return;
        }

        int id = Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());

        int opcao = JOptionPane.showConfirmDialog(this, "Confirmar assinatura deste termo?",
                "Confirmação", JOptionPane.YES_NO_OPTION);

        if (opcao == JOptionPane.YES_OPTION) {
            termoDAO.marcarComoAssinado(id);
            carregarTabela();
            JOptionPane.showMessageDialog(this, "Termo marcado como assinado.");
        }
    }

    private String descreverAtivo(int ativoId) {
        Ativo ativo = ativoDAO.buscarPorId(ativoId);

        if (ativo == null) {
            return "Ativo #" + ativoId;
        }

        return ativo.getTipo() + " - " + ativo.getMarca() + " " + ativo.getModelo();
    }

    private String descreverColaborador(int colaboradorId) {
        Colaborador colaborador = colaboradorDAO.buscarPorId(colaboradorId);

        return colaborador != null ? colaborador.getNome() : "Colaborador #" + colaboradorId;
    }
}