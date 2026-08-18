package br.com.agrovisionti.view;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Map;

/**
 * Renderiza uma célula de tabela como um "badge" (pill colorido), em vez de
 * texto puro — usado nas colunas de status (Ativo, Unidade, Termo...).
 * <p>
 * Uso: tabela.getColumnModel().getColumn(indice).setCellRenderer(
 *          new BadgeCellRenderer(Map.of("Ativo", Cores.STATUS_DISPONIVEL, ...)));
 */
public class BadgeCellRenderer extends JLabel implements TableCellRenderer {

    private final Map<String, Color> cores;
    private Color corAtual;

    public BadgeCellRenderer(Map<String, Color> cores) {
        this.cores = cores;
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        corAtual = Cores.SUBTITULO;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        String texto = value != null ? value.toString() : "";
        setText(texto);
        corAtual = cores.getOrDefault(texto, Cores.SUBTITULO);
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        FontMetrics fm = g2.getFontMetrics(getFont());
        int largura = Math.min(getWidth() - 8, fm.stringWidth(getText()) + 24);
        int altura = 22;
        int x = (getWidth() - largura) / 2;
        int y = (getHeight() - altura) / 2;

        g2.setColor(corAtual);
        g2.fillRoundRect(x, y, largura, altura, altura, altura);
        g2.dispose();

        super.paintComponent(g);
    }
}