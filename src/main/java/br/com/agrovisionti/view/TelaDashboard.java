package br.com.agrovisionti.view;

import javax.swing.*;
import java.awt.*;

public class TelaDashboard extends JFrame {
    private JButton btnAtivos;
    private JButton btnMovimentacoes;
    private JButton btnHistorico;
    private JButton btnRelatorios;
    private JButton btnSair;

    public TelaDashboard() {
        setTitle("AgroVisionTI - Dashboard");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
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

        JLabel tituloTopo = new JLabel("AgroVisionTI");
        tituloTopo.setForeground(Color.WHITE);
        tituloTopo.setFont(new Font("Segoe UI", Font.BOLD, 24));

        btnSair = new JButton("Sair");

        topo.add(tituloTopo, BorderLayout.WEST);
        topo.add(btnSair, BorderLayout.EAST);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(new Color(245, 247, 250));
        centro.setBorder(BorderFactory.createEmptyBorder(40, 70, 50, 70));

        JLabel titulo = new JLabel("Dashboard", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(new Color(30, 41, 59));

        JLabel subtitulo = new JLabel("Selecione uma opção para gerenciar o sistema", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitulo.setForeground(new Color(100, 116, 139));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 6));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        JPanel cards = new JPanel(new GridLayout(2, 2, 25, 25));
        cards.setOpaque(false);
        cards.setBorder(BorderFactory.createEmptyBorder(45, 120, 40, 120));

        btnAtivos = criarCard("Ativos", "Gerenciar equipamentos cadastrados");
        btnMovimentacoes = criarCard("Movimentações", "Controlar entradas, saídas e transferências");
        btnHistorico = criarCard("Histórico", "Consultar registros e alterações");
        btnRelatorios = criarCard("Relatórios", "Gerar informações gerenciais");

        cards.add(btnAtivos);
        cards.add(btnMovimentacoes);
        cards.add(btnHistorico);
        cards.add(btnRelatorios);

        centro.add(textos, BorderLayout.NORTH);
        centro.add(cards, BorderLayout.CENTER);

        raiz.add(topo, BorderLayout.NORTH);
        raiz.add(centro, BorderLayout.CENTER);
        add(raiz);

        btnSair.addActionListener(e -> {
            dispose();
            new TelaLogin();
        });

        btnAtivos.addActionListener(e -> {
            dispose();
            new TelaAtivos();
        });

        btnMovimentacoes.addActionListener(e -> new TelaMovimentacoes());
        btnHistorico.addActionListener(e -> new TelaHistorico());
        btnRelatorios.addActionListener(e -> JOptionPane.showMessageDialog(this, "Módulo de relatórios previsto para implementação futura."));
    }

    private JButton criarCard(String titulo, String descricao) {
        JButton botao = new JButton("<html><center><b style='font-size:17px'>" + titulo + "</b><br><br><span style='font-size:11px'>" + descricao + "</span></center></html>");
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        botao.setFocusPainted(false);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return botao;
    }
}
