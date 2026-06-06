package library.service;

import library.exception.PatronNotFoundException;
import library.exception.PatronHasActiveLoansException;
import library.model.Patron;
import library.repository.FileRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PatronService {
    private final FileRepository<Patron> repository;
    private LoanService loanService;

    public PatronService(String dataDir) {
        this.repository = new FileRepository<>(dataDir + "/patrons.dat");
    }

    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

    public void addPatron(Patron patron) {
        repository.put(patron.getId(), patron);
    }

    public void updatePatron(Patron patron) throws PatronNotFoundException {
        if (!repository.contains(patron.getId()))
            throw new PatronNotFoundException(patron.getId());
        repository.put(patron.getId(), patron);
    }

    public void deletePatron(String id) throws PatronNotFoundException, PatronHasActiveLoansException {
        if (!repository.contains(id))
            throw new PatronNotFoundException(id);
        if (loanService != null && loanService.hasActiveLoans(id))
            throw new PatronHasActiveLoansException(id);
        repository.remove(id);
    }

    public Patron getPatron(String id) throws PatronNotFoundException {
        return repository.get(id)
                .orElseThrow(() -> new PatronNotFoundException(id));
    }

    public Collection<Patron> getAllPatrons() {
        return repository.getAll();
    }

    public List<Patron> search(String query) {
        String q = query.toLowerCase();
        return repository.getAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(q)
                        || p.getId().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public boolean idExists(String id) {
        return repository.contains(id);
    }
}
