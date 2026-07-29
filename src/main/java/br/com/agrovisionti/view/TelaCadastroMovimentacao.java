package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.dao.MovimentacaoDAO;
import br.com.agrovisionti.model.Ativo;
import br.com.agrovisionti.model.Movimentacao;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaCadastroMovimentacao extends JFrame {

    private final AtivoDAO ativoDAO = new AtivoDAO();
    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();
    private final Runnable aoSalvar;

    private JComboBox<Ativo> comboAtivo;
    private JLabel labelUnidadeAtual;
    private JLabel labelResponsavelAtual;
    private JTextField campoNovaUnidade;
    private JTextField campoNovoResponsavel;
    private JTextArea campoObservacoes;

    public TelaCadastroMovimentacao() {
        this(() -> {});
    }

    public TelaCadastroMovimentacao(TelaMovimentacoes telaPai) {
        this(telaPai::carregarTabela);
    }

    public TelaCadastroMovimentacao(PainelMovimentacoes painelMovimentacoes) {
        this(painelMovimentacoes::carregarTabela);
    }

    private TelaCadastroMovimentacao(Runnable aoSalvar) {
        this.aoSalvar = aoSalvar;

        setTitle("AgroVisionTI - Cadastro de Movimentação");
        setSize(800, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        add(construirPainelPrincipal());
        carregarAtivos();
        setVisible(true);
    }

    private JPanel construirPainelPrincipal() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(new Color(245, 247, 250));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        painel.add(construirTitulo(), BorderLayout.NORTH);
        painel.add(construirFormulario(), BorderLayout.CENTER);
        painel.add(construirPainelBotoes(), BorderLayout.SOUTH);

        return painel;
    }

    private JLabel construirTitulo() {
        JLabel titulo = new JLabel("Cadastro de Movimentação", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        return titulo;
    }

    private JPanel construirFormulario() {
        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;

        comboAtivo = new JComboBox<>();
        comboAtivo.addActionListener(e -> atualizarLocalizacaoAtual());

        labelUnidadeAtual = new JLabel("-");
        labelResponsavelAtual = new JLabel("-");
        campoNovaUnidade = new JTextField();
        campoNovoResponsavel = new JTextField();
        campoObservacoes = new JTextArea(4, 20);
        campoObservacoes.setLineWrap(true);
        campoObservacoes.setWrapStyleWord(true);

        adicionarCampo(formulario, c, "Ativo", comboAtivo);
        adicionarCampo(formulario, c, "Unidade atual", labelUnidadeAtual);
        adicionarCampo(formulario, c, "Responsável atual", labelResponsavelAtual);
        adicionarCampo(formulario, c, "Nova unidade", campoNovaUnidade);
        adicionarCampo(formulario, c, "Novo responsável", campoNovoResponsavel);
        adicionarCampo(formulario, c, "Observações", new JScrollPane(campoObservacoes));

        return formulario;
    }

    private void adicionarCampo(JPanel formulario, GridBagConstraints c, String rotulo, JComponent campo) {
        JLabel label = new JLabel(rotulo);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        formulario.add(label, c);
        c.gridy++;
        formulario.add(campo, c);
        c.gridy++;
    }

    private JPanel construirPainelBotoes() {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.setOpaque(false);

        JButton botaoSalvar = new JButton("Salvar Movimentação");
        botaoSalvar.addActionListener(e -> salvarMovimentacao());

        JButton botaoCancelar = new JButton("Cancelar");
        botaoCancelar.addActionListener(e -> dispose());

        painelBotoes.add(botaoSalvar);
        painelBotoes.add(botaoCancelar);

        return painelBotoes;
    }

    private void carregarAtivos() {
        List<Ativo> ativos = ativoDAO.listar();

        for (Ativo ativo : ativos) {
            comboAtivo.addItem(ativo);
        }

        comboAtivo.setRenderer(new AtivoComboRenderer());
    }

    private void atualizarLocalizacaoAtual() {
        Ativo selecionado = (Ativo) comboAtivo.getSelectedItem();

        if (selecionado == null) {
            labelUnidadeAtual.setText("-");
            labelResponsavelAtual.setText("-");
            return;
        }

        labelUnidadeAtual.setText(selecionado.getUnidade());
        labelResponsavelAtual.setText(selecionado.getResponsavel());
    }

    private void salvarMovimentacao() {
        Ativo ativoSelecionado = (Ativo) comboAtivo.getSelectedItem();

        if (ativoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um ativo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String novaUnidade = campoNovaUnidade.getText().trim();
        String novoResponsavel = campoNovoResponsavel.getText().trim();

        if (novaUnidade.isEmpty() || novoResponsavel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha a nova unidade e o novo responsável.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Movimentacao movimentacao = new Movimentacao(
                ativoSelecionado.getId(),
                ativoSelecionado.getUnidade(),
                novaUnidade,
                ativoSelecionado.getResponsavel(),
                novoResponsavel,
                campoObservacoes.getText().trim()
        );

        boolean sucesso = movimentacaoDAO.cadastrar(movimentacao);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Movimentação registrada com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            aoSalvar.run();

            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível registrar a movimentação.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class AtivoComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Ativo ativo) {
                setText(ativo.getTipo() + " - " + ativo.getMarca() + " " + ativo.getModelo()
                        + " (id " + ativo.getId() + ")");
            }

            return this;
        }
    }
}