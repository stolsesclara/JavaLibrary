package library.gui;

import library.exception.BookNotFoundException;
import library.model.Book;
import library.service.AuthService;
import library.service.BookService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collection;
import java.util.List;

public class BooksPanel extends JPanel {
    private final BookService bookService;
    private final AuthService authService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private static final String[] COLUMNS = {"ISBN", "Título", "Autor", "Gênero", "Ano", "Total", "Disponíveis"};

    public BooksPanel(BookService bookService, AuthService authService) {
        this.bookService = bookService;
        this.authService = authService;
        setLayout(new BorderLayout(5, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initUI();
        loadData(bookService.getAllBooks());
    }

    private void initUI() {
        // Top: Search + Buttons
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(25);
        JButton searchBtn = new JButton("🔍 Buscar");
        JButton clearBtn = new JButton("Limpar");
        searchBtn.addActionListener(e -> search());
        clearBtn.addActionListener(e -> { searchField.setText(""); loadData(bookService.getAllBooks()); });
        searchPanel.add(new JLabel("Buscar:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("➕ Adicionar");
        JButton editBtn = new JButton("✏️ Editar");
        JButton deleteBtn = new JButton("🗑️ Excluir");
        addBtn.addActionListener(e -> showBookDialog(null));
        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());

        // Only admin can add/delete
        boolean isAdmin = authService.isAdmin();
        addBtn.setEnabled(isAdmin);
        deleteBtn.setEnabled(isAdmin);

        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(btnPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel status = new JLabel(" Total de livros no sistema");
        status.setFont(new Font("Arial", Font.ITALIC, 11));
        add(status, BorderLayout.SOUTH);
    }

    private void loadData(Collection<Book> books) {
        tableModel.setRowCount(0);
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                b.getIsbn(), b.getTitle(), b.getAuthor(),
                b.getGenre(), b.getYear(), b.getTotalCopies(), b.getAvailableCopies()
            });
        }
    }

    private void search() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) { loadData(bookService.getAllBooks()); return; }
        loadData(bookService.search(q));
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um livro."); return; }
        String isbn = (String) tableModel.getValueAt(row, 0);
        try {
            showBookDialog(bookService.getBook(isbn));
        } catch (BookNotFoundException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Selecione um livro."); return; }
        String isbn = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja excluir o livro ISBN: " + isbn + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                bookService.deleteBook(isbn);
                loadData(bookService.getAllBooks());
                JOptionPane.showMessageDialog(this, "Livro excluído com sucesso.");
            } catch (BookNotFoundException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showBookDialog(Book existing) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                existing == null ? "Adicionar Livro" : "Editar Livro", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(400, 360);
        dialog.setLocationRelativeTo(this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"ISBN:", "Título:", "Autor:", "Gênero:", "Ano:", "Cópias Totais:"};
        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            dialog.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            fields[i] = new JTextField(18);
            dialog.add(fields[i], gbc);
        }

        if (existing != null) {
            fields[0].setText(existing.getIsbn()); fields[0].setEditable(false);
            fields[1].setText(existing.getTitle());
            fields[2].setText(existing.getAuthor());
            fields[3].setText(existing.getGenre());
            fields[4].setText(String.valueOf(existing.getYear()));
            fields[5].setText(String.valueOf(existing.getTotalCopies()));
        }

        gbc.gridx = 0; gbc.gridy = labels.length; gbc.gridwidth = 2;
        JButton saveBtn = new JButton("Salvar");
        saveBtn.addActionListener(e -> {
            try {
                String isbn = fields[0].getText().trim();
                String title = fields[1].getText().trim();
                String author = fields[2].getText().trim();
                String genre = fields[3].getText().trim();
                int year = Integer.parseInt(fields[4].getText().trim());
                int copies = Integer.parseInt(fields[5].getText().trim());

                if (isbn.isEmpty() || title.isEmpty() || author.isEmpty())
                    throw new IllegalArgumentException("ISBN, Título e Autor são obrigatórios.");

                if (existing == null) {
                    if (bookService.isbnExists(isbn))
                        throw new IllegalArgumentException("ISBN já cadastrado.");
                    bookService.addBook(new Book(isbn, title, author, genre, year, copies));
                } else {
                    existing.setTitle(title);
                    existing.setAuthor(author);
                    existing.setGenre(genre);
                    existing.setYear(year);
                    existing.setTotalCopies(copies);
                    bookService.updateBook(existing);
                }

                loadData(bookService.getAllBooks());
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Livro salvo com sucesso.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Ano e Cópias devem ser números.", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(saveBtn, gbc);
        dialog.setVisible(true);
    }
}
