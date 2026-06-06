package library.exception;

public class PatronHasActiveLoansException extends Exception {
    public PatronHasActiveLoansException(String patronId) {
        super("O patrono '" + patronId + "' possui empréstimos ativos e não pode ser removido.");
    }
}
