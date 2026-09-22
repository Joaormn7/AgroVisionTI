package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.dao.ColaboradorDAO;
import br.com.agrovisionti.dao.MovimentacaoDAO;
import br.com.agrovisionti.model.Ativo;
import br.com.agrovisionti.model.Colaborador;
import br.com.agrovisionti.model.Movimentacao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TelaMovimentacoes extends JFrame {

    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();
    private final AtivoDAO ativoDAO = new AtivoDAO();
    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JButton btnNovaMovimentacao;
    private JButton btnAtualizar;
    private JButton btnVoltar;

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblQuantidade;

    public TelaMovimentacoes() {
        setTitle("AgroVisionTI - Movimentações");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
        topo.setBackground(new Color(5, 150, 105));
        topo.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));

        JLabel tituloTopo = new JLabel("AgroVisionTI - Movimentações");
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

        JLabel titulo = new JLabel("Histórico de Movimentações");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(new Color(30, 41, 59));

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);

        btnNovaMovimentacao = new JButton("Nova Movimentação");
        btnAtualizar = new JButton("Atualizar");

        botoes.add(btnNovaMovimentacao);
        botoes.add(btnAtualizar);

        JPanel linha = new JPanel(new BorderLayout());
        linha.setOpaque(false);
        linha.add(botoes, BorderLayout.EAST);

        areaSuperior.add(titulo, BorderLayout.NORTH);
        areaSuperior.add(linha, BorderLayout.SOUTH);

        modeloTabela = new DefaultTableModel(new Object[]{
                "ID", "Ativo", "Unidade Origem", "Unidade Destino",
                "Responsável Origem", "Responsável Destino", "Data", "Observações"
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

        JScrollPane scroll = new JScrollPane(tabela);

        conteudo.add(areaSuperior, BorderLayout.NORTH);
        conteudo.add(scroll, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBorder(BorderFactory.createEmptyBorder(12, 40, 18, 40));
        rodape.setBackground(new Color(245, 247, 250));

        lblQuantidade = new JLabel("0 movimentações registradas");
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rodape.add(lblQuantidade, BorderLayout.WEST);

        raiz.add(topo, BorderLayout.NORTH);
        raiz.add(conteudo, BorderLayout.CENTER);
        raiz.add(rodape, BorderLayout.SOUTH);

        add(raiz);
    }

    private void configurarEventos() {
        btnVoltar.addActionListener(e -> dispose());
        btnNovaMovimentacao.addActionListener(e -> new TelaCadastroMovimentacao(this));
        btnAtualizar.addActionListener(e -> carregarTabela());
    }

    public void carregarTabela() {
        List<Movimentacao> movimentacoes = movimentacaoDAO.listarTodas();
        modeloTabela.setRowCount(0);

        for (Movimentacao mov : movimentacoes) {
            modeloTabela.addRow(new Object[]{
                    mov.getId(),
                    descreverAtivo(mov.getAtivoId()),
                    mov.getUnidadeOrigemNome() != null ? mov.getUnidadeOrigemNome() : "-",
                    mov.getUnidadeDestinoNome(),
                    descreverColaborador(mov.getResponsavelOrigemId()),
                    descreverColaborador(mov.getResponsavelDestinoId()),
                    mov.getDataMovimentacao() != null ? mov.getDataMovimentacao().format(FORMATO_DATA) : "-",
                    mov.getObservacoes()
            });
        }

        lblQuantidade.setText(movimentacoes.size() + " movimentação(ões) registrada(s)");
    }

    private String descreverAtivo(int ativoId) {
        Ativo ativo = ativoDAO.buscarPorId(ativoId);

        if (ativo == null) {
            return "Ativo #" + ativoId;
        }

        return ativo.getTipo() + " - " + ativo.getMarca() + " " + ativo.getModelo();
    }

    private String descreverColaborador(Integer colaboradorId) {
        if (colaboradorId == null) {
            return "-";
        }

        Colaborador colaborador = colaboradorDAO.buscarPorId(colaboradorId);

        return colaborador != null ? colaborador.getNome() : "Colaborador #" + colaboradorId;
    }
}