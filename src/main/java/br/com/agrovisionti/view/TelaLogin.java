package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.UsuarioDAO;
import br.com.agrovisionti.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class TelaLogin extends JFrame {
    private JTextField txtEmail;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnSair;

    public TelaLogin() {
        setTitle("AgroVisionTI - Login");
        setSize(520, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        criarComponentes();
        setVisible(true);
    }

    private void criarComponentes() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(Cores.FUNDO);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel titulo = new JLabel("AgroVisionTI", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titulo.setForeground(Cores.PRIMARIA);
        gbc.gridy = 0;
        card.add(titulo, gbc);

        JLabel subtitulo = new JLabel("Gerenciamento de Ativos de TI", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridy = 1;
        card.add(subtitulo, gbc);

        JLabel lblEmail = new JLabel("E-mail");
        gbc.gridy = 2;
        gbc.insets = new Insets(20, 0, 4, 0);
        card.add(lblEmail, gbc);

        txtEmail = new JTextField();
        txtEmail.setPreferredSize(new Dimension(340, 36));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 8, 0);
        card.add(txtEmail, gbc);

        JLabel lblSenha = new JLabel("Senha");
        gbc.gridy = 4;
        gbc.insets = new Insets(8, 0, 4, 0);
        card.add(lblSenha, gbc);

        txtSenha = new JPasswordField();
        txtSenha.setPreferredSize(new Dimension(340, 36));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 15, 0);
        card.add(txtSenha, gbc);

        JPanel botoes = new JPanel(new GridLayout(1, 2, 12, 0));
        botoes.setOpaque(false);

        btnEntrar = new JButton("Entrar");
        btnSair = new JButton("Sair");

        btnEntrar.setBackground(Cores.PRIMARIA);
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setOpaque(true);
        btnEntrar.setBorderPainted(false);

        botoes.add(btnEntrar);
        botoes.add(btnSair);

        gbc.gridy = 6;
        gbc.insets = new Insets(8, 0, 0, 0);
        card.add(botoes, gbc);

        painel.add(card);
        add(painel);

        btnEntrar.addActionListener(e -> realizarLogin());
        btnSair.addActionListener(e -> System.exit(0));
        txtSenha.addActionListener(e -> realizarLogin());
    }

    private void realizarLogin() {
        String email = txtEmail.getText().trim();
        String senha = String.valueOf(txtSenha.getPassword());

        if (email.isBlank() || senha.isBlank()) {
            JOptionPane.showMessageDialog(this, "Informe e-mail e senha.");
            return;
        }

        try {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario usuario = dao.autenticar(email, senha);

            if (usuario != null) {

                dispose();

                new TelaPrincipal(usuario);

            } else {

                JOptionPane.showMessageDialog(this, "E-mail ou senha inválidos.");

            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}