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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PainelRelatorios extends JPanel {

    private static final Color COR_BORDA = Cores.BORDA;
    private static final Color COR_TITULO = Cores.TITULO;
    private static final Color COR_SUBTITULO = Cores.SUBTITULO;

    private final AtivoDAO ativoDAO = new AtivoDAO();
    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();
    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();

    public PainelRelatorios() {
        setLayout(new BorderLayout());
        setBackground(Cores.FUNDO);
        montar();
    }

    public void atualizar() {
        removeAll();
        montar();
        revalidate();
        repaint();
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

    private void montar() {
        add(construirVisaoGeral(), BorderLayout.CENTER);
    }

    private JScrollPane construirVisaoGeral() {
        JPanel conteudo = new PainelConteudo();
        conteudo.setBackground(Cores.FUNDO);
        conteudo.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        List<Ativo> ativos = ativoDAO.listar();

        JLabel titulo = new JLabel("Relatórios Gerenciais");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(COR_TITULO);

        JButton btnExportarCsv = new JButton("Exportar Relatório de Ativos (CSV)");
        Cores.estilizarBotaoSecundario(btnExportarCsv);
        btnExportarCsv.addActionListener(e -> exportarCsv(ativos));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecalho.setMaximumSize(new Dimension(1120, 50));
        cabecalho.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        cabecalho.add(titulo, BorderLayout.WEST);
        cabecalho.add(btnExportarCsv, BorderLayout.EAST);

        JPanel painelResumo = construirResumo(ativos);
        painelResumo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linhaGraficos = construirLinhaGraficos(ativos);
        linhaGraficos.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linhaRecentes = construirLinhaRecentes(ativos);
        linhaRecentes.setAlignmentX(Component.LEFT_ALIGNMENT);

        conteudo.add(cabecalho);
        conteudo.add(painelResumo);
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(linhaGraficos);
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(linhaRecentes);

        JScrollPane scroll = new JScrollPane(conteudo);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel construirResumo(List<Ativo> ativos) {
        JPanel painel = new JPanel(new GridLayout(1, 3, 20, 0));
        painel.setOpaque(false);
        painel.setMaximumSize(new Dimension(1120, 90));

        long totalAtivos = ativos.size();
        long totalMovimentacoes = movimentacaoDAO.listarTodas().size();
        long emManutencao = ativos.stream().filter(a -> "Manutenção".equals(a.getStatus())).count();

        painel.add(criarCartaoResumo("Total de Ativos", String.valueOf(totalAtivos)));
        painel.add(criarCartaoResumo("Total de Movimentações", String.valueOf(totalMovimentacoes)));
        painel.add(criarCartaoResumo("Em Manutenção", String.valueOf(emManutencao)));

        return painel;
    }

    private JPanel criarCartaoResumo(String rotulo, String valor) {
        JPanel cartao = new JPanel();
        cartao.setLayout(new BoxLayout(cartao, BoxLayout.Y_AXIS));
        cartao.setBackground(Color.WHITE);
        cartao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValor.setForeground(Cores.PRIMARIA);

        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRotulo.setForeground(COR_SUBTITULO);

        cartao.add(lblValor);
        cartao.add(lblRotulo);

        return cartao;
    }

    private JPanel construirLinhaGraficos(List<Ativo> ativos) {
        JPanel linha = new JPanel(new GridLayout(1, 2, 20, 0));
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(1120, 260));
        linha.setPreferredSize(new Dimension(1120, 260));

        Map<String, Integer> porStatus = contarPorCampo(ativos, Ativo::getStatus);
        Map<String, Integer> porUnidade = contarPorCampo(ativos, Ativo::getUnidadeNome);

        GraficoBarraPanel graficoStatus = new GraficoBarraPanel("Ativos por Status", porStatus, Cores.PRIMARIA);
        GraficoBarraPanel graficoUnidade = new GraficoBarraPanel("Ativos por Unidade", porUnidade, Cores.PRIMARIA_CLARA);

        graficoStatus.setBorder(BorderFactory.createLineBorder(COR_BORDA));
        graficoUnidade.setBorder(BorderFactory.createLineBorder(COR_BORDA));

        linha.add(graficoStatus);
        linha.add(graficoUnidade);

        return linha;
    }

    private JPanel construirLinhaRecentes(List<Ativo> ativos) {
        JPanel linha = new JPanel(new GridLayout(1, 2, 20, 0));
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(1120, 280));
        linha.setPreferredSize(new Dimension(1120, 280));

        linha.add(construirUltimasMovimentacoes());
        linha.add(construirAtivosRecentes(ativos));

        return linha;
    }

    private JPanel construirUltimasMovimentacoes() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JLabel titulo = new JLabel("Últimas Movimentações");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(COR_TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        painel.add(titulo);

        List<Movimentacao> movimentacoes = movimentacaoDAO.listarTodas().stream()
                .limit(4)
                .collect(Collectors.toList());

        if (movimentacoes.isEmpty()) {
            JLabel vazio = new JLabel("Nenhuma movimentação registrada ainda.");
            vazio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            vazio.setForeground(COR_SUBTITULO);
            painel.add(vazio);
        }

        java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Movimentacao mov : movimentacoes) {
            JPanel linha = new JPanel();
            linha.setLayout(new BoxLayout(linha, BoxLayout.Y_AXIS));
            linha.setOpaque(false);
            linha.setMaximumSize(new Dimension(1000, 46));
            linha.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

            JLabel nomeAtivo = new JLabel(descreverAtivo(mov.getAtivoId()));
            nomeAtivo.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nomeAtivo.setForeground(COR_TITULO);
            nomeAtivo.setAlignmentX(Component.LEFT_ALIGNMENT);

            JPanel linhaDetalhe = new JPanel(new BorderLayout());
            linhaDetalhe.setOpaque(false);
            linhaDetalhe.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel trajeto = new JLabel(mov.getUnidadeOrigem() + " → " + mov.getUnidadeDestino());
            trajeto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            trajeto.setForeground(COR_SUBTITULO);

            JLabel data = new JLabel(mov.getDataMovimentacao() != null ? mov.getDataMovimentacao().format(formato) : "-");
            data.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            data.setForeground(COR_SUBTITULO);

            linhaDetalhe.add(trajeto, BorderLayout.WEST);
            linhaDetalhe.add(data, BorderLayout.EAST);

            linha.add(nomeAtivo);
            linha.add(linhaDetalhe);

            painel.add(linha);
        }

        return painel;
    }

    private JPanel construirAtivosRecentes(List<Ativo> ativos) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);
        painel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));

        JLabel titulo = new JLabel("Ativos Recentes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titulo.setForeground(COR_TITULO);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        DefaultTableModel modelo = new DefaultTableModel(new Object[]{"ID", "Tipo", "Unidade", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ativos.stream().limit(4).forEach(ativo -> modelo.addRow(new Object[]{
                ativo.getId(), ativo.getTipo(), ativo.getUnidadeNome(), ativo.getStatus()
        }));

        JTable tabela = new JTable(modelo);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setRowHeight(24);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabela.setEnabled(false);

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(new JScrollPane(tabela), BorderLayout.CENTER);

        return painel;
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

    private Map<String, Integer> contarPorCampo(List<Ativo> ativos, Function<Ativo, String> extrator) {
        Map<String, Integer> contagem = new LinkedHashMap<>();

        for (Ativo ativo : ativos) {
            String chave = extrator.apply(ativo);
            contagem.merge(chave, 1, Integer::sum);
        }

        return contagem;
    }

    private void exportarCsv(List<Ativo> ativos) {
        JFileChooser seletor = new JFileChooser();
        seletor.setSelectedFile(new File("relatorio_ativos.csv"));

        if (seletor.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = seletor.getSelectedFile();

        try (FileWriter escritor = new FileWriter(arquivo)) {
            escritor.write("ID;Tipo;Marca;Modelo;Numero Serie;Unidade;Responsavel;Status\n");

            for (Ativo ativo : ativos) {
                escritor.write(String.format("%d;%s;%s;%s;%s;%s;%s;%s%n",
                        ativo.getId(), ativo.getTipo(), ativo.getMarca(), ativo.getModelo(),
                        ativo.getNumeroSerie(), ativo.getUnidadeNome(),
                        descreverColaborador(ativo.getResponsavelId()), ativo.getStatus()));
            }

            Toast.mostrar(this, "Relatório exportado com sucesso!", Toast.Tipo.SUCESSO);

        } catch (IOException e) {
            Toast.mostrar(this, "Erro ao exportar relatório: " + e.getMessage(), Toast.Tipo.ERRO);
        }
    }
}