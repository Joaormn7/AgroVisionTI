package br.com.agrovisionti.view;

import br.com.agrovisionti.dao.AtivoDAO;
import br.com.agrovisionti.dao.ColaboradorDAO;
import br.com.agrovisionti.dao.TermoDAO;
import br.com.agrovisionti.model.Ativo;
import br.com.agrovisionti.model.Colaborador;
import br.com.agrovisionti.model.Termo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TelaCadastroTermo extends JDialog {

    private final AtivoDAO ativoDAO = new AtivoDAO();
    private final ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private final TermoDAO termoDAO = new TermoDAO();
    private final Runnable aoSalvar;

    private JComboBox<Ativo> cbAtivo;
    private JComboBox<Colaborador> cbResponsavel;
    private JTextArea txtObservacoes;

    public TelaCadastroTermo(Window janelaProprietaria, Runnable aoSalvar) {
        super(janelaProprietaria, Dialog.ModalityType.APPLICATION_MODAL);
        this.aoSalvar = aoSalvar;

        setTitle("Novo Termo de Responsabilidade");
        setSize(520, 440);
        setLocationRelativeTo(getParent());
        setResizable(false);

        criarComponentes();
        carregarAtivos();
        carregarResponsaveis();

        setVisible(true);
    }

    private void criarComponentes() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Cores.FUNDO);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(Cores.PRIMARIA);
        topo.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel titulo = new JLabel("Cadastro de Termo");
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
        int linha = 0;

        gbc.gridy = linha++;
        form.add(new JLabel("Ativo:"), gbc);
        cbAtivo = new JComboBox<>();
        cbAtivo.setPreferredSize(new Dimension(400, 34));
        gbc.gridy = linha++;
        form.add(cbAtivo, gbc);

        gbc.gridy = linha++;
        form.add(new JLabel("Responsável:"), gbc);
        cbResponsavel = new JComboBox<>();
        cbResponsavel.setPreferredSize(new Dimension(400, 34));
        gbc.gridy = linha++;
        form.add(cbResponsavel, gbc);

        gbc.gridy = linha++;
        form.add(new JLabel("Observações:"), gbc);
        txtObservacoes = new JTextArea(4, 20);
        txtObservacoes.setLineWrap(true);
        txtObservacoes.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtObservacoes);
        gbc.gridy = linha++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        form.add(scrollObs, gbc);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botoes.setBackground(Cores.FUNDO);
        botoes.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 24));

        JButton btnSalvar = new JButton("Salvar");
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

    private void carregarAtivos() {
        List<Ativo> ativos = ativoDAO.listar();

        if (ativos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum ativo cadastrado ainda. Cadastre um ativo antes de emitir um termo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        }

        for (Ativo ativo : ativos) {
            cbAtivo.addItem(ativo);
        }

        cbAtivo.setRenderer(new DefaultListCellRenderer() {
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
        });
    }

    private void carregarResponsaveis() {
        List<Colaborador> colaboradores = colaboradorDAO.listarAtivos();

        if (colaboradores.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum colaborador cadastrado ainda. Cadastre um colaborador antes de emitir um termo.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        }

        for (Colaborador colaborador : colaboradores) {
            cbResponsavel.addItem(colaborador);
        }

        cbResponsavel.setRenderer(new DefaultListCellRenderer() {
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
    }

    private void salvar() {
        Ativo ativoSelecionado = (Ativo) cbAtivo.getSelectedItem();

        if (ativoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um ativo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Colaborador responsavelSelecionado = (Colaborador) cbResponsavel.getSelectedItem();

        if (responsavelSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um responsável.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Termo termo = new Termo(ativoSelecionado.getId(), responsavelSelecionado.getId(),
                txtObservacoes.getText().trim());

        termoDAO.cadastrar(termo);

        JOptionPane.showMessageDialog(this, "Termo cadastrado com sucesso!");

        aoSalvar.run();
        dispose();
    }
}