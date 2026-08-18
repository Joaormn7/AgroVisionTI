package br.com.agrovisionti.view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

// Paleta central do sistema. Concentrar as cores aqui facilita reaproveitar
// nas telas e ajustar o tema inteiro num lugar só, sem caçar Color(...) espalhado.
public final class Cores {

    private Cores() {
    }

    // Marca (verde da empresa)
    public static final Color PRIMARIA = new Color(5, 150, 105);
    public static final Color PRIMARIA_CLARA = new Color(16, 185, 129);
    public static final Color PRIMARIA_ESCURA = new Color(4, 120, 87);

    // Sidebar
    public static final Color SIDEBAR_FUNDO = new Color(6, 46, 33);
    public static final Color SIDEBAR_HOVER = new Color(13, 68, 49);
    public static final Color SIDEBAR_ITEM_ATIVO = new Color(5, 150, 105);
    public static final Color SIDEBAR_TEXTO = new Color(203, 213, 225);

    // Superfícies neutras
    public static final Color FUNDO = new Color(245, 247, 250);
    public static final Color BORDA = new Color(226, 232, 240);
    public static final Color TITULO = new Color(30, 41, 59);
    public static final Color SUBTITULO = new Color(100, 116, 139);

    // Status de ativos
    public static final Color STATUS_DISPONIVEL = new Color(34, 197, 94);
    public static final Color STATUS_EM_USO = new Color(8, 145, 178);
    public static final Color STATUS_MANUTENCAO = new Color(239, 68, 68);
    public static final Color STATUS_AGUARDANDO = new Color(245, 158, 11);

    // Perigo (ações destrutivas: excluir, desativar)
    public static final Color PERIGO = new Color(220, 38, 38);

    // ================= Estilização de botão =================
    // Centraliza aqui em vez de repetir setBackground/setForeground/setFont em
    // cada tela — é o que faltava pra parar de ter só o botão de login estilizado.

    public static void estilizarBotaoPrimario(JButton botao) {
        botao.setBackground(PRIMARIA);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void estilizarBotaoSecundario(JButton botao) {
        botao.setBackground(Color.WHITE);
        botao.setForeground(PRIMARIA);
        botao.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        botao.setBorder(BorderFactory.createLineBorder(PRIMARIA, 1, true));
        botao.setFocusPainted(false);
        botao.setOpaque(true);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void estilizarBotaoPerigo(JButton botao) {
        botao.setBackground(PERIGO);
        botao.setForeground(Color.WHITE);
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setFocusPainted(false);
        botao.setBorderPainted(false);
        botao.setOpaque(true);
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}