package library.exception;

public class BookAlreadyOnLoanException extends Exception {
    public BookAlreadyOnLoanException(String isbn) {
        super("O livro com ISBN " + isbn + " não possui cópias disponíveis no momento.");
    }
}
