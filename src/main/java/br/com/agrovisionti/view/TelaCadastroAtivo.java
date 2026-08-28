package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.dao.ColaboradorDAO;
import br.com.agrovisionti.dao.UnidadeDAO;
import br.com.agrovisionti.model.Ativo;
import br.com.agrovisionti.model.Colaborador;
import br.com.agrovisionti.model.Unidade;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaCadastroAtivo extends JDialog {
    private final AtivoDAO ativoDAO = new AtivoDAO();
    private final UnidadeDAO unidadeDAO = new UnidadeDAO();
    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private final Runnable aoSalvar;
    private Ativo ativoEmEdicao;

    private JTextField txtTipo;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtNumeroSerie;
    private JComboBox<Unidade> cbUnidade;
    private JComboBox<Colaborador> cbResponsavel;
    private JComboBox<String> cbStatus;
    private JTextArea txtObservacoes;

    // ===== Uso a partir da tela antiga (TelaAtivos como janela própria) =====
    public TelaCadastroAtivo(TelaAtivos telaAtivos) {
        this(telaAtivos, null, telaAtivos::carregarTabela);
    }

    public TelaCadastroAtivo(TelaAtivos telaAtivos, Ativo ativo) {
        this(telaAtivos, ativo, telaAtivos::carregarTabela);
    }

    // ===== Uso a partir do novo PainelAtivos (embutido no CardLayout) =====
    public TelaCadastroAtivo(Window janelaProprietaria, PainelAtivos painelAtivos) {
        this(janelaProprietaria, null, painelAtivos::carregarTabela);
    }

    public TelaCadastroAtivo(Window janelaProprietaria, PainelAtivos painelAtivos, Ativo ativo) {
        this(janelaProprietaria, ativo, painelAtivos::carregarTabela);
    }

    // ===== Construtor mestre: não sabe (nem precisa saber) quem é o chamador =====
    private TelaCadastroAtivo(Window janelaProprietaria, Ativo ativo, Runnable aoSalvar) {
        super(janelaProprietaria, Dialog.ModalityType.APPLICATION_MODAL);
        this.ativoEmEdicao = ativo;
        this.aoSalvar = aoSalvar;

        setTitle(ativo == null ? "Novo Ativo" : "Editar Ativo");
        setSize(720, 630);
        setLocationRelativeTo(getParent());
        setResizable(false);

        criarComponentes();
        carregarUnidades();
        carregarResponsaveis();

        if (ativo != null) {
            preencherCampos(ativo);
        }

        setVisible(true);
    }

    private void criarComponentes() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Cores.FUNDO);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Cores.PRIMARIA);
        topo.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));

        JLabel titulo = new JLabel(ativoEmEdicao == null ? "Cadastro de Ativo" : "Edição de Ativo");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topo.add(titulo, BorderLayout.WEST);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Cores.FUNDO);
        form.setBorder(BorderFactory.createEmptyBorder(25, 45, 20, 45));

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

        adicionarLinha(form, 0, "Tipo:", txtTipo);
        adicionarLinha(form, 1, "Marca:", txtMarca);
        adicionarLinha(form, 2, "Modelo:", txtModelo);
        adicionarLinha(form, 3, "Número de Série:", txtNumeroSerie);
        adicionarLinha(form, 4, "Unidade:", cbUnidade);
        adicionarLinha(form, 5, "Responsável:", cbResponsavel);
        adicionarLinha(form, 6, "Status:", cbStatus);

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

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        botoes.setBackground(Cores.FUNDO);
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 32));

        JButton btnSalvar = new JButton(ativoEmEdicao == null ? "Salvar" : "Atualizar");
        JButton btnCancelar = new JButton("Cancelar");

        Cores.estilizarBotaoPrimario(btnSalvar);
        Cores.estilizarBotaoSecundario(btnCancelar);

        btnSalvar.setPreferredSize(new Dimension(120, 38));
        btnCancelar.setPreferredSize(new Dimension(120, 38));

        botoes.add(btnSalvar);
        botoes.add(btnCancelar);

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        raiz.add(topo, BorderLayout.NORTH);
        raiz.add(form, BorderLayout.CENTER);
        raiz.add(botoes, BorderLayout.SOUTH);

        add(raiz);
    }

    private void carregarUnidades() {
        List<Unidade> unidades = unidadeDAO.listarAtivas();

        if (unidades.isEmpty()) {
            Toast.mostrar(this, "Nenhuma unidade cadastrada — cadastre uma unidade primeiro.", Toast.Tipo.AVISO);
        }

        for (Unidade unidade : unidades) {
            cbUnidade.addItem(unidade);
        }
    }

    private void carregarResponsaveis() {
        cbResponsavel.addItem(null); // opção "sem responsável" — ativo Disponível não precisa de um

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

    private JTextField criarCampo() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return campo;
    }

    private void adicionarLinha(JPanel painel, int linha, String texto, JComponent campo) {
        JLabel label = new JLabel(texto);
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

    private void salvar() {
        if (!validarCampos()) return;

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
            Toast.mostrar(janelaPaiOuThis(), "Ativo cadastrado com sucesso.", Toast.Tipo.SUCESSO);
        } else {
            ativoDAO.atualizar(ativo);
            Toast.mostrar(janelaPaiOuThis(), "Ativo atualizado com sucesso.", Toast.Tipo.SUCESSO);
        }

        aoSalvar.run();
        dispose();
    }

    // O diálogo fecha antes do toast terminar de aparecer, então mostramos
    // na janela dona (a tela por trás) em vez de "this" — assim o toast
    // continua visível mesmo depois do formulário sumir.
    private Window janelaPaiOuThis() {
        Window dono = getOwner();
        return dono != null ? dono : this;
    }

    private boolean validarCampos() {
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

        // Regra de negócio: só é obrigatório ter responsável se o ativo não estiver "Disponível"
        String statusSelecionado = (String) cbStatus.getSelectedItem();
        if (!"Disponível".equals(statusSelecionado) && cbResponsavel.getSelectedItem() == null) {
            Toast.mostrar(this, "Selecione um responsável — obrigatório quando o status não é 'Disponível'.", Toast.Tipo.AVISO);
            return false;
        }

        return true;
    }

    private void preencherCampos(Ativo ativo) {
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
}