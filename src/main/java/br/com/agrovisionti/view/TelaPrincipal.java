package br.com.agrovisionti.view;

import br.com.agrovisionti.model.Usuario;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class TelaPrincipal extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel painelCards = new JPanel(cardLayout);
    private final List<JButton> itensMenu = new ArrayList<>();
    private final Usuario usuarioLogado;
    private JButton itemAtivoAtual;

    private PainelAtivos painelAtivos;
    private PainelMovimentacoes painelMovimentacoes;
    private PainelRelatorios painelRelatorios;
    private PainelUnidades painelUnidades;
    private PainelUsuarios painelUsuarios;
    private PainelColaboradores painelColaboradores;

    public TelaPrincipal(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        setTitle("AgroVisionTI");
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
        raiz.setBackground(Cores.FUNDO);

        raiz.add(construirSidebar(), BorderLayout.WEST);

        painelAtivos = new PainelAtivos(this, usuarioLogado.isAdministrador());
        painelMovimentacoes = new PainelMovimentacoes(usuarioLogado.isAdministrador());
        painelRelatorios = new PainelRelatorios();
        painelUnidades = new PainelUnidades(this, usuarioLogado.isAdministrador());
        painelUsuarios = new PainelUsuarios(this);
        painelColaboradores = new PainelColaboradores(this, usuarioLogado.isAdministrador());

        painelCards.add(painelAtivos, "ATIVOS");
        painelCards.add(painelMovimentacoes, "MOVIMENTACOES");
        painelCards.add(painelRelatorios, "RELATORIOS");
        painelCards.add(painelUnidades, "UNIDADES");
        painelCards.add(painelUsuarios, "USUARIOS");
        painelCards.add(painelColaboradores, "COLABORADORES");

        raiz.add(painelCards, BorderLayout.CENTER);

        add(raiz);

        mostrar("RELATORIOS", itensMenu.get(0));
    }

    private JPanel construirSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Cores.SIDEBAR_FUNDO);
        sidebar.setPreferredSize(new Dimension(230, getHeight()));

        JLabel logo = new JLabel("AgroVisionTI");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setBorder(BorderFactory.createEmptyBorder(24, 20, 4, 20));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLogo = new JLabel("Gestão de Ativos");
        subLogo.setForeground(Cores.SIDEBAR_TEXTO);
        subLogo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLogo.setBorder(BorderFactory.createEmptyBorder(0, 20, 4, 20));
        subLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel usuarioInfo = new JLabel(usuarioLogado.getNome() + " · " + usuarioLogado.getPerfil());
        usuarioInfo.setForeground(new Color(134, 239, 172));
        usuarioInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        usuarioInfo.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        usuarioInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rotuloMenu = new JLabel("MENU PRINCIPAL");
        rotuloMenu.setForeground(new Color(100, 140, 120));
        rotuloMenu.setFont(new Font("Segoe UI", Font.BOLD, 11));
        rotuloMenu.setBorder(BorderFactory.createEmptyBorder(0, 20, 8, 20));
        rotuloMenu.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton itemRelatorios = criarItemMenu("Relatórios", "icons/relatorios.svg");
        JButton itemAtivos = criarItemMenu("Ativos", "icons/ativos.svg");
        JButton itemMovimentacoes = criarItemMenu("Movimentações", "icons/movimentacoes.svg");
        JButton itemUnidades = criarItemMenu("Unidades", "icons/unidades.svg");
        JButton itemColaboradores = criarItemMenu("Colaboradores", "icons/colaboradores.svg");

        itensMenu.add(itemRelatorios);
        itensMenu.add(itemAtivos);
        itensMenu.add(itemMovimentacoes);
        itensMenu.add(itemUnidades);
        itensMenu.add(itemColaboradores);

        itemRelatorios.addActionListener(e -> mostrar("RELATORIOS", itemRelatorios));
        itemAtivos.addActionListener(e -> mostrar("ATIVOS", itemAtivos));
        itemMovimentacoes.addActionListener(e -> mostrar("MOVIMENTACOES", itemMovimentacoes));
        itemUnidades.addActionListener(e -> mostrar("UNIDADES", itemUnidades));
        itemColaboradores.addActionListener(e -> mostrar("COLABORADORES", itemColaboradores));

        sidebar.add(logo);
        sidebar.add(subLogo);
        sidebar.add(usuarioInfo);
        sidebar.add(rotuloMenu);
        sidebar.add(itemRelatorios);
        sidebar.add(itemAtivos);
        sidebar.add(itemMovimentacoes);
        sidebar.add(itemUnidades);
        sidebar.add(itemColaboradores);

        if (usuarioLogado.isAdministrador()) {
            JButton itemUsuarios = criarItemMenu("Usuários", "icons/usuarios.svg");
            itensMenu.add(itemUsuarios);
            itemUsuarios.addActionListener(e -> mostrar("USUARIOS", itemUsuarios));
            sidebar.add(itemUsuarios);
        }

        JButton itemSair = criarItemMenu("Sair", "icons/sair.svg");
        itemSair.addActionListener(e -> {
            dispose();
            new TelaLogin();
        });

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(itemSair);
        sidebar.add(Box.createVerticalStrut(20));

        return sidebar;
    }

    private JButton criarItemMenu(String texto, String caminhoIcone) {
        JButton item = new JButton(texto);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        item.setForeground(Cores.SIDEBAR_TEXTO);
        item.setBackground(Cores.SIDEBAR_FUNDO);
        item.setBorderPainted(false);
        item.setFocusPainted(false);
        item.setContentAreaFilled(true);
        item.setOpaque(true);
        item.setHorizontalAlignment(SwingConstants.LEFT);
        item.setIconTextGap(14);
        item.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(230, 44));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        try {
            item.setIcon(new FlatSVGIcon(caminhoIcone, 18, 18));
        } catch (Exception e) {
            // Se o ícone não carregar por algum motivo, o botão continua funcionando só com texto
        }

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (item != itemAtivoAtual) {
                    item.setBackground(Cores.SIDEBAR_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (item != itemAtivoAtual) {
                    item.setBackground(Cores.SIDEBAR_FUNDO);
                }
            }
        });

        return item;
    }

    private void marcarItemAtivo(JButton itemSelecionado) {
        for (JButton item : itensMenu) {
            item.setBackground(Cores.SIDEBAR_FUNDO);
            item.setForeground(Cores.SIDEBAR_TEXTO);
        }

        itemSelecionado.setBackground(Cores.SIDEBAR_ITEM_ATIVO);
        itemSelecionado.setForeground(Color.WHITE);
        itemAtivoAtual = itemSelecionado;
    }

    private void mostrar(String nomeCard, JButton itemMenu) {
        atualizarPainel(nomeCard);
        cardLayout.show(painelCards, nomeCard);
        marcarItemAtivo(itemMenu);
    }

    private void atualizarPainel(String nomeCard) {
        switch (nomeCard) {
            case "ATIVOS" -> painelAtivos.carregarTabela();
            case "MOVIMENTACOES" -> painelMovimentacoes.carregarTabela();
            case "RELATORIOS" -> painelRelatorios.atualizar();
            case "UNIDADES" -> painelUnidades.carregarTabela();
            case "USUARIOS" -> painelUsuarios.carregarTabela();
            case "COLABORADORES" -> painelColaboradores.carregarTabela();
        }
    }
}