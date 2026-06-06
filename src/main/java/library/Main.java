package library;

import library.gui.LoginFrame;
import library.service.*;

import javax.swing.*;

public class Main {
    public static final String DATA_DIR = "data";

    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize services
        AuthService authService = new AuthService();
        BookService bookService = new BookService(DATA_DIR);
        PatronService patronService = new PatronService(DATA_DIR);
        LoanService loanService = new LoanService(DATA_DIR, bookService, patronService);
        patronService.setLoanService(loanService);
        OverdueNotificationService notificationService = new OverdueNotificationService(loanService, patronService);

        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame(authService, bookService, patronService, loanService, notificationService);
            loginFrame.setVisible(true);
        });
    }
}
