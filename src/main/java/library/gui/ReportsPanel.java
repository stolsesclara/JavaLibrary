package library.gui;

import library.exception.PatronNotFoundException;
import library.model.Book;
import library.model.Loan;
import library.model.Patron;
import library.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsPanel extends JPanel {
    private final LoanService loanService;
    private final BookService bookService;
    private final PatronService patronService;

    private JTextArea reportArea;
    private JTable table;
    private DefaultTableModel tableModel;

    public ReportsPanel(LoanService loanService, BookService bookService, PatronService patronService) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.patronService = patronService;
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initUI();
    }

    private void initUI() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));

        JButton activeBtn = new JButton("📋 Livros Emprestados");
        JButton overdueBtn = new JButton("⚠️ Empréstimos em Atraso");
        JButton patronHistBtn = new JButton("👤 Histórico de Patrono");
        JButton statsBtn = new JButton("📊 Estatísticas");

        activeBtn.addActionListener(e -> showActiveLoansReport());
        overdueBtn.addActionListener(e -> showOverdueReport());
        patronHistBtn.addActionListener(e -> showPatronHistoryReport());
        statsBtn.addActionListener(e -> showStatsReport());

        btnPanel.add(activeBtn);
        btnPanel.add(overdueBtn);
        btnPanel.add(patronHistBtn);
        btnPanel.add(statsBtn);
        add(btnPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Info 1", "Info 2", "Info 3", "Info 4", "Info 5"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(table), new JScrollPane(reportArea));
        split.setDividerLocation(350);
        add(split, BorderLayout.CENTER);
    }

    private void showActiveLoansReport() {
        List<Loan> active = loanService.getActiveLoans();
        tableModel.setColumnIdentifiers(new String[]{"ID Empréstimo", "Patrono", "ISBN", "Vencimento", "Status"});
        tableModel.setRowCount(0);
        reportArea.setText("=== LIVROS ATUALMENTE EMPRESTADOS ===\nTotal: " + active.size() + "\n\n");
        for (Loan l : active) {
            String status = l.isOverdue() ? "EM ATRASO" : "No prazo";
            tableModel.addRow(new Object[]{l.getLoanId(), l.getPatronId(), l.getBookIsbn(), l.getDueDate(), status});
            reportArea.append(l + "\n");
        }
    }

    private void showOverdueReport() {
        List<Loan> overdue = loanService.getOverdueLoans();
        tableModel.setColumnIdentifiers(new String[]{"ID Empréstimo", "Patrono", "Email", "ISBN", "Multa (R$)"});
        tableModel.setRowCount(0);
        reportArea.setText("=== EMPRÉSTIMOS EM ATRASO ===\nTotal: " + overdue.size() + "\n\n");

        for (Loan l : overdue) {
            try {
                Patron p = patronService.getPatron(l.getPatronId());
                double fine = l.calculateFine();
                tableModel.addRow(new Object[]{
                    l.getLoanId(), p.getName(), p.getEmail(),
                    l.getBookIsbn(), String.format("%.2f", fine)
                });
                reportArea.append(String.format("%-10s | %-20s | %-25s | ISBN: %-15s | Multa: R$%.2f%n",
                        l.getLoanId(), p.getName(), p.getEmail(), l.getBookIsbn(), fine));
            } catch (PatronNotFoundException e) {
                reportArea.append("Patrono não encontrado: " + l.getPatronId() + "\n");
            }
        }
    }

    private void showPatronHistoryReport() {
        String id = JOptionPane.showInputDialog(this, "Informe o ID do Patrono:", "Histórico", JOptionPane.QUESTION_MESSAGE);
        if (id == null || id.trim().isEmpty()) return;
        try {
            Patron p = patronService.getPatron(id.trim());
            List<Loan> loans = loanService.getLoansByPatron(id.trim());

            tableModel.setColumnIdentifiers(new String[]{"ID Empréstimo", "ISBN", "Data Saída", "Vencimento", "Devolvido"});
            tableModel.setRowCount(0);

            reportArea.setText("=== HISTÓRICO DE: " + p.getName().toUpperCase() + " ===\n\n");
            for (Loan l : loans) {
                tableModel.addRow(new Object[]{l.getLoanId(), l.getBookIsbn(), l.getCheckOutDate(), l.getDueDate(), l.isReturned() ? "Sim" : "Não"});
                reportArea.append(l + "\n");
            }
            if (loans.isEmpty()) reportArea.append("Nenhum empréstimo registrado.\n");
        } catch (PatronNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showStatsReport() {
        tableModel.setColumnIdentifiers(new String[]{"Métrica", "Valor", "", "", ""});
        tableModel.setRowCount(0);

        long totalBooks = bookService.getAllBooks().size();
        long totalPatrons = patronService.getAllPatrons().size();
        long activeLoans = loanService.getActiveLoans().size();
        long overdueLoans = loanService.getOverdueLoans().size();
        double totalFines = loanService.getOverdueLoans().stream().mapToDouble(Loan::calculateFine).sum();

        tableModel.addRow(new Object[]{"Total de Livros", totalBooks, "", "", ""});
        tableModel.addRow(new Object[]{"Total de Patronos", totalPatrons, "", "", ""});
        tableModel.addRow(new Object[]{"Empréstimos Ativos", activeLoans, "", "", ""});
        tableModel.addRow(new Object[]{"Empréstimos em Atraso", overdueLoans, "", "", ""});
        tableModel.addRow(new Object[]{"Multas Totais (R$)", String.format("%.2f", totalFines), "", "", ""});

        // Most borrowed books
        Map<String, Long> bookCount = loanService.getAllLoans().stream()
                .collect(Collectors.groupingBy(Loan::getBookIsbn, Collectors.counting()));
        bookCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .forEach(e -> {
                    try {
                        Book b = bookService.getBook(e.getKey());
                        tableModel.addRow(new Object[]{"Mais emprestado", b.getTitle(), "(" + e.getValue() + " vezes)", "", ""});
                    } catch (Exception ex) { /* ignore */ }
                });

        reportArea.setText("=== DASHBOARD DE ESTATÍSTICAS ===\n\n" +
                "Livros: " + totalBooks + " | Patronos: " + totalPatrons +
                " | Ativos: " + activeLoans + " | Atraso: " + overdueLoans +
                " | Multas: R$" + String.format("%.2f", totalFines));
    }
}
