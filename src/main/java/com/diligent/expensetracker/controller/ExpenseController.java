package com.diligent.expensetracker.controller;

import com.diligent.expensetracker.model.ExpenseRequest;
import com.diligent.expensetracker.model.TotalResponse;
import com.diligent.expensetracker.model.Expense;
import com.diligent.expensetracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Expense> addExpense(@Valid @RequestBody ExpenseRequest request) {
        Expense created = service.addExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/expenses            -> all expenses
    // GET /api/expenses?category=X -> expenses filtered by category
    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(service.getExpenses(Optional.ofNullable(category)));
    }

    // GET /api/expenses/total            -> total across all expenses
    // GET /api/expenses/total?category=X -> total for a single category
    @GetMapping("/total")
    public ResponseEntity<TotalResponse> getTotal(
            @RequestParam(required = false) String category) {
        var total = service.getTotal(Optional.ofNullable(category));
        return ResponseEntity.ok(new TotalResponse(category, total));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String id) {
        service.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
