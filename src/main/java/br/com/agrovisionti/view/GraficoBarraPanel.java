package br.com.agrovisionti.view;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class GraficoBarraPanel extends JPanel {

    private final String titulo;
    private final Map<String, Integer> dados;
    private final Color corBarra;

    public GraficoBarraPanel(String titulo, Map<String, Integer> dados, Color corBarra) {
        this.titulo = titulo;
        this.dados = dados;
        this.corBarra = corBarra;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int largura = getWidth();
        int altura = getHeight();
        int margemInferior = 50;
        int margemSuperior = 40;
        int margemLateral = 20;

        g2.setColor(Cores.TITULO);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 15));
        g2.drawString(titulo, margemLateral, 22);

        if (dados == null || dados.isEmpty()) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g2.drawString("Sem dados para exibir.", margemLateral, altura / 2);
            return;
        }

        int maiorValor = dados.values().stream().max(Integer::compareTo).orElse(1);
        if (maiorValor == 0) maiorValor = 1;

        int quantidadeBarras = dados.size();
        int areaUtil = largura - (margemLateral * 2);
        int larguraMaximaPorBarra = 90;
        int larguraBarra = Math.min(larguraMaximaPorBarra, Math.max(30, areaUtil / quantidadeBarras - 20));
        int alturaMaxima = altura - margemInferior - margemSuperior;

        int larguraTotalUsada = quantidadeBarras * larguraBarra + (quantidadeBarras - 1) * 20;
        int x = margemLateral + Math.max(0, (areaUtil - larguraTotalUsada) / 2);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        for (Map.Entry<String, Integer> entrada : dados.entrySet()) {
            int valor = entrada.getValue();
            int alturaBarra = (int) ((valor / (double) maiorValor) * alturaMaxima);
            int y = altura - margemInferior - alturaBarra;

            g2.setColor(corBarra);
            g2.fillRoundRect(x, y, larguraBarra, alturaBarra, 8, 8);

            g2.setColor(Cores.TITULO);
            String valorTexto = String.valueOf(valor);
            int larguraTexto = g2.getFontMetrics().stringWidth(valorTexto);
            g2.drawString(valorTexto, x + (larguraBarra - larguraTexto) / 2, y - 6);

            String rotulo = entrada.getKey();
            int larguraRotulo = g2.getFontMetrics().stringWidth(rotulo);
            int xRotulo = x + (larguraBarra - larguraRotulo) / 2;
            g2.drawString(rotulo, Math.max(margemLateral, xRotulo), altura - margemInferior + 18);

            x += larguraBarra + 20;
        }
    }
}