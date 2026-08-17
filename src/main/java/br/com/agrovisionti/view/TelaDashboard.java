package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.dao.MovimentacaoDAO;
import br.com.agrovisionti.model.Ativo;
import br.com.agrovisionti.model.Movimentacao;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TelaDashboard extends JFrame {

    private static final Color COR_SIDEBAR = new Color(6, 46, 33);
    private static final Color COR_SIDEBAR_ITEM_ATIVO = new Color(5, 150, 105);
    private static final Color COR_TEXTO_SIDEBAR = new Color(203, 213, 225);
    private static final Color COR_FUNDO = new Color(245, 247, 250);
    private static final Color COR_BORDA = new Color(226, 232, 240);
    private static final Color COR_TITULO = new Color(30, 41, 59);
    private static final Color COR_SUBTITULO = new Color(100, 116, 139);

    private static final Color COR_DISPONIVEL = new Color(34, 197, 94);
    private static final Color COR_EM_USO = new Color(101, 163, 13);
    private static final Color COR_MANUTENCAO = new Color(239, 68, 68);
    private static final Color COR_AGUARDANDO = new Color(245, 158, 11);
    private static final Color COR_TOTAL = new Color(100, 116, 139);
    private static final Color COR_PRIMARIA = new Color(5, 150, 105);

    private final AtivoDAO ativoDAO = new AtivoDAO();
    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();

    public TelaDashboard() {
        setTitle("AgroVisionTI - Dashboard");
        setSize(1400, 860);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        criarComponentes();
        setVisible(true);
    }

    private void criarComponentes() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(COR_FUNDO);

        raiz.add(construirSidebar(), BorderLayout.WEST);
        raiz.add(construirConteudo(), BorderLayout.CENTER);

        add(raiz);
    }

    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(230, getHeight()));

        JLabel logo = new JLabel("AgroVisionTI");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setBorder(BorderFactory.createEmptyBorder(24, 20, 4, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLogo = new JLabel("Gestão de Ativos");
        subLogo.setForeground(COR_TEXTO_SIDEBAR);
        subLogo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLogo.setBorder(BorderFactory.createEmptyBorder(0, 20, 24, 20));
        subLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rotuloMenu = new JLabel("MENU PRINCIPAL");
        rotuloMenu.setForeground(new Color(100, 116, 139));
        rotuloMenu.setFont(new Font("Segoe UI", Font.BOLD, 11));
        rotuloMenu.setBorder(BorderFactory.createEmptyBorder(0, 20, 8, 20));
        rotuloMenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton itemDashboard = criarItemMenu("Dashboard", true);
        JButton itemAtivos = criarItemMenu("Ativos", false);
        JButton itemMovimentacoes = criarItemMenu("Movimentações", false);
        JButton itemHistorico = criarItemMenu("Histórico", false);
        JButton itemRelatorios = criarItemMenu("Relatórios", false);
        JButton itemSair = criarItemMenu("Sair", false);

        itemAtivos.addActionListener(e -> {
            dispose();
            new TelaAtivos();
        });
        itemMovimentacoes.addActionListener(e -> new TelaMovimentacoes());
        itemHistorico.addActionListener(e -> new TelaHistorico());
        itemRelatorios.addActionListener(e -> new TelaRelatorios());
        itemSair.addActionListener(e -> {
            dispose();
            new TelaLogin();
        });

        sidebar.add(logo);
        sidebar.add(subLogo);
        sidebar.add(rotuloMenu);
        sidebar.add(itemDashboard);
        sidebar.add(itemAtivos);
        sidebar.add(itemMovimentacoes);
        sidebar.add(itemHistorico);
        sidebar.add(itemRelatorios);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(itemSair);
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    private JButton criarItemMenu(String texto, boolean ativo) {
        JButton item = new JButton(texto);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setForeground(ativo ? Color.WHITE : COR_TEXTO_SIDEBAR);
        item.setBackground(ativo ? COR_SIDEBAR_ITEM_ATIVO : COR_SIDEBAR);
        item.setBorderPainted(false);
        item.setFocusPainted(false);
        item.setContentAreaFilled(true);
        item.setOpaque(true);
        item.setHorizontalAlignment(SwingConstants.LEFT);
        item.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(230, 44));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return item;
    }

    private static class PainelConteudo extends JPanel implements Scrollable {
        PainelConteudo() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 100;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private JScrollPane construirConteudo() {
        JPanel conteudo = new PainelConteudo();
        conteudo.setBackground(COR_FUNDO);
        conteudo.setBorder(BorderFactory.createEmptyBorder(26, 32, 26, 32));

        List<Ativo> ativos = ativoDAO.listar();
        Map<String, Integer> porStatus = contarPorStatus(ativos);

        conteudo.add(construirCabecalho());
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(construirCartoesEstatisticas(ativos.size(), porStatus));
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(construirLinhaMeio(ativos));
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(construirAtivosRecentes(ativos));

        JScrollPane scroll = new JScrollPane(conteudo);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel construirCabecalho() {
        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setOpaque(false);

        JLabel titulo = new JLabel("Dashboard");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(COR_TITULO);

        JLabel subtitulo = new JLabel("Visão geral dos ativos de TI cadastrados no sistema.");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(COR_SUBTITULO);

        textos.add(titulo);
        textos.add(subtitulo);

        JLabel atualizado = new JLabel("Atualizado em " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy · HH:mm")));
        atualizado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        atualizado.setForeground(COR_SUBTITULO);

        cabecalho.add(textos, BorderLayout.WEST);
        cabecalho.add(atualizado, BorderLayout.EAST);

        return cabecalho;
    }

    private JPanel construirCartoesEstatisticas(int total, Map<String, Integer> porStatus) {
        JPanel painel = new JPanel(new GridLayout(1, 5, 16, 0));
        painel.setOpaque(false);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        painel.add(criarCartaoEstatistica("Total de ativos", String.valueOf(total), COR_TOTAL));
        painel.add(criarCartaoEstatistica("Disponíveis", String.valueOf(porStatus.getOrDefault("Disponível", 0)), COR_DISPONIVEL));
        painel.add(criarCartaoEstatistica("Em uso", String.valueOf(porStatus.getOrDefault("Em uso", 0)), COR_EM_USO));
        painel.add(criarCartaoEstatistica("Em manutenção", String.valueOf(porStatus.getOrDefault("Manutenção", 0)), COR_MANUTENCAO));
        painel.add(criarCartaoEstatistica("Aguardando formatação", String.valueOf(porStatus.getOrDefault("Aguardando formatação", 0)), COR_AGUARDANDO));

        return painel;
    }

    private JPanel criarCartaoEstatistica(String rotulo, String valor, Color cor) {
        JPanel cartao = new JPanel(new BorderLayout());
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValor.setForeground(cor);

        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRotulo.setForeground(COR_SUBTITULO);

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(lblRotulo);
        textos.add(lblValor);

        cartao.add(textos, BorderLayout.WEST);

        return cartao;
    }

    private JPanel construirLinhaMeio(List<Ativo> ativos) {
        JPanel linha = new JPanel(new GridLayout(1, 2, 20, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        linha.setPreferredSize(new Dimension(Integer.MAX_VALUE, 280));

        linha.add(construirAtivosPorUnidade(ativos));
        linha.add(construirUltimasMovimentacoes());

        return linha;
    }

    private JPanel construirAtivosPorUnidade(List<Ativo> ativos) {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel titulo = new JLabel("Ativos por Unidade");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(COR_TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Distribuição entre unidades cadastradas");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(COR_SUBTITULO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        painel.add(titulo);
        painel.add(subtitulo);

        Map<String, Integer> porUnidade = new LinkedHashMap<>();
        for (Ativo ativo : ativos) {
            porUnidade.merge(ativo.getUnidade(), 1, Integer::sum);
        }

        int maior = porUnidade.values().stream().max(Integer::compareTo).orElse(1);

        if (porUnidade.isEmpty()) {
            JLabel vazio = new JLabel("Nenhum ativo cadastrado ainda.");
            vazio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            vazio.setForeground(COR_SUBTITULO);
            painel.add(vazio);
        }

        for (Map.Entry<String, Integer> entrada : porUnidade.entrySet()) {
            painel.add(construirLinhaUnidade(entrada.getKey(), entrada.getValue(), maior));
            painel.add(Box.createVerticalStrut(10));
        }

        return painel;
    }

    private JPanel construirLinhaUnidade(String unidade, int quantidade, int maior) {
        JPanel linha = new JPanel();
        linha.setLayout(new BoxLayout(linha, BoxLayout.Y_AXIS));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(1000, 40));

        JPanel textoLinha = new JPanel(new BorderLayout());
        textoLinha.setOpaque(false);

        JLabel lblUnidade = new JLabel(unidade);
        lblUnidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUnidade.setForeground(COR_TITULO);

        JLabel lblQuantidade = new JLabel(String.valueOf(quantidade));
        lblQuantidade.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblQuantidade.setForeground(COR_SUBTITULO);

        textoLinha.add(lblUnidade, BorderLayout.WEST);
        textoLinha.add(lblQuantidade, BorderLayout.EAST);

        JProgressBar barra = new JProgressBar(0, maior);
        barra.setValue(quantidade);
        barra.setStringPainted(false);
        barra.setForeground(COR_PRIMARIA);
        barra.setBackground(COR_BORDA);
        barra.setBorderPainted(false);
        barra.setPreferredSize(new Dimension(100, 8));
        barra.setMaximumSize(new Dimension(1000, 8));

        linha.add(textoLinha);
        linha.add(Box.createVerticalStrut(4));
        linha.add(barra);

        return linha;
    }

    private JPanel construirUltimasMovimentacoes() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        JLabel titulo = new JLabel("Últimas Movimentações");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(COR_TITULO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 14, 0));

        painel.add(titulo);

        List<Movimentacao> movimentacoes = movimentacaoDAO.listarTodas().stream()
                .limit(5)
                .collect(Collectors.toList());

        if (movimentacoes.isEmpty()) {
            JLabel vazio = new JLabel("Nenhuma movimentação registrada ainda.");
            vazio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            vazio.setForeground(COR_SUBTITULO);
            painel.add(vazio);
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Movimentacao mov : movimentacoes) {
            JPanel linha = new JPanel(new BorderLayout());
            linha.setOpaque(false);
            linha.setAlignmentX(Component.LEFT_ALIGNMENT);
            linha.setMaximumSize(new Dimension(1000, 36));
            linha.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

            JLabel descricao = new JLabel(mov.getUnidadeOrigem() + " → " + mov.getUnidadeDestino());
            descricao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            descricao.setForeground(COR_TITULO);

            JLabel data = new JLabel(mov.getDataMovimentacao() != null ? mov.getDataMovimentacao().format(formato) : "-");
            data.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            data.setForeground(COR_SUBTITULO);

            linha.add(descricao, BorderLayout.WEST);
            linha.add(data, BorderLayout.EAST);

            painel.add(linha);
        }

        return painel;
    }

    private JPanel construirAtivosRecentes(List<Ativo> ativos) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));
        painel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        painel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 260));

        JLabel titulo = new JLabel("Ativos Recentes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        titulo.setForeground(COR_TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Tipo", "Modelo", "Unidade", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ativos.stream().limit(5).forEach(ativo -> modelo.addRow(new Object[]{
                ativo.getId(), ativo.getTipo(), ativo.getModelo(), ativo.getUnidade(), ativo.getStatus()
        }));

        JTable tabela = new JTable(modelo);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(28);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabela.setEnabled(false);

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        return painel;
    }

    private Map<String, Integer> contarPorStatus(List<Ativo> ativos) {
        Map<String, Integer> contagem = new LinkedHashMap<>();

        for (Ativo ativo : ativos) {
            contagem.merge(ativo.getStatus(), 1, Integer::sum);
        }

        return contagem;
    }
}