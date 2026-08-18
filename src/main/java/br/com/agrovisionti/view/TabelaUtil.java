package br.com.agrovisionti.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Estilização compartilhada de tabelas: linhas zebradas, sem grade visível,
 * seleção na cor da marca em vez do azul padrão do Swing. Chame depois de
 * criar a JTable, antes de colocá-la num JScrollPane.
 */
public final class TabelaUtil {

    private static final Color LINHA_PAR = Color.WHITE;
    private static final Color LINHA_IMPAR = new Color(248, 250, 252);

    private TabelaUtil() {
    }

    public static void aplicarEstiloPadrao(JTable tabela) {
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(Cores.PRIMARIA_CLARA);
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setFillsViewportHeight(true);

        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? LINHA_PAR : LINHA_IMPAR);
                }
                return c;
            }
        });
    }
}