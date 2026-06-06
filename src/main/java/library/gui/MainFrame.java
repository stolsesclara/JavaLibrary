package library.gui;

import library.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private final AuthService authService;
    private final BookService bookService;
    private final PatronService patronService;
    private final LoanService loanService;
    private final OverdueNotificationService notificationService;

    public MainFrame(AuthService authService, BookService bookService,
                     PatronService patronService, LoanService loanService,
                     OverdueNotificationService notificationService) {
        this.authService = authService;
        this.bookService = bookService;
        this.patronService = patronService;
        this.loanService = loanService;
        this.notificationService = notificationService;

        setTitle("JavaLibrary - Sistema de Gerenciamento de Biblioteca");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        JMenu systemMenu = new JMenu("Sistema");
        JMenuItem notifyItem = new JMenuItem("Enviar Notificações de Atraso");
        notifyItem.addActionListener(e -> sendNotifications());
        JMenuItem logoutItem = new JMenuItem("Sair / Logout");
        logoutItem.addActionListener(e -> logout());
        systemMenu.add(notifyItem);
        systemMenu.addSeparator();
        systemMenu.add(logoutItem);
        menuBar.add(systemMenu);
        setJMenuBar(menuBar);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 60, 114));
        header.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("📚 JavaLibrary");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        String userInfo = authService.getCurrentUser().getUsername() +
                " (" + authService.getCurrentUser().getRole() + ")";
        JLabel userLabel = new JLabel(userInfo);
        userLabel.setForeground(new Color(200, 220, 255));
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        header.add(title, BorderLayout.WEST);
        header.add(userLabel, BorderLayout.EAST);

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 13));

        tabs.addTab("📖 Livros", new BooksPanel(bookService, authService));
        tabs.addTab("👤 Patronos", new PatronsPanel(patronService, authService));
        tabs.addTab("🔄 Empréstimos", new LoansPanel(loanService, bookService, patronService, authService));
        tabs.addTab("📊 Relatórios", new ReportsPanel(loanService, bookService, patronService));

        JPanel content = new JPanel(new BorderLayout());
        content.add(header, BorderLayout.NORTH);
        content.add(tabs, BorderLayout.CENTER);

        setContentPane(content);
    }

    private void sendNotifications() {
        int count = notificationService.sendOverdueNotifications();
        JOptionPane.showMessageDialog(this,
                count + " notificação(ões) gravada(s) em data/overdue_notifications.log",
                "Notificações Enviadas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void logout() {
        authService.logout();
        LoginFrame login = new LoginFrame(authService, bookService, patronService, loanService, notificationService);
        login.setVisible(true);
        dispose();
    }
}
