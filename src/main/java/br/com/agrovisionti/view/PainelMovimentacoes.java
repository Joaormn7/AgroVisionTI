package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.dao.ColaboradorDAO;
import br.com.agrovisionti.dao.MovimentacaoDAO;
import br.com.agrovisionti.dao.UnidadeDAO;
import br.com.agrovisionti.model.Ativo;
import br.com.agrovisionti.model.Colaborador;
import br.com.agrovisionti.model.Movimentacao;
import br.com.agrovisionti.model.Unidade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PainelMovimentacoes extends JPanel {

    private final MovimentacaoDAO movimentacaoDAO = new MovimentacaoDAO();
    private final AtivoDAO ativoDAO = new AtivoDAO();
    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private final UnidadeDAO unidadeDAO = new UnidadeDAO();
    private final boolean podeEditar;
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Alterna entre a lista e o formulário DENTRO do próprio painel —
    // mesmo padrão do PainelAtivos, nada de JFrame/popup por cima.
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel painelCards = new JPanel(cardLayout);
    private static final String CARD_LISTA = "LISTA";
    private static final String CARD_FORMULARIO = "FORMULARIO";

    // ---- componentes da lista ----
    private JButton btnNovaMovimentacao;
    private JButton btnAtualizar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblQuantidade;

    // ---- componentes do formulário ----
    private JComboBox<Ativo> comboAtivo;
    private JLabel labelUnidadeAtual;
    private JLabel labelResponsavelAtual;
    private JComboBox<Unidade> comboNovaUnidade;
    private JComboBox<Colaborador> comboNovoResponsavel;
    private JTextArea campoObservacoes;

    public PainelMovimentacoes(boolean podeEditar) {
        this.podeEditar = podeEditar;
        setLayout(new BorderLayout());
        setBackground(Cores.FUNDO);

        painelCards.add(construirPainelLista(), CARD_LISTA);
        painelCards.add(construirPainelFormulario(), CARD_FORMULARIO);
        add(painelCards, BorderLayout.CENTER);

        carregarTabela();
    }

    // ================= LISTA =================

    private JPanel construirPainelLista() {
        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setBackground(Cores.FUNDO);
        conteudo.setBorder(BorderFactory.createEmptyBorder(26, 32, 20, 32));

        JPanel areaSuperior = new JPanel(new BorderLayout(20, 14));
        areaSuperior.setOpaque(false);

        JLabel titulo = new JLabel("Histórico de Movimentações");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Cores.TITULO);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);

        btnNovaMovimentacao = new JButton("Nova Movimentação");
        btnAtualizar = new JButton("Atualizar");

        Cores.estilizarBotaoPrimario(btnNovaMovimentacao);
        Cores.estilizarBotaoSecundario(btnAtualizar);

        botoes.add(btnNovaMovimentacao);
        botoes.add(btnAtualizar);

        if (!podeEditar) {
            btnNovaMovimentacao.setEnabled(false);
        }

        JPanel linha = new JPanel(new BorderLayout());
        linha.setOpaque(false);
        linha.add(botoes, BorderLayout.EAST);

        areaSuperior.add(titulo, BorderLayout.NORTH);
        areaSuperior.add(linha, BorderLayout.SOUTH);

        modeloTabela = new DefaultTableModel(new Object[]{
                "ID", "Ativo", "Unidade Origem", "Unidade Destino",
                "Responsável Origem", "Responsável Destino", "Data", "Observações"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(34);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        TabelaUtil.aplicarEstiloPadrao(tabela);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(Cores.BORDA));

        conteudo.add(areaSuperior, BorderLayout.NORTH);
        conteudo.add(scroll, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBorder(BorderFactory.createEmptyBorder(12, 32, 12, 32));
        rodape.setBackground(Cores.FUNDO);

        lblQuantidade = new JLabel("0 movimentações registradas");
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rodape.add(lblQuantidade, BorderLayout.WEST);

        conteudo.add(rodape, BorderLayout.SOUTH);

        configurarEventosLista();

        return conteudo;
    }

    private void configurarEventosLista() {
        btnNovaMovimentacao.addActionListener(e -> abrirFormularioNovo());
        btnAtualizar.addActionListener(e -> carregarTabela());
    }

    public void carregarTabela() {
        List<Movimentacao> movimentacoes = movimentacaoDAO.listarTodas();
        modeloTabela.setRowCount(0);

        for (Movimentacao mov : movimentacoes) {
            modeloTabela.addRow(new Object[]{
                    mov.getId(),
                    descreverAtivo(mov.getAtivoId()),
                    mov.getUnidadeOrigemNome() != null ? mov.getUnidadeOrigemNome() : "-",
                    mov.getUnidadeDestinoNome(),
                    descreverColaborador(mov.getResponsavelOrigemId()),
                    descreverColaborador(mov.getResponsavelDestinoId()),
                    mov.getDataMovimentacao() != null ? mov.getDataMovimentacao().format(FORMATO_DATA) : "-",
                    mov.getObservacoes()
            });
        }

        lblQuantidade.setText(movimentacoes.size() + " movimentação(ões) registrada(s)");
    }

    private String descreverAtivo(int ativoId) {
        Ativo ativo = ativoDAO.buscarPorId(ativoId);

        if (ativo == null) {
            return "Ativo #" + ativoId;
        }

        return ativo.getTipo() + " - " + ativo.getMarca() + " " + ativo.getModelo();
    }

    private String descreverColaborador(Integer colaboradorId) {
        if (colaboradorId == null) {
            return "-";
        }

        Colaborador colaborador = colaboradorDAO.buscarPorId(colaboradorId);

        return colaborador != null ? colaborador.getNome() : "Colaborador #" + colaboradorId;
    }

    // ================= FORMULÁRIO (substitui a lista no mesmo painel) =================

    private JPanel construirPainelFormulario() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Cores.FUNDO);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Cores.PRIMARIA);
        topo.setBorder(BorderFactory.createEmptyBorder(18, 32, 18, 32));

        JLabel lblTituloFormulario = new JLabel("Nova Movimentação");
        lblTituloFormulario.setForeground(Color.WHITE);
        lblTituloFormulario.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JButton btnVoltar = new JButton("← Voltar pra lista");
        btnVoltar.setOpaque(false);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> cardLayout.show(painelCards, CARD_LISTA));

        topo.add(lblTituloFormulario, BorderLayout.WEST);
        topo.add(btnVoltar, BorderLayout.EAST);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Cores.FUNDO);
        form.setBorder(BorderFactory.createEmptyBorder(28, 45, 20, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;
        int linha = 0;

        comboAtivo = new JComboBox<>();
        comboAtivo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboAtivo.setPreferredSize(new Dimension(100, 34));
        comboAtivo.addActionListener(e -> atualizarLocalizacaoAtual());

        labelUnidadeAtual = new JLabel("-");
        labelUnidadeAtual.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        labelResponsavelAtual = new JLabel("-");
        labelResponsavelAtual.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        comboNovaUnidade = new JComboBox<>();
        comboNovaUnidade.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboNovaUnidade.setPreferredSize(new Dimension(100, 34));

        comboNovoResponsavel = new JComboBox<>();
        comboNovoResponsavel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboNovoResponsavel.setPreferredSize(new Dimension(100, 34));

        campoObservacoes = new JTextArea(5, 20);
        campoObservacoes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campoObservacoes.setLineWrap(true);
        campoObservacoes.setWrapStyleWord(true);

        linha = adicionarLinha(form, gbc, linha, "Ativo:", comboAtivo, true);
        linha = adicionarLinha(form, gbc, linha, "Unidade atual:", labelUnidadeAtual, false);
        linha = adicionarLinha(form, gbc, linha, "Responsável atual:", labelResponsavelAtual, false);
        linha = adicionarLinha(form, gbc, linha, "Nova unidade:", comboNovaUnidade, true);
        linha = adicionarLinha(form, gbc, linha, "Novo responsável:", comboNovoResponsavel, true);

        JLabel lblObs = new JLabel("Observações:");
        lblObs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = linha;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(lblObs, gbc);
        linha++;

        JScrollPane scrollObs = new JScrollPane(campoObservacoes);
        scrollObs.setPreferredSize(new Dimension(100, 110));
        gbc.gridy = linha;
        gbc.fill = GridBagConstraints.BOTH;
        form.add(scrollObs, gbc);
        linha++;

        JLabel lblLegenda = new JLabel("* Campos obrigatórios");
        lblLegenda.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblLegenda.setForeground(new Color(220, 38, 38));
        gbc.gridy = linha;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(12, 0, 8, 0);
        form.add(lblLegenda, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16));
        botoes.setBackground(Cores.FUNDO);
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 45, 20, 0));

        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        Cores.estilizarBotaoPrimario(btnSalvar);
        Cores.estilizarBotaoSecundario(btnCancelar);

        btnSalvar.setPreferredSize(new Dimension(120, 38));
        btnCancelar.setPreferredSize(new Dimension(120, 38));

        botoes.add(btnSalvar);
        botoes.add(btnCancelar);

        btnSalvar.addActionListener(e -> salvarMovimentacao());
        btnCancelar.addActionListener(e -> cardLayout.show(painelCards, CARD_LISTA));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Cores.FUNDO);
        centro.add(form, BorderLayout.CENTER);
        centro.add(botoes, BorderLayout.SOUTH);

        raiz.add(topo, BorderLayout.NORTH);
        raiz.add(new JScrollPane(centro), BorderLayout.CENTER);

        return raiz;
    }

    // Rótulo à esquerda + campo largo ocupando o restante da linha — mesmo
    // padrão visual da tela de Editar Ativo. Retorna a próxima linha livre.
    private int adicionarLinha(JPanel painel, GridBagConstraints gbc, int linha, String texto, JComponent campo, boolean obrigatorio) {
        JLabel label = new JLabel(obrigatorio
                ? "<html>" + texto + " <span style='color:#DC2626;'>*</span></html>"
                : texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridy = linha;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        painel.add(label, gbc);
        linha++;

        gbc.gridy = linha;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        campo.setPreferredSize(new Dimension(100, 34));
        painel.add(campo, gbc);
        linha++;

        return linha;
    }

    private void abrirFormularioNovo() {
        carregarCombosFormulario();
        atualizarLocalizacaoAtual();
        campoObservacoes.setText("");
        cardLayout.show(painelCards, CARD_FORMULARIO);
    }

    private void carregarCombosFormulario() {
        comboAtivo.removeAllItems();
        for (Ativo ativo : ativoDAO.listar()) {
            comboAtivo.addItem(ativo);
        }
        comboAtivo.setRenderer(new AtivoComboRenderer());

        comboNovoResponsavel.removeAllItems();
        List<Colaborador> colaboradores = colaboradorDAO.listarAtivos();

        if (colaboradores.isEmpty()) {
            Toast.mostrar(this, "Nenhum colaborador cadastrado — cadastre um antes de registrar a movimentação.", Toast.Tipo.AVISO);
        }

        for (Colaborador colaborador : colaboradores) {
            comboNovoResponsavel.addItem(colaborador);
        }

        comboNovoResponsavel.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Colaborador colaborador) {
                    setText(colaborador.getNome()
                            + (colaborador.getCargo() != null ? " - " + colaborador.getCargo() : ""));
                }
                return this;
            }
        });

        comboNovaUnidade.removeAllItems();
        List<Unidade> unidades = unidadeDAO.listarAtivas();

        if (unidades.isEmpty()) {
            Toast.mostrar(this, "Nenhuma unidade cadastrada — cadastre uma antes de registrar a movimentação.", Toast.Tipo.AVISO);
        }

        for (Unidade unidade : unidades) {
            comboNovaUnidade.addItem(unidade);
        }

        comboNovaUnidade.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Unidade unidade) {
                    setText(unidade.getNome());
                }
                return this;
            }
        });
    }

    private void atualizarLocalizacaoAtual() {
        Ativo selecionado = (Ativo) comboAtivo.getSelectedItem();

        if (selecionado == null) {
            labelUnidadeAtual.setText("-");
            labelResponsavelAtual.setText("-");
            return;
        }

        labelUnidadeAtual.setText(selecionado.getUnidadeNome());

        if (selecionado.getResponsavelId() != null) {
            Colaborador responsavelAtual = colaboradorDAO.buscarPorId(selecionado.getResponsavelId());
            labelResponsavelAtual.setText(responsavelAtual != null ? responsavelAtual.getNome() : "-");
        } else {
            labelResponsavelAtual.setText("-");
        }
    }

    private void salvarMovimentacao() {
        Ativo ativoSelecionado = (Ativo) comboAtivo.getSelectedItem();

        if (ativoSelecionado == null) {
            Toast.mostrar(this, "Selecione um ativo.", Toast.Tipo.AVISO);
            return;
        }

        Unidade novaUnidade = (Unidade) comboNovaUnidade.getSelectedItem();
        Colaborador novoResponsavel = (Colaborador) comboNovoResponsavel.getSelectedItem();

        if (novaUnidade == null || novoResponsavel == null) {
            Toast.mostrar(this, "Selecione a nova unidade e o novo responsável.", Toast.Tipo.AVISO);
            return;
        }

        Movimentacao movimentacao = new Movimentacao(
                ativoSelecionado.getId(),
                ativoSelecionado.getUnidadeId(),
                novaUnidade.getId(),
                ativoSelecionado.getResponsavelId(),
                novoResponsavel.getId(),
                campoObservacoes.getText().trim()
        );

        boolean sucesso = movimentacaoDAO.cadastrar(movimentacao);

        if (sucesso) {
            Toast.mostrar(this, "Movimentação registrada com sucesso!", Toast.Tipo.SUCESSO);
            carregarTabela();
            cardLayout.show(painelCards, CARD_LISTA);
        } else {
            Toast.mostrar(this, "Não foi possível registrar a movimentação.", Toast.Tipo.ERRO);
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