package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.UnidadeDAO;
import br.com.agrovisionti.model.Unidade;

import javax.swing.*;
import java.awt.*;

public class TelaCadastroUnidade extends JDialog {

    private final UnidadeDAO unidadeDAO = new UnidadeDAO();
    private final Runnable aoSalvar;
    private Unidade unidadeEmEdicao;

    private JTextField txtNome;
    private JComboBox<String> cbTipo;
    private JTextField txtCnpj;

    public TelaCadastroUnidade(Window janelaProprietaria, Runnable aoSalvar) {
        this(janelaProprietaria, null, aoSalvar);
    }

    public TelaCadastroUnidade(Window janelaProprietaria, Unidade unidade, Runnable aoSalvar) {
        super(janelaProprietaria, Dialog.ModalityType.APPLICATION_MODAL);
        this.unidadeEmEdicao = unidade;
        this.aoSalvar = aoSalvar;

        setTitle(unidade == null ? "Nova Unidade" : "Editar Unidade");
        setSize(420, 380);
        setLocationRelativeTo(getParent());
        setResizable(false);

        criarComponentes();

        if (unidade != null) {
            preencherCampos(unidade);
        }

        setVisible(true);
    }

    private void criarComponentes() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Cores.FUNDO);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Cores.PRIMARIA);
        topo.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel titulo = new JLabel(unidadeEmEdicao == null ? "Cadastro de Unidade" : "Edição de Unidade");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topo.add(titulo, BorderLayout.WEST);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Cores.FUNDO);
        form.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel lblNome = new JLabel("Nome da unidade:");
        gbc.gridy = 0;
        form.add(lblNome, gbc);

        txtNome = new JTextField();
        txtNome.setPreferredSize(new Dimension(300, 34));
        gbc.gridy = 1;
        form.add(txtNome, gbc);

        JLabel lblTipo = new JLabel("Tipo:");
        gbc.gridy = 2;
        form.add(lblTipo, gbc);

        cbTipo = new JComboBox<>(new String[]{"Matriz", "Filial"});
        cbTipo.setPreferredSize(new Dimension(300, 34));
        gbc.gridy = 3;
        form.add(cbTipo, gbc);

        JLabel lblCnpj = new JLabel("CNPJ:");
        gbc.gridy = 4;
        form.add(lblCnpj, gbc);

        txtCnpj = new JTextField();
        txtCnpj.setPreferredSize(new Dimension(300, 34));
        gbc.gridy = 5;
        form.add(txtCnpj, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botoes.setBackground(Cores.FUNDO);
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 24));

        JButton btnSalvar = new JButton(unidadeEmEdicao == null ? "Salvar" : "Atualizar");
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

    private void salvar() {
        String nome = txtNome.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Informe o nome da unidade.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cnpjDigitos = txtCnpj.getText().replaceAll("\\D", "");

        if (cnpjDigitos.length() != 14) {
            JOptionPane.showMessageDialog(this, "CNPJ inválido — informe os 14 dígitos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tipo = (String) cbTipo.getSelectedItem();
        String cnpj = txtCnpj.getText().trim();

        try {
            if (unidadeEmEdicao == null) {
                Unidade nova = new Unidade(nome, tipo, cnpj);
                unidadeDAO.salvar(nova);
                JOptionPane.showMessageDialog(this, "Unidade cadastrada com sucesso.");
            } else {
                unidadeEmEdicao.setNome(nome);
                unidadeEmEdicao.setTipo(tipo);
                unidadeEmEdicao.setCnpj(cnpj);
                unidadeDAO.atualizar(unidadeEmEdicao);
                JOptionPane.showMessageDialog(this, "Unidade atualizada com sucesso.");
            }

            aoSalvar.run();
            dispose();

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar unidade. Verifique se o nome ou o CNPJ já estão em uso.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherCampos(Unidade unidade) {
        txtNome.setText(unidade.getNome());
        cbTipo.setSelectedItem(unidade.getTipo());
        txtCnpj.setText(unidade.getCnpj());
    }
}