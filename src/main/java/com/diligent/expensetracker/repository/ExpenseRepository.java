package com.diligent.expensetracker.repository;

import com.diligent.expensetracker.model.Expense;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory store. A ConcurrentHashMap is enough to make add/read/delete
 * thread-safe without pulling in a database for a take-home assignment.
 * Data does not survive a restart - the assignment explicitly allows this.
 */
@Repository
public class ExpenseRepository {

    private final Map<String, Expense> store = new ConcurrentHashMap<>();

    public Expense save(Expense expense) {
        store.put(expense.getId(), expense);
        return expense;
    }

    public List<Expense> findAll() {
        return List.copyOf(store.values());
    }

    public Optional<Expense> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * @return true if a record existed and was removed, false if there was nothing to delete.
     */
    public boolean deleteById(String id) {
        return store.remove(id) != null;
    }
}
