package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.UsuarioDAO;
import br.com.agrovisionti.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class TelaCadastroUsuario extends JDialog {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Runnable aoSalvar;
    private Usuario usuarioEmEdicao;

    private JTextField txtNome;
    private JTextField txtEmail;
    private JComboBox<String> cbPerfil;
    private JCheckBox chkAlterarSenha;
    private JPasswordField txtSenha;
    private JLabel lblSenha;

    public TelaCadastroUsuario(Window janelaProprietaria, Runnable aoSalvar) {
        this(janelaProprietaria, null, aoSalvar);
    }

    public TelaCadastroUsuario(Window janelaProprietaria, Usuario usuario, Runnable aoSalvar) {
        super(janelaProprietaria, Dialog.ModalityType.APPLICATION_MODAL);
        this.usuarioEmEdicao = usuario;
        this.aoSalvar = aoSalvar;

        setTitle(usuario == null ? "Novo Usuário" : "Editar Usuário");
        setSize(440, usuario == null ? 400 : 440);
        setLocationRelativeTo(getParent());
        setResizable(false);

        criarComponentes();

        if (usuario != null) {
            preencherCampos(usuario);
        }

        setVisible(true);
    }

    private void criarComponentes() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Cores.FUNDO);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Cores.PRIMARIA);
        topo.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel titulo = new JLabel(usuarioEmEdicao == null ? "Cadastro de Usuário" : "Edição de Usuário");
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

        gbc.gridy = linha++;
        form.add(new JLabel("Nome:"), gbc);
        txtNome = new JTextField();
        txtNome.setPreferredSize(new Dimension(320, 34));
        gbc.gridy = linha++;
        form.add(txtNome, gbc);

        gbc.gridy = linha++;
        form.add(new JLabel("E-mail:"), gbc);
        txtEmail = new JTextField();
        txtEmail.setPreferredSize(new Dimension(320, 34));
        gbc.gridy = linha++;
        form.add(txtEmail, gbc);

        gbc.gridy = linha++;
        form.add(new JLabel("Perfil:"), gbc);
        cbPerfil = new JComboBox<>(new String[]{"Administrador", "Visualizador"});
        cbPerfil.setPreferredSize(new Dimension(320, 34));
        gbc.gridy = linha++;
        form.add(cbPerfil, gbc);

        if (usuarioEmEdicao != null) {
            chkAlterarSenha = new JCheckBox("Alterar senha");
            chkAlterarSenha.setOpaque(false);
            chkAlterarSenha.addActionListener(e -> alternarCampoSenha());
            gbc.gridy = linha++;
            form.add(chkAlterarSenha, gbc);
        }

        lblSenha = new JLabel("Senha:");
        gbc.gridy = linha++;
        form.add(lblSenha, gbc);

        txtSenha = new JPasswordField();
        txtSenha.setPreferredSize(new Dimension(320, 34));
        gbc.gridy = linha++;
        form.add(txtSenha, gbc);

        if (usuarioEmEdicao != null) {
            lblSenha.setVisible(false);
            txtSenha.setVisible(false);
        }

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botoes.setBackground(Cores.FUNDO);
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 24));

        JButton btnSalvar = new JButton(usuarioEmEdicao == null ? "Salvar" : "Atualizar");
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

    private void alternarCampoSenha() {
        boolean mostrar = chkAlterarSenha.isSelected();
        lblSenha.setVisible(mostrar);
        txtSenha.setVisible(mostrar);
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String email = txtEmail.getText().trim();
        String perfil = (String) cbPerfil.getSelectedItem();

        if (nome.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha nome e e-mail.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (usuarioEmEdicao == null) {
                String senha = new String(txtSenha.getPassword());

                if (senha.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Informe uma senha.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Usuario novo = new Usuario();
                novo.setNome(nome);
                novo.setEmail(email);
                novo.setPerfil(perfil);

                usuarioDAO.cadastrar(novo, senha);
                JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso.");

            } else {
                usuarioEmEdicao.setNome(nome);
                usuarioEmEdicao.setEmail(email);
                usuarioEmEdicao.setPerfil(perfil);
                usuarioDAO.atualizar(usuarioEmEdicao);

                if (chkAlterarSenha.isSelected()) {
                    String novaSenha = new String(txtSenha.getPassword());

                    if (novaSenha.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Informe a nova senha ou desmarque 'Alterar senha'.",
                                "Aviso", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    usuarioDAO.definirSenha(email, novaSenha);
                }

                JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso.");
            }

            aoSalvar.run();
            dispose();

        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar usuário. Verifique se o e-mail já está em uso.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void preencherCampos(Usuario usuario) {
        txtNome.setText(usuario.getNome());
        txtEmail.setText(usuario.getEmail());
        cbPerfil.setSelectedItem(usuario.getPerfil());
    }
}