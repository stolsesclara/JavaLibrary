package library.service;

import library.exception.BookNotFoundException;
import library.model.Book;
import library.repository.FileRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BookService {
    private final FileRepository<Book> repository;

    public BookService(String dataDir) {
        this.repository = new FileRepository<>(dataDir + "/books.dat");
    }

    public void addBook(Book book) {
        repository.put(book.getIsbn(), book);
    }

    public void updateBook(Book book) throws BookNotFoundException {
        if (!repository.contains(book.getIsbn()))
            throw new BookNotFoundException(book.getIsbn());
        repository.put(book.getIsbn(), book);
    }

    public void deleteBook(String isbn) throws BookNotFoundException {
        if (!repository.contains(isbn))
            throw new BookNotFoundException(isbn);
        repository.remove(isbn);
    }

    public Book getBook(String isbn) throws BookNotFoundException {
        return repository.get(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }

    public Collection<Book> getAllBooks() {
        return repository.getAll();
    }

    public List<Book> search(String query) {
        String q = query.toLowerCase();
        return repository.getAll().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(q)
                        || b.getAuthor().toLowerCase().contains(q)
                        || b.getIsbn().toLowerCase().contains(q)
                        || b.getGenre().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public boolean isbnExists(String isbn) {
        return repository.contains(isbn);
    }
}
