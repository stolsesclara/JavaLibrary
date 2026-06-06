package library.repository;

import java.io.*;
import java.util.*;

public class FileRepository<T extends Serializable> {
    private final String filePath;
    private Map<String, T> data;

    public FileRepository(String filePath) {
        this.filePath = filePath;
        this.data = new LinkedHashMap<>();
        load();
    }

    @SuppressWarnings("unchecked")
    public void load() {
        File file = new File(filePath);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            data = (Map<String, T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar dados de " + filePath + ": " + e.getMessage());
            data = new LinkedHashMap<>();
        }
    }

    public void save() {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados em " + filePath + ": " + e.getMessage());
        }
    }

    public void put(String key, T value) {
        data.put(key, value);
        save();
    }

    public Optional<T> get(String key) {
        return Optional.ofNullable(data.get(key));
    }

    public boolean contains(String key) {
        return data.containsKey(key);
    }

    public void remove(String key) {
        data.remove(key);
        save();
    }

    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(data.values());
    }

    public int size() {
        return data.size();
    }
}
