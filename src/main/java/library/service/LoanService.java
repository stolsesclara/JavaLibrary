package library.service;

import library.exception.BookAlreadyOnLoanException;
import library.exception.BookNotFoundException;
import library.exception.PatronNotFoundException;
import library.model.Book;
import library.model.Loan;
import library.model.Patron;
import library.repository.FileRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class LoanService {
    private final FileRepository<Loan> repository;
    private final BookService bookService;
    private final PatronService patronService;
    private int loanCounter = 1;

    public LoanService(String dataDir, BookService bookService, PatronService patronService) {
        this.repository = new FileRepository<>(dataDir + "/loans.dat");
        this.bookService = bookService;
        this.patronService = patronService;
        // Sync counter with existing data
        repository.getAll().forEach(l -> {
            try {
                int num = Integer.parseInt(l.getLoanId().replace("L", ""));
                if (num >= loanCounter) loanCounter = num + 1;
            } catch (NumberFormatException ignored) {}
        });
    }

    public Loan checkOut(String patronId, String bookIsbn)
            throws PatronNotFoundException, BookNotFoundException, BookAlreadyOnLoanException {

        Patron patron = patronService.getPatron(patronId);
        Book book = bookService.getBook(bookIsbn);

        if (!book.isAvailable())
            throw new BookAlreadyOnLoanException(bookIsbn);

        String loanId = "L" + String.format("%04d", loanCounter++);
        Loan loan = new Loan(loanId, patronId, bookIsbn);

        book.checkOut();
        bookService.addBook(book);

        patron.addToHistory("Emprestado: " + book.getTitle() + " em " + LocalDate.now());
        patronService.addPatron(patron);

        repository.put(loanId, loan);
        return loan;
    }

    public Loan checkIn(String loanId) throws Exception {
        Loan loan = repository.get(loanId)
                .orElseThrow(() -> new Exception("Empréstimo '" + loanId + "' não encontrado."));

        if (loan.isReturned())
            throw new Exception("Este empréstimo já foi encerrado.");

        loan.returnBook();

        Book book = bookService.getBook(loan.getBookIsbn());
        book.checkIn();
        bookService.addBook(book);

        Patron patron = patronService.getPatron(loan.getPatronId());
        patron.addToHistory("Devolvido: " + book.getTitle() + " em " + LocalDate.now());
        patronService.addPatron(patron);

        repository.put(loanId, loan);
        return loan;
    }

    public List<Loan> getActiveLoans() {
        return repository.getAll().stream()
                .filter(l -> !l.isReturned())
                .collect(Collectors.toList());
    }

    public List<Loan> getOverdueLoans() {
        return repository.getAll().stream()
                .filter(l -> !l.isReturned() && l.isOverdue())
                .collect(Collectors.toList());
    }

    public List<Loan> getLoansByPatron(String patronId) {
        return repository.getAll().stream()
                .filter(l -> l.getPatronId().equals(patronId))
                .collect(Collectors.toList());
    }

    public boolean hasActiveLoans(String patronId) {
        return repository.getAll().stream()
                .anyMatch(l -> l.getPatronId().equals(patronId) && !l.isReturned());
    }

    public Collection<Loan> getAllLoans() {
        return repository.getAll();
    }

    public void resetFine(String loanId) throws Exception {
        Loan loan = repository.get(loanId)
                .orElseThrow(() -> new Exception("Empréstimo não encontrado."));
        repository.put(loanId, loan);
    }
}
