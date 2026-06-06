package library.exception;

public class BookNotFoundException extends Exception {
    public BookNotFoundException(String isbn) {
        super("Livro com ISBN '" + isbn + "' não encontrado.");
    }
}
