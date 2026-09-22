package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.UnidadeDAO;
import br.com.agrovisionti.model.Unidade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class PainelUnidades extends JPanel {

    private final UnidadeDAO unidadeDAO = new UnidadeDAO();
    private final boolean podeEditar;

    private static final Map<String, Color> CORES_STATUS = Map.of(
            "Ativa", Cores.STATUS_DISPONIVEL,
            "Desativada", Cores.SUBTITULO
    );

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel painelCards = new JPanel(cardLayout);
    private static final String CARD_LISTA = "LISTA";
    private static final String CARD_FORMULARIO = "FORMULARIO";

    // ---- lista ----
    private JButton btnNova;
    private JButton btnEditar;
    private JButton btnDesativar;
    private JButton btnReativar;
    private JButton btnAtualizar;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private JLabel lblQuantidade;

    // ---- formulário ----
    private JLabel lblTituloFormulario;
    private JTextField txtNome;
    private JComboBox<String> cbTipo;
    private JTextField txtCnpj;
    private Unidade unidadeEmEdicao;

    public PainelUnidades(Window janelaProprietaria, boolean podeEditar) {
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

        JLabel titulo = new JLabel("Gerenciamento de Unidades");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(Cores.TITULO);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoes.setOpaque(false);

        btnNova = new JButton("Nova Unidade");
        btnEditar = new JButton("Editar");
        btnDesativar = new JButton("Desativar");
        btnReativar = new JButton("Reativar");
        btnAtualizar = new JButton("Atualizar");

        Cores.estilizarBotaoPrimario(btnNova);
        Cores.estilizarBotaoSecundario(btnEditar);
        Cores.estilizarBotaoPerigo(btnDesativar);
        Cores.estilizarBotaoSecundario(btnReativar);
        Cores.estilizarBotaoSecundario(btnAtualizar);

        botoes.add(btnNova);
        botoes.add(btnEditar);
        botoes.add(btnDesativar);
        botoes.add(btnReativar);
        botoes.add(btnAtualizar);

        if (!podeEditar) {
            btnNova.setEnabled(false);
            btnEditar.setEnabled(false);
            btnDesativar.setEnabled(false);
            btnReativar.setEnabled(false);
        }

        areaSuperior.add(titulo, BorderLayout.WEST);
        areaSuperior.add(botoes, BorderLayout.EAST);

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Tipo", "CNPJ", "Status"}, 0) {
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
        tabela.getColumnModel().getColumn(4).setCellRenderer(new BadgeCellRenderer(CORES_STATUS));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createLineBorder(Cores.BORDA));

        conteudo.add(areaSuperior, BorderLayout.NORTH);
        conteudo.add(scroll, BorderLayout.CENTER);

        JPanel rodape = new JPanel(new BorderLayout());
        rodape.setBorder(BorderFactory.createEmptyBorder(12, 32, 12, 32));
        rodape.setBackground(Cores.FUNDO);

        lblQuantidade = new JLabel("0 unidade(s) cadastrada(s)");
        lblQuantidade.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rodape.add(lblQuantidade, BorderLayout.WEST);

        conteudo.add(rodape, BorderLayout.SOUTH);

        configurarEventosLista();

        return conteudo;
    }

    private void configurarEventosLista() {
        btnNova.addActionListener(e -> abrirFormularioNovo());
        btnEditar.addActionListener(e -> editarSelecionada());
        btnDesativar.addActionListener(e -> alterarStatusSelecionada(false));
        btnReativar.addActionListener(e -> alterarStatusSelecionada(true));
        btnAtualizar.addActionListener(e -> carregarTabela());
    }

    public void carregarTabela() {
        List<Unidade> unidades = unidadeDAO.listarTodas();
        modeloTabela.setRowCount(0);

        for (Unidade unidade : unidades) {
            modeloTabela.addRow(new Object[]{
                    unidade.getId(),
                    unidade.getNome(),
                    unidade.getTipo(),
                    unidade.getCnpj(),
                    unidade.isAtivo() ? "Ativa" : "Desativada"
            });
        }

        lblQuantidade.setText(unidades.size() + " unidade(s) cadastrada(s)");
    }

    private int obterIdSelecionado() {
        int linha = tabela.getSelectedRow();

        if (linha == -1) {
            Toast.mostrar(this, "Selecione uma unidade na tabela.", Toast.Tipo.AVISO);
            return -1;
        }

        return Integer.parseInt(modeloTabela.getValueAt(linha, 0).toString());
    }

    private void editarSelecionada() {
        int id = obterIdSelecionado();

        if (id == -1) return;

        Unidade unidade = unidadeDAO.listarTodas().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);

        if (unidade == null) {
            Toast.mostrar(this, "Unidade não encontrada.", Toast.Tipo.ERRO);
            return;
        }

        abrirFormularioEdicao(unidade);
    }

    private void alterarStatusSelecionada(boolean ativar) {
        int id = obterIdSelecionado();

        if (id == -1) return;

        if (ativar) {
            unidadeDAO.reativar(id);
            Toast.mostrar(this, "Unidade reativada.", Toast.Tipo.SUCESSO);
        } else {
            unidadeDAO.desativar(id);
            Toast.mostrar(this, "Unidade desativada.", Toast.Tipo.SUCESSO);
        }

        carregarTabela();
    }

    // ================= FORMULÁRIO =================

    private JPanel construirPainelFormulario() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Cores.FUNDO);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Cores.PRIMARIA);
        topo.setBorder(BorderFactory.createEmptyBorder(18, 32, 18, 32));

        lblTituloFormulario = new JLabel("Nova Unidade");
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

        JLabel lblNome = new JLabel("<html>Nome da unidade: <span style='color:#DC2626;'>*</span></html>");
        gbc.gridy = 0;
        form.add(lblNome, gbc);

        txtNome = new JTextField();
        txtNome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtNome.setPreferredSize(new Dimension(360, 34));
        gbc.gridy = 1;
        form.add(txtNome, gbc);

        JLabel lblTipo = new JLabel("Tipo:");
        gbc.gridy = 2;
        form.add(lblTipo, gbc);

        cbTipo = new JComboBox<>(new String[]{"Matriz", "Filial"});
        cbTipo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbTipo.setPreferredSize(new Dimension(360, 34));
        gbc.gridy = 3;
        form.add(cbTipo, gbc);

        JLabel lblCnpj = new JLabel("<html>CNPJ: <span style='color:#DC2626;'>*</span></html>");
        gbc.gridy = 4;
        form.add(lblCnpj, gbc);

        txtCnpj = new JTextField();
        txtCnpj.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCnpj.setPreferredSize(new Dimension(360, 34));
        gbc.gridy = 5;
        form.add(txtCnpj, gbc);

        JLabel lblLegenda = new JLabel("* Campos obrigatórios");
        lblLegenda.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblLegenda.setForeground(new Color(220, 38, 38));
        gbc.gridy = 6;
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

    private void abrirFormularioNovo() {
        unidadeEmEdicao = null;
        lblTituloFormulario.setText("Nova Unidade");
        txtNome.setText("");
        cbTipo.setSelectedIndex(0);
        txtCnpj.setText("");
        cardLayout.show(painelCards, CARD_FORMULARIO);
    }

    private void abrirFormularioEdicao(Unidade unidade) {
        unidadeEmEdicao = unidade;
        lblTituloFormulario.setText("Editar Unidade");
        txtNome.setText(unidade.getNome());
        cbTipo.setSelectedItem(unidade.getTipo());
        txtCnpj.setText(unidade.getCnpj());
        cardLayout.show(painelCards, CARD_FORMULARIO);
    }

    private void salvarFormulario() {
        String nome = txtNome.getText().trim();

        if (nome.isEmpty()) {
            Toast.mostrar(this, "Informe o nome da unidade.", Toast.Tipo.AVISO);
            return;
        }

        String cnpjDigitos = txtCnpj.getText().replaceAll("\\D", "");

        if (cnpjDigitos.length() != 14) {
            Toast.mostrar(this, "CNPJ inválido — informe os 14 dígitos.", Toast.Tipo.AVISO);
            return;
        }

        String tipo = (String) cbTipo.getSelectedItem();
        String cnpj = txtCnpj.getText().trim();

        try {
            if (unidadeEmEdicao == null) {
                Unidade nova = new Unidade(nome, tipo, cnpj);
                unidadeDAO.salvar(nova);
                Toast.mostrar(this, "Unidade cadastrada com sucesso.", Toast.Tipo.SUCESSO);
            } else {
                unidadeEmEdicao.setNome(nome);
                unidadeEmEdicao.setTipo(tipo);
                unidadeEmEdicao.setCnpj(cnpj);
                unidadeDAO.atualizar(unidadeEmEdicao);
                Toast.mostrar(this, "Unidade atualizada com sucesso.", Toast.Tipo.SUCESSO);
            }

            carregarTabela();
            cardLayout.show(painelCards, CARD_LISTA);

        } catch (RuntimeException e) {
            Toast.mostrar(this, "Erro ao salvar unidade. Verifique se o nome ou o CNPJ já estão em uso.", Toast.Tipo.ERRO);
        }
    }
}