package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.ColaboradorDAO;
import br.com.agrovisionti.dao.UnidadeDAO;
import br.com.agrovisionti.model.Colaborador;
import br.com.agrovisionti.model.Unidade;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaCadastroColaborador extends JDialog {

    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private final UnidadeDAO unidadeDAO = new UnidadeDAO();
    private final Runnable aoSalvar;
    private Colaborador colaboradorEmEdicao;

    private JTextField txtNome;
    private JTextField txtCpf;
    private JTextField txtCargo;
    private JTextField txtSetor;
    private JComboBox<Unidade> cbUnidade;

    public TelaCadastroColaborador(Window janelaProprietaria, Runnable aoSalvar) {
        this(janelaProprietaria, null, aoSalvar);
    }

    public TelaCadastroColaborador(Window janelaProprietaria, Colaborador colaborador, Runnable aoSalvar) {
        super(janelaProprietaria, Dialog.ModalityType.APPLICATION_MODAL);
        this.colaboradorEmEdicao = colaborador;
        this.aoSalvar = aoSalvar;

        setTitle(colaborador == null ? "Novo Colaborador" : "Editar Colaborador");
        setSize(520, 490);
        setLocationRelativeTo(getParent());
        setResizable(false);

        criarComponentes();
        carregarUnidades();

        if (colaborador != null) {
            preencherCampos(colaborador);
        }

        setVisible(true);
    }

    private void criarComponentes() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Cores.FUNDO);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Cores.PRIMARIA);
        topo.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel titulo = new JLabel(colaboradorEmEdicao == null ? "Cadastro de Colaborador" : "Edição de Colaborador");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topo.add(titulo, BorderLayout.WEST);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Cores.FUNDO);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        int linha = 0;

        txtNome = criarCampo();
        txtCpf = criarCampo();
        txtCargo = criarCampo();
        txtSetor = criarCampo();

        cbUnidade = new JComboBox<>();
        cbUnidade.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        adicionarLinha(form, gbc, linha++, "Nome:", txtNome, true);
        adicionarLinha(form, gbc, linha++, "CPF:", txtCpf, true);
        adicionarLinha(form, gbc, linha++, "Cargo:", txtCargo, false);
        adicionarLinha(form, gbc, linha++, "Setor:", txtSetor, false);
        adicionarLinha(form, gbc, linha++, "Unidade:", cbUnidade, true);

        JLabel lblLegenda = new JLabel("* Campos obrigatórios");
        lblLegenda.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblLegenda.setForeground(new Color(220, 38, 38));
        gbc.gridy = linha * 2;
        gbc.insets = new Insets(10, 0, 0, 0);
        form.add(lblLegenda, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botoes.setBackground(Cores.FUNDO);
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 24));

        JButton btnSalvar = new JButton(colaboradorEmEdicao == null ? "Salvar" : "Atualizar");
        JButton btnCancelar = new JButton("Cancelar");

        Cores.estilizarBotaoPrimario(btnSalvar);
        Cores.estilizarBotaoSecundario(btnCancelar);

        botoes.add(btnSalvar);
        botoes.add(btnCancelar);

        btnSalvar.addActionListener(e -> salvar());
        btnCancelar.addActionListener(e -> dispose());

        raiz.add(topo, BorderLayout.NORTH);
        raiz.add(form, BorderLayout.CENTER);
        raiz.add(botoes, BorderLayout.SOUTH);

        add(raiz);
    }

    private JTextField criarCampo() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(380, 34));
        return campo;
    }

    private void adicionarLinha(JPanel painel, GridBagConstraints gbc, int linha, String texto, JComponent campo, boolean obrigatorio) {
        JLabel label = new JLabel(obrigatorio
                ? "<html>" + texto + " <span style='color:#DC2626;'>*</span></html>"
                : texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridy = linha * 2;
        painel.add(label, gbc);

        gbc.gridy = linha * 2 + 1;
        painel.add(campo, gbc);
    }

    private void carregarUnidades() {
        List<Unidade> unidades = unidadeDAO.listarAtivas();

        if (unidades.isEmpty()) {
            Toast.mostrar(this, "Nenhuma unidade cadastrada — cadastre uma unidade antes de cadastrar um colaborador.", Toast.Tipo.AVISO);
        }

        for (Unidade unidade : unidades) {
            cbUnidade.addItem(unidade);
        }
    }

    private void salvar() {
        if (!validarCampos()) return;

        Colaborador colaborador = colaboradorEmEdicao == null ? new Colaborador() : colaboradorEmEdicao;

        Unidade unidadeSelecionada = (Unidade) cbUnidade.getSelectedItem();

        colaborador.setNome(txtNome.getText().trim());
        colaborador.setCpf(txtCpf.getText().trim());
        colaborador.setCargo(txtCargo.getText().trim());
        colaborador.setSetor(txtSetor.getText().trim());
        colaborador.setUnidadeId(unidadeSelecionada.getId());

        try {
            if (colaboradorEmEdicao == null) {
                colaboradorDAO.salvar(colaborador);
                Toast.mostrar(janelaPaiOuThis(), "Colaborador cadastrado com sucesso.", Toast.Tipo.SUCESSO);
            } else {
                colaboradorDAO.atualizar(colaborador);
                Toast.mostrar(janelaPaiOuThis(), "Colaborador atualizado com sucesso.", Toast.Tipo.SUCESSO);
            }

            aoSalvar.run();
            dispose();

        } catch (RuntimeException e) {
            Toast.mostrar(this, "Erro ao salvar colaborador. Verifique se o CPF já está cadastrado.", Toast.Tipo.ERRO);
        }
    }

    private Window janelaPaiOuThis() {
        Window dono = getOwner();
        return dono != null ? dono : this;
    }

    private boolean validarCampos() {
        if (txtNome.getText().trim().length() < 2) {
            Toast.mostrar(this, "Informe um nome válido.", Toast.Tipo.AVISO);
            return false;
        }

        String cpfDigitos = txtCpf.getText().replaceAll("\\D", "");

        if (cpfDigitos.length() != 11) {
            Toast.mostrar(this, "CPF inválido — informe os 11 dígitos.", Toast.Tipo.AVISO);
            return false;
        }

        if (cbUnidade.getSelectedItem() == null) {
            Toast.mostrar(this, "Selecione uma unidade. Se a lista estiver vazia, cadastre uma unidade primeiro.", Toast.Tipo.AVISO);
            return false;
        }

        return true;
    }

    private void preencherCampos(Colaborador colaborador) {
        txtNome.setText(colaborador.getNome());
        txtCpf.setText(colaborador.getCpf());
        txtCargo.setText(colaborador.getCargo());
        txtSetor.setText(colaborador.getSetor());

        for (int i = 0; i < cbUnidade.getItemCount(); i++) {
            Unidade unidade = cbUnidade.getItemAt(i);
            if (unidade.getId() == colaborador.getUnidadeId()) {
                cbUnidade.setSelectedIndex(i);
                break;
            }
        }
    }
}