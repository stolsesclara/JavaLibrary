package library.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final double FINE_PER_DAY = 1.50;
    private static final int DEFAULT_LOAN_DAYS = 14;

    private String loanId;
    private String patronId;
    private String bookIsbn;
    private LocalDate checkOutDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;

    public Loan(String loanId, String patronId, String bookIsbn) {
        this.loanId = loanId;
        this.patronId = patronId;
        this.bookIsbn = bookIsbn;
        this.checkOutDate = LocalDate.now();
        this.dueDate = checkOutDate.plusDays(DEFAULT_LOAN_DAYS);
        this.returned = false;
    }

    public String getLoanId() { return loanId; }
    public String getPatronId() { return patronId; }
    public String getBookIsbn() { return bookIsbn; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returned; }

    public void returnBook() {
        this.returnDate = LocalDate.now();
        this.returned = true;
    }

    public boolean isOverdue() {
        if (returned) return returnDate.isAfter(dueDate);
        return LocalDate.now().isAfter(dueDate);
    }

    public double calculateFine() {
        if (!isOverdue()) return 0.0;
        LocalDate endDate = returned ? returnDate : LocalDate.now();
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, endDate);
        return daysOverdue * FINE_PER_DAY;
    }

    @Override
    public String toString() {
        return String.format("Empréstimo[%s] Patrono:%s Livro:%s Venc:%s %s",
                loanId, patronId, bookIsbn, dueDate, returned ? "(Devolvido)" : "(Ativo)");
    }
}
