package com.diligent.expensetracker.service;

import com.diligent.expensetracker.model.ExpenseRequest;
import com.diligent.expensetracker.model.ExpenseNotFoundException;
import com.diligent.expensetracker.model.Expense;
import com.diligent.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public Expense addExpense(ExpenseRequest request) {
        Expense expense = new Expense(
                UUID.randomUUID().toString(),
                request.getTitle(),
                request.getAmount(),
                request.getCategory(),
                request.getDate()
        );
        return repository.save(expense);
    }

    public List<Expense> getExpenses(Optional<String> category) {
        List<Expense> all = repository.findAll();
        if (category.isEmpty()) {
            return all;
        }
        String wanted = category.get();
        return all.stream()
                .filter(e -> e.getCategory() != null && e.getCategory().equalsIgnoreCase(wanted))
                .toList();
    }

    public void deleteExpense(String id) {
        boolean removed = repository.deleteById(id);
        if (!removed) {
            throw new ExpenseNotFoundException(id);
        }
    }

    public BigDecimal getTotal(Optional<String> category) {
        return getExpenses(category).stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
