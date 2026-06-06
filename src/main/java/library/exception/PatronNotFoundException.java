package library.exception;

public class PatronNotFoundException extends Exception {
    public PatronNotFoundException(String id) {
        super("Patrono com ID '" + id + "' não encontrado.");
    }
}
