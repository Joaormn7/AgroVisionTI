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
        setSize(520, 460);
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

        adicionarLinha(form, gbc, linha++, "Nome:", txtNome);
        adicionarLinha(form, gbc, linha++, "CPF:", txtCpf);
        adicionarLinha(form, gbc, linha++, "Cargo:", txtCargo);
        adicionarLinha(form, gbc, linha++, "Setor:", txtSetor);
        adicionarLinha(form, gbc, linha++, "Unidade:", cbUnidade);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botoes.setBackground(Cores.FUNDO);
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 24));

        JButton btnSalvar = new JButton(colaboradorEmEdicao == null ? "Salvar" : "Atualizar");
        JButton btnCancelar = new JButton("Cancelar");

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

    private void adicionarLinha(JPanel painel, GridBagConstraints gbc, int linha, String texto, JComponent campo) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridy = linha * 2;
        painel.add(label, gbc);

        gbc.gridy = linha * 2 + 1;
        painel.add(campo, gbc);
    }

    private void carregarUnidades() {
        List<Unidade> unidades = unidadeDAO.listarAtivas();

        if (unidades.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nenhuma unidade cadastrada ainda. Cadastre uma unidade antes de cadastrar um colaborador.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Colaborador cadastrado com sucesso.");
            } else {
                colaboradorDAO.atualizar(colaborador);
                JOptionPane.showMessageDialog(this, "Colaborador atualizado com sucesso.");
            }

            aoSalvar.run();
            dispose();

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar colaborador. Verifique se o CPF já está cadastrado.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        if (txtNome.getText().trim().length() < 2) {
            JOptionPane.showMessageDialog(this, "Informe um nome válido.");
            return false;
        }

        String cpfDigitos = txtCpf.getText().replaceAll("\\D", "");

        if (cpfDigitos.length() != 11) {
            JOptionPane.showMessageDialog(this, "CPF inválido — informe os 11 dígitos.");
            return false;
        }

        if (cbUnidade.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma unidade. Se a lista estiver vazia, cadastre uma unidade primeiro.");
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