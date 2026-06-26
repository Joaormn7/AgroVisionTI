package br.com.agrovisionti.view;

import javax.swing.*;
import java.awt.*;

public class TelaHistorico extends JFrame {
    public TelaHistorico() {
        setTitle("AgroVisionTI - Histórico");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("Histórico", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel aviso = new JLabel("Módulo previsto para implementação futura.", SwingConstants.CENTER);
        aviso.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(aviso, BorderLayout.CENTER);

        add(painel);
        setVisible(true);
    }
}
