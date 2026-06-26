package br.com.agrovisionti.view;

import javax.swing.*;
import java.awt.*;

public class TelaCadastroMovimentacao extends JFrame {
    public TelaCadastroMovimentacao() {
        setTitle("AgroVisionTI - Cadastro de Movimentação");
        setSize(800, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(new Color(245, 247, 250));

        JLabel titulo = new JLabel("Cadastro de Movimentação", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel aviso = new JLabel("Tela prevista para implementação futura.", SwingConstants.CENTER);
        aviso.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        painel.add(titulo, BorderLayout.NORTH);
        painel.add(aviso, BorderLayout.CENTER);

        add(painel);
        setVisible(true);
    }
}
