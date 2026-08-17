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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class PainelHistorico extends JPanel {

    private static final DateTimeFormatter FORMATO_EXIBICAO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_ENTRADA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();
    private final AtivoDAO ativoDAO = new AtivoDAO();
    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();

    private JTextField txtPesquisa;
    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    private JButton btnFiltrar;
    private JButton btnLimparFiltro;

    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblQuantidade;

    public PainelHistorico() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        criarComponentes();
        configurarEventos();
        carregarTabela();
    }

    private void criarComponentes() {
        JPanel conteudo = new JPanel(new BorderLayout(0, 14));
        conteudo.setBackground(new Color(245, 247, 250));
        conteudo.setBorder(BorderFactory.createEmptyBorder(24, 32, 20, 32));

        JPanel areaSuperior = new JPanel(new BorderLayout(0, 10));
        areaSuperior.setOpaque(false);

        JLabel titulo = new JLabel("Consultar Histórico");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(new Color(30, 41, 59));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        filtros.setOpaque(false);

        txtPesquisa = new JTextField(18);
        txtDataInicio = new JTextField(8);
        txtDataFim = new JTextField(8);
        btnFiltrar = new JButton("Filtrar");
        btnLimparFiltro = new JButton("Limpar Filtros");

        filtros.add(new JLabel("Ativo:"));
        filtros.add(txtPesquisa);
        filtros.add(new JLabel("De (dd/mm/aaaa):"));
        filtros.add(txtDataInicio);
        filtros.add(new JLabel("Até (dd/mm/aaaa):"));
        filtros.add(txtDataFim);
        filtros.add(btnFiltrar);
        filtros.add(btnLimparFiltro);

        areaSuperior.add(titulo, BorderLayout.NORTH);
        areaSuperior.add(filtros, BorderLayout.SOUTH);

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

        lblQuantidade = new JLabel("0 registro(s) encontrado(s)");
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rodape.add(lblQuantidade, BorderLayout.WEST);

        add(conteudo, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnFiltrar.addActionListener(e -> carregarTabela());
        btnLimparFiltro.addActionListener(e -> {
            txtPesquisa.setText("");
            txtDataInicio.setText("");
            txtDataFim.setText("");
            carregarTabela();
        });
    }

    public void carregarTabela() {
        List<Movimentacao> movimentacoes = movimentacaoDAO.listarTodas();

        movimentacoes = filtrarPorTexto(movimentacoes, txtPesquisa.getText().trim());
        movimentacoes = filtrarPorPeriodo(movimentacoes, txtDataInicio.getText().trim(), txtDataFim.getText().trim());

        preencherTabela(movimentacoes);
    }

    private List<Movimentacao> filtrarPorTexto(List<Movimentacao> movimentacoes, String texto) {
        if (texto.isEmpty()) {
            return movimentacoes;
        }

        String termo = texto.toLowerCase();

        return movimentacoes.stream()
                .filter(mov -> descreverAtivo(mov.getAtivoId()).toLowerCase().contains(termo))
                .collect(Collectors.toList());
    }

    private List<Movimentacao> filtrarPorPeriodo(List<Movimentacao> movimentacoes, String textoInicio, String textoFim) {
        LocalDate inicio = converterData(textoInicio);
        LocalDate fim = converterData(textoFim);

        if (inicio == null && fim == null) {
            return movimentacoes;
        }

        return movimentacoes.stream()
                .filter(mov -> dataDentroDoPeriodo(mov.getDataMovimentacao(), inicio, fim))
                .collect(Collectors.toList());
    }

    private boolean dataDentroDoPeriodo(LocalDateTime data, LocalDate inicio, LocalDate fim) {
        if (data == null) {
            return false;
        }

        LocalDate dataMovimentacao = data.toLocalDate();

        if (inicio != null && dataMovimentacao.isBefore(inicio)) {
            return false;
        }

        if (fim != null && dataMovimentacao.isAfter(fim)) {
            return false;
        }

        return true;
    }

    private LocalDate converterData(String texto) {
        if (texto.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(texto, FORMATO_ENTRADA);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida: " + texto + ". Use o formato dd/mm/aaaa.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void preencherTabela(List<Movimentacao> movimentacoes) {
        modeloTabela.setRowCount(0);

        for (Movimentacao mov : movimentacoes) {
            modeloTabela.addRow(new Object[]{
                    mov.getId(),
                    descreverAtivo(mov.getAtivoId()),
                    mov.getUnidadeOrigem(),
                    mov.getUnidadeDestino(),
                    descreverColaborador(mov.getResponsavelOrigemId()),
                    descreverColaborador(mov.getResponsavelDestinoId()),
                    mov.getDataMovimentacao() != null ? mov.getDataMovimentacao().format(FORMATO_EXIBICAO) : "-",
                    mov.getObservacoes()
            });
        }

        lblQuantidade.setText(movimentacoes.size() + " registro(s) encontrado(s)");
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