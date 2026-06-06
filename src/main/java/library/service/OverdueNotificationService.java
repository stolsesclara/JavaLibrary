package library.service;

import library.model.Loan;
import library.model.Patron;
import library.exception.PatronNotFoundException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class OverdueNotificationService {
    private final LoanService loanService;
    private final PatronService patronService;
    private static final String LOG_FILE = "data/overdue_notifications.log";

    public OverdueNotificationService(LoanService loanService, PatronService patronService) {
        this.loanService = loanService;
        this.patronService = patronService;
    }

    public int sendOverdueNotifications() {
        List<Loan> overdueLoans = loanService.getOverdueLoans();
        int count = 0;

        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println("=== Notificações de Atraso - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + " ===");

            for (Loan loan : overdueLoans) {
                try {
                    Patron patron = patronService.getPatron(loan.getPatronId());
                    double fine = loan.calculateFine();
                    String message = String.format(
                            "AVISO: %s (%s) - Livro ISBN:%s está %d dias em atraso. Multa: R$%.2f",
                            patron.getName(), patron.getEmail(),
                            loan.getBookIsbn(),
                            java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), java.time.LocalDate.now()),
                            fine);
                    writer.println(message);
                    count++;
                } catch (PatronNotFoundException e) {
                    writer.println("ERRO: Patrono não encontrado para empréstimo " + loan.getLoanId());
                }
            }
            writer.println();
        } catch (IOException e) {
            System.err.println("Erro ao escrever log de notificações: " + e.getMessage());
        }

        return count;
    }
}
