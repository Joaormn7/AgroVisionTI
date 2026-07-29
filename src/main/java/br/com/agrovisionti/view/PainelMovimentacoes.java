package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.dao.MovimentacaoDAO;
import br.com.agrovisionti.model.Ativo;
import br.com.agrovisionti.model.Movimentacao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PainelMovimentacoes extends JPanel {

    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();
    private final AtivoDAO ativoDAO = new AtivoDAO();
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private JButton btnNovaMovimentacao;
    private JButton btnAtualizar;

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblQuantidade;

    public PainelMovimentacoes() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        criarComponentes();
        configurarEventos();
        carregarTabela();
    }

    private void criarComponentes() {
        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setBackground(new Color(245, 247, 250));
        conteudo.setBorder(BorderFactory.createEmptyBorder(26, 32, 20, 32));

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
        rodape.setBorder(BorderFactory.createEmptyBorder(12, 32, 12, 32));
        rodape.setBackground(new Color(245, 247, 250));

        lblQuantidade = new JLabel("0 movimentações registradas");
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rodape.add(lblQuantidade, BorderLayout.WEST);

        add(conteudo, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
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
                    mov.getUnidadeOrigem(),
                    mov.getUnidadeDestino(),
                    mov.getResponsavelOrigem(),
                    mov.getResponsavelDestino(),
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
}