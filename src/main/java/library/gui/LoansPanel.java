package library.gui;

import library.model.Loan;
import library.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LoansPanel extends JPanel {
    private final LoanService loanService;
    private final BookService bookService;
    private final PatronService patronService;
    private final AuthService authService;

    private JTable table;
    private DefaultTableModel tableModel;

    private static final String[] COLUMNS = {"ID Empréstimo", "ID Patrono", "ISBN Livro", "Data Saída", "Vencimento", "Status", "Multa (R$)"};

    public LoansPanel(LoanService loanService, BookService bookService,
                      PatronService patronService, AuthService authService) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.patronService = patronService;
        this.authService = authService;
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initUI();
        loadActiveLoans();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        JButton checkOutBtn = new JButton("📤 Emprestar Livro");
        JButton checkInBtn = new JButton("📥 Devolver Livro");
        JButton activeBtn = new JButton("Empréstimos Ativos");
        JButton overdueBtn = new JButton("⚠️ Em Atraso");
        JButton resetFineBtn = new JButton("🔁 Zerar Multa (Admin)");

        checkOutBtn.addActionListener(e -> showCheckOutDialog());
        checkInBtn.addActionListener(e -> showCheckInDialog());
        activeBtn.addActionListener(e -> loadActiveLoans());
        overdueBtn.addActionListener(e -> loadOverdueLoans());
        resetFineBtn.addActionListener(e -> resetFine());
        resetFineBtn.setEnabled(authService.isAdmin());

        topPanel.add(checkOutBtn);
        topPanel.add(checkInBtn);
        topPanel.add(new JSeparator(JSeparator.VERTICAL));
        topPanel.add(activeBtn);
        topPanel.add(overdueBtn);
        topPanel.add(resetFineBtn);
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

    private void loadActiveLoans() {
        loadLoans(loanService.getActiveLoans());
    }

    private void loadOverdueLoans() {
        loadLoans(loanService.getOverdueLoans());
    }

    private void loadLoans(List<Loan> loans) {
        tableModel.setRowCount(0);
        for (Loan l : loans) {
            String status = l.isReturned() ? "Devolvido" : (l.isOverdue() ? "EM ATRASO" : "Ativo");
            double fine = l.calculateFine();
            tableModel.addRow(new Object[]{
                l.getLoanId(), l.getPatronId(), l.getBookIsbn(),
                l.getCheckOutDate(), l.getDueDate(), status,
                fine > 0 ? String.format("%.2f", fine) : "-"
            });
        }
    }

    private void showCheckOutDialog() {
        JTextField patronIdField = new JTextField(12);
        JTextField isbnField = new JTextField(12);
        Object[] message = {"ID do Patrono:", patronIdField, "ISBN do Livro:", isbnField};
        int opt = JOptionPane.showConfirmDialog(this, message, "Emprestar Livro", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            try {
                String patronId = patronIdField.getText().trim();
                String isbn = isbnField.getText().trim();
                Loan loan = loanService.checkOut(patronId, isbn);
                loadActiveLoans();
                JOptionPane.showMessageDialog(this,
                        "Empréstimo realizado!\nID: " + loan.getLoanId() +
                        "\nDevolução: " + loan.getDueDate(),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showCheckInDialog() {
        String loanId = JOptionPane.showInputDialog(this, "Informe o ID do Empréstimo:", "Devolver Livro", JOptionPane.QUESTION_MESSAGE);
        if (loanId != null && !loanId.trim().isEmpty()) {
            try {
                Loan loan = loanService.checkIn(loanId.trim());
                double fine = loan.calculateFine();
                String msg = "Devolução registrada com sucesso!";
                if (fine > 0) msg += "\nMulta devida: R$" + String.format("%.2f", fine);
                loadActiveLoans();
                JOptionPane.showMessageDialog(this, msg, "Devolução", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetFine() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um empréstimo."); return; }
        String loanId = (String) tableModel.getValueAt(row, 0);
        try {
            loanService.resetFine(loanId);
            loadActiveLoans();
            JOptionPane.showMessageDialog(this, "Multa zerada com sucesso.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
