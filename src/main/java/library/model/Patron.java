package library.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Patron implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String email;
    private String phone;
    private List<String> borrowingHistory;

    public Patron(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.borrowingHistory = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<String> getBorrowingHistory() { return borrowingHistory; }

    public void addToHistory(String record) {
        borrowingHistory.add(record);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Email: %s | Tel: %s", id, name, email, phone);
    }
}
