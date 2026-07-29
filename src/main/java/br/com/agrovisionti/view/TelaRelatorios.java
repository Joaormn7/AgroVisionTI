package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.dao.MovimentacaoDAO;
import br.com.agrovisionti.model.Ativo;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TelaRelatorios extends JFrame {

    private final AtivoDAO ativoDAO = new AtivoDAO();
    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();

    public TelaRelatorios() {
        setTitle("AgroVisionTI - Relatórios");
        setSize(1200, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        criarComponentes();
        setVisible(true);
    }

    private void criarComponentes() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(new Color(245, 247, 250));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(37, 99, 235));
        topo.setBorder(BorderFactory.createEmptyBorder(16, 28, 16, 28));

        JLabel tituloTopo = new JLabel("AgroVisionTI - Relatórios");
        tituloTopo.setForeground(Color.WHITE);
        tituloTopo.setFont(new Font("Segoe UI", Font.BOLD, 23));

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> dispose());

        topo.add(tituloTopo, BorderLayout.WEST);
        topo.add(btnVoltar, BorderLayout.EAST);

        JPanel conteudo = new JPanel();
        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(new Color(245, 247, 250));
        conteudo.setBorder(BorderFactory.createEmptyBorder(26, 40, 20, 40));

        JLabel titulo = new JLabel("Relatórios Gerenciais");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(new Color(30, 41, 59));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        List<Ativo> ativos = ativoDAO.listar();

        JPanel painelResumo = construirResumo(ativos);
        painelResumo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel linhaGraficos = construirLinhaGraficos(ativos);
        linhaGraficos.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnExportarCsv = new JButton("Exportar Relatório de Ativos (CSV)");
        btnExportarCsv.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnExportarCsv.addActionListener(e -> exportarCsv(ativos));

        conteudo.add(titulo);
        conteudo.add(Box.createVerticalStrut(20));
        conteudo.add(painelResumo);
        conteudo.add(Box.createVerticalStrut(24));
        conteudo.add(linhaGraficos);
        conteudo.add(Box.createVerticalStrut(24));
        conteudo.add(btnExportarCsv);

        raiz.add(topo, BorderLayout.NORTH);
        raiz.add(conteudo, BorderLayout.CENTER);

        add(raiz);
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
                BorderFactory.createLineBorder(new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValor.setForeground(new Color(37, 99, 235));

        JLabel lblRotulo = new JLabel(rotulo);
        lblRotulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblRotulo.setForeground(new Color(100, 116, 139));

        cartao.add(lblValor);
        cartao.add(lblRotulo);

        return cartao;
    }

    private JPanel construirLinhaGraficos(List<Ativo> ativos) {
        JPanel linha = new JPanel(new GridLayout(1, 2, 20, 0));
        linha.setOpaque(false);
        linha.setMaximumSize(new Dimension(1120, 300));
        linha.setPreferredSize(new Dimension(1120, 300));

        Map<String, Integer> porStatus = contarPorCampo(ativos, Ativo::getStatus);
        Map<String, Integer> porUnidade = contarPorCampo(ativos, Ativo::getUnidade);

        GraficoBarraPanel graficoStatus = new GraficoBarraPanel("Ativos por Status", porStatus, new Color(37, 99, 235));
        GraficoBarraPanel graficoUnidade = new GraficoBarraPanel("Ativos por Unidade", porUnidade, new Color(16, 185, 129));

        graficoStatus.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        graficoUnidade.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        linha.add(graficoStatus);
        linha.add(graficoUnidade);

        return linha;
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
                        ativo.getNumeroSerie(), ativo.getUnidade(), ativo.getResponsavel(), ativo.getStatus()));
            }

            JOptionPane.showMessageDialog(this, "Relatório exportado com sucesso!");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar relatório: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}