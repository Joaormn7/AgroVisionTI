package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.dao.ColaboradorDAO;
import br.com.agrovisionti.dao.UnidadeDAO;
import br.com.agrovisionti.model.Ativo;
import br.com.agrovisionti.model.Colaborador;
import br.com.agrovisionti.model.Unidade;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class PainelAtivos extends JPanel {

    private final AtivoDAO ativoDAO = new AtivoDAO();
    private final UnidadeDAO unidadeDAO = new UnidadeDAO();
    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private final boolean podeEditar;

    private static final Map<String, Color> CORES_STATUS = Map.of(
            "Disponível", Cores.STATUS_DISPONIVEL,
            "Em uso", Cores.STATUS_EM_USO,
            "Manutenção", Cores.STATUS_MANUTENCAO,
            "Aguardando formatação", Cores.STATUS_AGUARDANDO
    );

    // Alterna entre a lista e o formulário DENTRO do próprio painel —
    // nada de JDialog/popup por cima.
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel painelCards = new JPanel(cardLayout);
    private static final String CARD_LISTA = "LISTA";
    private static final String CARD_FORMULARIO = "FORMULARIO";

    // ---- componentes da lista ----
    private JTextField txtPesquisa;
    private JButton btnNovo;
    private JButton btnEditar;
    private JButton btnExcluir;
    private JButton btnAtualizar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblQuantidade;

    // ---- componentes do formulário ----
    private JLabel lblTituloFormulario;
    private JTextField txtTipo;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtNumeroSerie;
    private JComboBox<Unidade> cbUnidade;
    private JComboBox<Colaborador> cbResponsavel;
    private JComboBox<String> cbStatus;
    private JTextArea txtObservacoes;
    private Ativo ativoEmEdicao;

    public PainelAtivos(Window janelaProprietaria, boolean podeEditar) {
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

        JLabel titulo = new JLabel("Gerenciamento de Ativos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Cores.TITULO);

        JPanel pesquisa = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pesquisa.setOpaque(false);

        JLabel lblPesquisa = new JLabel("Pesquisar:");
        lblPesquisa.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtPesquisa = new JTextField();
        txtPesquisa.setPreferredSize(new Dimension(330, 36));

        pesquisa.add(lblPesquisa);
        pesquisa.add(txtPesquisa);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);

        btnNovo = new JButton("Novo Ativo");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnAtualizar = new JButton("Atualizar");

        Cores.estilizarBotaoPrimario(btnNovo);
        Cores.estilizarBotaoSecundario(btnEditar);
        Cores.estilizarBotaoPerigo(btnExcluir);
        Cores.estilizarBotaoSecundario(btnAtualizar);

        botoes.add(btnNovo);
        botoes.add(btnEditar);
        botoes.add(btnExcluir);
        botoes.add(btnAtualizar);

        if (!podeEditar) {
            btnNovo.setEnabled(false);
            btnEditar.setEnabled(false);
            btnExcluir.setEnabled(false);
        }

        JPanel linha = new JPanel(new BorderLayout());
        linha.setOpaque(false);
        linha.add(pesquisa, BorderLayout.WEST);
        linha.add(botoes, BorderLayout.EAST);

        areaSuperior.add(titulo, BorderLayout.NORTH);
        areaSuperior.add(linha, BorderLayout.SOUTH);

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Tipo", "Marca", "Modelo", "Nº Série", "Unidade", "Responsável", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modeloTabela);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.setRowHeight(34);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        TabelaUtil.aplicarEstiloPadrao(tabela);
        tabela.getColumnModel().getColumn(7).setCellRenderer(new BadgeCellRenderer(CORES_STATUS));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(Cores.BORDA));

        conteudo.add(areaSuperior, BorderLayout.NORTH);
        conteudo.add(scroll, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBorder(BorderFactory.createEmptyBorder(12, 32, 12, 32));
        rodape.setBackground(Cores.FUNDO);

        lblQuantidade = new JLabel("0 ativos cadastrados");
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rodape.add(lblQuantidade, BorderLayout.WEST);

        conteudo.add(rodape, BorderLayout.SOUTH);

        configurarEventosLista();

        return conteudo;
    }

    private void configurarEventosLista() {
        btnNovo.addActionListener(e -> abrirFormularioNovo());
        btnEditar.addActionListener(e -> editarAtivoSelecionado());
        btnExcluir.addActionListener(e -> excluirAtivoSelecionado());
        btnAtualizar.addActionListener(e -> carregarTabela());

        txtPesquisa.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { pesquisar(); }
            public void removeUpdate(DocumentEvent e) { pesquisar(); }
            public void changedUpdate(DocumentEvent e) { pesquisar(); }
        });
    }

    public void carregarTabela() {
        preencherTabela(ativoDAO.listar());
    }

    private void pesquisar() {
        String termo = txtPesquisa.getText().trim();

        if (termo.isEmpty()) {
            carregarTabela();
            return;
        }

        preencherTabela(ativoDAO.pesquisar(termo));
    }

    private void preencherTabela(List<Ativo> ativos) {
        modeloTabela.setRowCount(0);

        for (Ativo ativo : ativos) {
            modeloTabela.addRow(new Object[]{
                    ativo.getId(),
                    ativo.getTipo(),
                    ativo.getMarca(),
                    ativo.getModelo(),
                    ativo.getNumeroSerie(),
                    ativo.getUnidadeNome(),
                    descreverResponsavel(ativo.getResponsavelId()),
                    ativo.getStatus()
            });
        }

        lblQuantidade.setText(ativos.size() + " ativo(s) encontrado(s)");
    }

    private String descreverResponsavel(Integer responsavelId) {
        if (responsavelId == null) {
            return "-";
        }

        Colaborador colaborador = colaboradorDAO.buscarPorId(responsavelId);

        return colaborador != null ? colaborador.getNome() : "Colaborador #" + responsavelId;
    }

    private int obterIdSelecionado() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            Toast.mostrar(this, "Selecione um ativo na tabela.", Toast.Tipo.AVISO);
            return -1;
        }

        return Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
    }

    private void editarAtivoSelecionado() {
        int id = obterIdSelecionado();

        if (id == -1) return;

        Ativo ativo = ativoDAO.buscarPorId(id);

        if (ativo == null) {
            Toast.mostrar(this, "Ativo não encontrado.", Toast.Tipo.ERRO);
            return;
        }

        abrirFormularioEdicao(ativo);
    }

    private void excluirAtivoSelecionado() {
        int id = obterIdSelecionado();

        if (id == -1) return;

        int opcao = JOptionPane.showConfirmDialog(this, "Deseja realmente excluir este ativo?", "Confirmação", JOptionPane.YES_NO_OPTION);

        if (opcao == JOptionPane.YES_OPTION) {
            ativoDAO.excluir(id);
            carregarTabela();
            Toast.mostrar(this, "Ativo excluído com sucesso.", Toast.Tipo.SUCESSO);
        }
    }

    // ================= FORMULÁRIO (substitui a lista no mesmo painel) =================

    private JPanel construirPainelFormulario() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Cores.FUNDO);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Cores.PRIMARIA);
        topo.setBorder(BorderFactory.createEmptyBorder(18, 32, 18, 32));

        lblTituloFormulario = new JLabel("Novo Ativo");
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

        txtTipo = criarCampo();
        txtMarca = criarCampo();
        txtModelo = criarCampo();
        txtNumeroSerie = criarCampo();

        cbUnidade = new JComboBox<>();
        cbUnidade.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cbResponsavel = new JComboBox<>();
        cbResponsavel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cbStatus = new JComboBox<>(new String[]{"Disponível", "Em uso", "Manutenção", "Aguardando formatação"});
        cbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtObservacoes = new JTextArea(5, 20);
        txtObservacoes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtObservacoes.setLineWrap(true);
        txtObservacoes.setWrapStyleWord(true);

        adicionarLinha(form, 0, "Tipo:", txtTipo, true);
        adicionarLinha(form, 1, "Marca:", txtMarca, true);
        adicionarLinha(form, 2, "Modelo:", txtModelo, true);
        adicionarLinha(form, 3, "Número de Série:", txtNumeroSerie, false);
        adicionarLinha(form, 4, "Unidade:", cbUnidade, true);
        adicionarLinha(form, 5, "Responsável:", cbResponsavel, false);
        adicionarLinha(form, 6, "Status:", cbStatus, false);

        JLabel lblObs = new JLabel("Observações:");
        lblObs.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(10, 0, 0, 12);
        form.add(lblObs, gbc);

        JScrollPane scrollObs = new JScrollPane(txtObservacoes);
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 0, 0);
        form.add(scrollObs, gbc);

        JLabel lblLegenda = new JLabel("* Campos obrigatórios");
        lblLegenda.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblLegenda.setForeground(new Color(220, 38, 38));
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 0, 0, 0);
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

        btnSalvar.addActionListener(e -> salvarFormulario());
        btnCancelar.addActionListener(e -> cardLayout.show(painelCards, CARD_LISTA));

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Cores.FUNDO);
        centro.add(form, BorderLayout.CENTER);
        centro.add(botoes, BorderLayout.SOUTH);

        raiz.add(topo, BorderLayout.NORTH);
        raiz.add(new JScrollPane(centro), BorderLayout.CENTER);

        return raiz;
    }

    private JTextField criarCampo() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return campo;
    }

    private void adicionarLinha(JPanel painel, int linha, String texto, JComponent campo, boolean obrigatorio) {
        JLabel label = new JLabel(obrigatorio
                ? "<html>" + texto + " <span style='color:#DC2626;'>*</span></html>"
                : texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 8, 12);
        painel.add(label, gbc);

        gbc.gridx = 1;
        gbc.gridy = linha;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        campo.setPreferredSize(new Dimension(420, 34));
        painel.add(campo, gbc);
    }

    private void abrirFormularioNovo() {
        ativoEmEdicao = null;
        lblTituloFormulario.setText("Novo Ativo");
        limparFormulario();
        carregarCombosFormulario();
        cardLayout.show(painelCards, CARD_FORMULARIO);
    }

    private void abrirFormularioEdicao(Ativo ativo) {
        ativoEmEdicao = ativo;
        lblTituloFormulario.setText("Editar Ativo");
        carregarCombosFormulario();
        preencherFormulario(ativo);
        cardLayout.show(painelCards, CARD_FORMULARIO);
    }

    private void limparFormulario() {
        txtTipo.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtNumeroSerie.setText("");
        txtObservacoes.setText("");
        cbStatus.setSelectedIndex(0);
    }

    private void carregarCombosFormulario() {
        cbUnidade.removeAllItems();
        List<Unidade> unidades = unidadeDAO.listarAtivas();

        if (unidades.isEmpty()) {
            Toast.mostrar(this, "Nenhuma unidade cadastrada — cadastre uma unidade primeiro.", Toast.Tipo.AVISO);
        }

        for (Unidade unidade : unidades) {
            cbUnidade.addItem(unidade);
        }

        cbResponsavel.removeAllItems();
        cbResponsavel.addItem(null); // "sem responsável" — ativo Disponível não precisa de um

        for (Colaborador colaborador : colaboradorDAO.listarAtivos()) {
            cbResponsavel.addItem(colaborador);
        }

        cbResponsavel.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("(sem responsável)");
                } else if (value instanceof Colaborador colaborador) {
                    setText(colaborador.getNome()
                            + (colaborador.getCargo() != null ? " - " + colaborador.getCargo() : ""));
                }
                return this;
            }
        });
    }

    private void preencherFormulario(Ativo ativo) {
        txtTipo.setText(ativo.getTipo());
        txtMarca.setText(ativo.getMarca());
        txtModelo.setText(ativo.getModelo());
        txtNumeroSerie.setText(ativo.getNumeroSerie());
        cbStatus.setSelectedItem(ativo.getStatus());
        txtObservacoes.setText(ativo.getObservacoes());

        for (int i = 0; i < cbUnidade.getItemCount(); i++) {
            Unidade unidade = cbUnidade.getItemAt(i);
            if (unidade.getId() == ativo.getUnidadeId()) {
                cbUnidade.setSelectedIndex(i);
                break;
            }
        }

        if (ativo.getResponsavelId() != null) {
            for (int i = 0; i < cbResponsavel.getItemCount(); i++) {
                Colaborador colaborador = cbResponsavel.getItemAt(i);
                if (colaborador != null && colaborador.getId() == ativo.getResponsavelId()) {
                    cbResponsavel.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void salvarFormulario() {
        if (!validarFormulario()) return;

        Ativo ativo = ativoEmEdicao == null ? new Ativo() : ativoEmEdicao;

        Unidade unidadeSelecionada = (Unidade) cbUnidade.getSelectedItem();
        Colaborador responsavelSelecionado = (Colaborador) cbResponsavel.getSelectedItem();

        ativo.setTipo(txtTipo.getText().trim());
        ativo.setMarca(txtMarca.getText().trim());
        ativo.setModelo(txtModelo.getText().trim());
        ativo.setNumeroSerie(txtNumeroSerie.getText().trim());
        ativo.setUnidadeId(unidadeSelecionada.getId());
        ativo.setResponsavelId(responsavelSelecionado != null ? responsavelSelecionado.getId() : null);
        ativo.setStatus(cbStatus.getSelectedItem().toString());
        ativo.setObservacoes(txtObservacoes.getText().trim());

        if (ativoEmEdicao == null) {
            ativoDAO.salvar(ativo);
            Toast.mostrar(this, "Ativo cadastrado com sucesso.", Toast.Tipo.SUCESSO);
        } else {
            ativoDAO.atualizar(ativo);
            Toast.mostrar(this, "Ativo atualizado com sucesso.", Toast.Tipo.SUCESSO);
        }

        carregarTabela();
        cardLayout.show(painelCards, CARD_LISTA);
    }

    private boolean validarFormulario() {
        if (txtTipo.getText().trim().isEmpty()
                || txtMarca.getText().trim().isEmpty()
                || txtModelo.getText().trim().isEmpty()) {

            Toast.mostrar(this, "Preencha os campos obrigatórios: Tipo, Marca e Modelo.", Toast.Tipo.AVISO);
            return false;
        }

        if (cbUnidade.getSelectedItem() == null) {
            Toast.mostrar(this, "Selecione uma unidade. Se a lista estiver vazia, cadastre uma unidade primeiro.", Toast.Tipo.AVISO);
            return false;
        }

        String statusSelecionado = (String) cbStatus.getSelectedItem();
        if (!"Disponível".equals(statusSelecionado) && cbResponsavel.getSelectedItem() == null) {
            Toast.mostrar(this, "Selecione um responsável — obrigatório quando o status não é 'Disponível'.", Toast.Tipo.AVISO);
            return false;
        }

        return true;
    }
}