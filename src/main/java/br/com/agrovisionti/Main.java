package br.com.agrovisionti;

import br.com.agrovisionti.view.TelaLogin;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Erro ao carregar o tema: " + e.getMessage());
        }

        SwingUtilities.invokeLater(TelaLogin::new);
    }
}
