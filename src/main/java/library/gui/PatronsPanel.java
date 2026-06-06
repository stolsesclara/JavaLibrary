package library.gui;

import library.exception.PatronNotFoundException;
import library.model.Patron;
import library.service.AuthService;
import library.service.PatronService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collection;

public class PatronsPanel extends JPanel {
    private final PatronService patronService;
    private final AuthService authService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private static final String[] COLUMNS = {"ID", "Nome", "Email", "Telefone"};

    public PatronsPanel(PatronService patronService, AuthService authService) {
        this.patronService = patronService;
        this.authService = authService;
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initUI();
        loadData(patronService.getAllPatrons());
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(25);
        JButton searchBtn = new JButton("🔍 Buscar");
        JButton clearBtn = new JButton("Limpar");
        searchBtn.addActionListener(e -> search());
        clearBtn.addActionListener(e -> { searchField.setText(""); loadData(patronService.getAllPatrons()); });
        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("➕ Adicionar");
        JButton editBtn = new JButton("✏️ Editar");
        JButton deleteBtn = new JButton("🗑️ Excluir");
        JButton historyBtn = new JButton("📋 Histórico");

        addBtn.addActionListener(e -> showPatronDialog(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        historyBtn.addActionListener(e -> showHistory());

        boolean isAdmin = authService.isAdmin();
        addBtn.setEnabled(isAdmin);
        deleteBtn.setEnabled(isAdmin);

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(historyBtn);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadData(Collection<Patron> patrons) {
        tableModel.setRowCount(0);
        for (Patron p : patrons) {
            tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getEmail(), p.getPhone()});
        }
    }

    private void search() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) { loadData(patronService.getAllPatrons()); return; }
        loadData(patronService.search(q));
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um patrono."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        try {
            showPatronDialog(patronService.getPatron(id));
        } catch (PatronNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um patrono."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o patrono ID: " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                patronService.deletePatron(id);
                loadData(patronService.getAllPatrons());
                JOptionPane.showMessageDialog(this, "Patrono excluído com sucesso.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showHistory() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um patrono."); return; }
        String id = (String) tableModel.getValueAt(row, 0);
        try {
            Patron p = patronService.getPatron(id);
            JTextArea area = new JTextArea();
            area.setEditable(false);
            if (p.getBorrowingHistory().isEmpty()) {
                area.setText("Nenhum histórico encontrado.");
            } else {
                p.getBorrowingHistory().forEach(h -> area.append(h + "\n"));
            }
            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                    "Histórico de " + p.getName(), JOptionPane.INFORMATION_MESSAGE);
        } catch (PatronNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPatronDialog(Patron existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Adicionar Patrono" : "Editar Patrono", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(380, 280);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 8, 7, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"ID:", "Nome:", "Email:", "Telefone:"};
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            dialog.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            fields[i] = new JTextField(18);
            dialog.add(fields[i], gbc);
        }

        if (existing != null) {
            fields[0].setText(existing.getId()); fields[0].setEditable(false);
            fields[1].setText(existing.getName());
            fields[2].setText(existing.getEmail());
            fields[3].setText(existing.getPhone());
        }

        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        JButton saveBtn = new JButton("Salvar");
        saveBtn.addActionListener(e -> {
            try {
                String id = fields[0].getText().trim();
                String name = fields[1].getText().trim();
                String email = fields[2].getText().trim();
                String phone = fields[3].getText().trim();

                if (id.isEmpty() || name.isEmpty())
                    throw new IllegalArgumentException("ID e Nome são obrigatórios.");

                if (existing == null) {
                    if (patronService.idExists(id))
                        throw new IllegalArgumentException("ID já cadastrado.");
                    patronService.addPatron(new Patron(id, name, email, phone));
                } else {
                    existing.setName(name);
                    existing.setEmail(email);
                    existing.setPhone(phone);
                    patronService.updatePatron(existing);
                }

                loadData(patronService.getAllPatrons());
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Patrono salvo com sucesso.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(saveBtn, gbc);
        dialog.setVisible(true);
    }
}
