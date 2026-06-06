package library.model;

import java.io.Serializable;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private String isbn;
    private String title;
    private String author;
    private String genre;
    private int year;
    private int totalCopies;
    private int availableCopies;

    public Book(String isbn, String title, String author, String genre, int year, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }

    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public boolean isAvailable() { return availableCopies > 0; }

    public void checkOut() {
        if (availableCopies > 0) availableCopies--;
    }

    public void checkIn() {
        if (availableCopies < totalCopies) availableCopies++;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s (%d) | Disponíveis: %d/%d",
                isbn, title, author, year, availableCopies, totalCopies);
    }
}
