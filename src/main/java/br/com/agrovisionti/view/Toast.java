package br.com.agrovisionti.view;

import javax.swing.*;
import java.awt.*;

/**
 * Notificação flutuante que aparece no canto da janela e some sozinha —
 * substitui JOptionPane.showMessageDialog() nas telas do sistema, que tem
 * visual datado (caixinha de diálogo do sistema operacional).
 * <p>
 * Uso: Toast.mostrar(this, "Ativo cadastrado com sucesso.", Toast.Tipo.SUCESSO);
 */
public class Toast {

    public enum Tipo {
        SUCESSO(new Color(5, 150, 105)),
        ERRO(new Color(220, 38, 38)),
        AVISO(new Color(217, 119, 6));

        final Color cor;

        Tipo(Color cor) {
            this.cor = cor;
        }
    }

    private static final int DURACAO_MS = 2600;
    private static final int LARGURA_MAXIMA = 340;

    public static void mostrar(Component componenteChamador, String mensagem, Tipo tipo) {
        Window owner = componenteChamador instanceof Window
                ? (Window) componenteChamador
                : SwingUtilities.getWindowAncestor(componenteChamador);

        JWindow janela = new JWindow(owner);

        try {
            janela.setBackground(new Color(0, 0, 0, 0));
        } catch (UnsupportedOperationException ignorado) {
            // Ambiente sem suporte a janela transparente — segue sem cantos arredondados,
            // não é crítico o suficiente pra travar a notificação.
        }

        JPanel painel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(tipo.cor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        painel.setOpaque(false);
        painel.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JLabel label = new JLabel("<html><body style='width:" + (LARGURA_MAXIMA - 40) + "px'>"
                + escaparHtml(mensagem) + "</body></html>");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        painel.add(label, BorderLayout.CENTER);

        janela.setContentPane(painel);
        janela.pack();

        posicionar(janela, owner);
        janela.setVisible(true);

        Timer temporizador = new Timer(DURACAO_MS, e -> janela.dispose());
        temporizador.setRepeats(false);
        temporizador.start();
    }

    private static void posicionar(JWindow janela, Window owner) {
        if (owner != null && owner.isShowing()) {
            Rectangle limites = owner.getBounds();
            int x = limites.x + limites.width - janela.getWidth() - 30;
            int y = limites.y + limites.height - janela.getHeight() - 40;
            janela.setLocation(x, y);
        } else {
            Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
            janela.setLocation(tela.width - janela.getWidth() - 30, tela.height - janela.getHeight() - 60);
        }
    }

    private static String escaparHtml(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}