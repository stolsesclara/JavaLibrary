package library.gui;

import library.service.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final AuthService authService;
    private final BookService bookService;
    private final PatronService patronService;
    private final LoanService loanService;
    private final OverdueNotificationService notificationService;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame(AuthService authService, BookService bookService,
                      PatronService patronService, LoanService loanService,
                      OverdueNotificationService notificationService) {
        this.authService = authService;
        this.bookService = bookService;
        this.patronService = patronService;
        this.loanService = loanService;
        this.notificationService = notificationService;

        setTitle("JavaLibrary - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(30, 60, 114));

        // Header
        JLabel titleLabel = new JLabel("📚 JavaLibrary", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(30, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Usuário:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        JButton loginBtn = new JButton("Entrar");
        loginBtn.setBackground(new Color(30, 60, 114));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> doLogin());
        formPanel.add(loginBtn, gbc);

        // Hint
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel hint = new JLabel("<html><center><small>Admin: admin/admin123 | Bibliotecário: librarian/lib123</small></center></html>",
                SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        formPanel.add(hint, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        getRootPane().setDefaultButton(loginBtn);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (authService.login(username, password)) {
            MainFrame mainFrame = new MainFrame(authService, bookService, patronService, loanService, notificationService);
            mainFrame.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Usuário ou senha inválidos.", "Erro de Login",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}
