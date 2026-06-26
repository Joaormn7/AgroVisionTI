package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.model.Ativo;

import javax.swing.*;
import java.awt.*;

public class TelaCadastroAtivo extends JDialog {
    private final TelaAtivos telaAtivos;
    private final AtivoDAO ativoDAO = new AtivoDAO();
    private Ativo ativoEmEdicao;

    private JTextField txtTipo;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtNumeroSerie;
    private JTextField txtUnidade;
    private JTextField txtResponsavel;
    private JComboBox<String> cbStatus;
    private JTextArea txtObservacoes;

    public TelaCadastroAtivo(TelaAtivos telaAtivos) {
        this(telaAtivos, null);
    }

    public TelaCadastroAtivo(TelaAtivos telaAtivos, Ativo ativo) {
        super(telaAtivos, true);
        this.telaAtivos = telaAtivos;
        this.ativoEmEdicao = ativo;

        setTitle(ativo == null ? "Novo Ativo" : "Editar Ativo");
        setSize(720, 610);
        setLocationRelativeTo(getParent());
        setResizable(false);

        criarComponentes();

        if (ativo != null) {
            preencherCampos(ativo);
        }

        setVisible(true);
    }

    private void criarComponentes() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(new Color(245, 247, 250));

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(37, 99, 235));
        topo.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));

        JLabel titulo = new JLabel(ativoEmEdicao == null ? "Cadastro de Ativo" : "Edição de Ativo");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topo.add(titulo, BorderLayout.WEST);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 247, 250));
        form.setBorder(BorderFactory.createEmptyBorder(25, 45, 20, 45));

        txtTipo = criarCampo();
        txtMarca = criarCampo();
        txtModelo = criarCampo();
        txtNumeroSerie = criarCampo();
        txtUnidade = criarCampo();
        txtResponsavel = criarCampo();

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
        adicionarLinha(form, 4, "Unidade:", txtUnidade);
        adicionarLinha(form, 5, "Responsável:", txtResponsavel);
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
        botoes.setBackground(new Color(245, 247, 250));
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 32));

        JButton btnSalvar = new JButton(ativoEmEdicao == null ? "Salvar" : "Atualizar");
        JButton btnCancelar = new JButton("Cancelar");

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

        ativo.setTipo(txtTipo.getText().trim());
        ativo.setMarca(txtMarca.getText().trim());
        ativo.setModelo(txtModelo.getText().trim());
        ativo.setNumeroSerie(txtNumeroSerie.getText().trim());
        ativo.setUnidade(txtUnidade.getText().trim());
        ativo.setResponsavel(txtResponsavel.getText().trim());
        ativo.setStatus(cbStatus.getSelectedItem().toString());
        ativo.setObservacoes(txtObservacoes.getText().trim());

        if (ativoEmEdicao == null) {
            ativoDAO.salvar(ativo);
            JOptionPane.showMessageDialog(this, "Ativo cadastrado com sucesso.");
        } else {
            ativoDAO.atualizar(ativo);
            JOptionPane.showMessageDialog(this, "Ativo atualizado com sucesso.");
        }

        telaAtivos.carregarTabela();
        dispose();
    }

    private boolean validarCampos() {
        if (txtTipo.getText().trim().isEmpty()
                || txtMarca.getText().trim().isEmpty()
                || txtModelo.getText().trim().isEmpty()
                || txtUnidade.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Preencha os campos obrigatórios: Tipo, Marca, Modelo e Unidade.");
            return false;
        }

        return true;
    }

    private void preencherCampos(Ativo ativo) {
        txtTipo.setText(ativo.getTipo());
        txtMarca.setText(ativo.getMarca());
        txtModelo.setText(ativo.getModelo());
        txtNumeroSerie.setText(ativo.getNumeroSerie());
        txtUnidade.setText(ativo.getUnidade());
        txtResponsavel.setText(ativo.getResponsavel());
        cbStatus.setSelectedItem(ativo.getStatus());
        txtObservacoes.setText(ativo.getObservacoes());
    }
}
