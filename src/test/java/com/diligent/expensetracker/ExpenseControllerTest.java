package com.diligent.expensetracker;

import com.diligent.expensetracker.model.ExpenseRequest;
import com.diligent.expensetracker.model.TotalResponse;
import com.diligent.expensetracker.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExpenseControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/expenses";
    }

    @BeforeEach
    void cleanSlate() {
        // Each test starts by draining whatever the previous test created,
        // since the store is a shared in-memory singleton for the app context.
        Expense[] existing = restTemplate.getForObject(baseUrl(), Expense[].class);
        if (existing != null) {
            for (Expense e : existing) {
                restTemplate.delete(baseUrl() + "/" + e.getId());
            }
        }
    }

    private ExpenseRequest sampleRequest(String title, String amount, String category, String date) {
        ExpenseRequest req = new ExpenseRequest();
        req.setTitle(title);
        req.setAmount(new BigDecimal(amount));
        req.setCategory(category);
        req.setDate(LocalDate.parse(date));
        return req;
    }

    @Test
    void addExpense_returnsCreatedExpenseWithGeneratedId() {
        ExpenseRequest req = sampleRequest("Coffee", "4.50", "Food", "2026-07-01");

        ResponseEntity<Expense> response = restTemplate.postForEntity(baseUrl(), req, Expense.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isNotBlank();
        assertThat(response.getBody().getTitle()).isEqualTo("Coffee");
        assertThat(response.getBody().getAmount()).isEqualByComparingTo("4.50");
    }

    @Test
    void addExpense_withBlankTitle_returns400() {
        ExpenseRequest req = sampleRequest("", "4.50", "Food", "2026-07-01");

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), req, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void addExpense_withNegativeAmount_returns400() {
        ExpenseRequest req = sampleRequest("Refund", "-10", "Food", "2026-07-01");

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), req, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAllExpenses_returnsEverythingAdded() {
        restTemplate.postForEntity(baseUrl(), sampleRequest("Coffee", "4.50", "Food", "2026-07-01"), Expense.class);
        restTemplate.postForEntity(baseUrl(), sampleRequest("Bus ticket", "1.20", "Transport", "2026-07-02"), Expense.class);

        Expense[] all = restTemplate.getForObject(baseUrl(), Expense[].class);

        assertThat(all).hasSize(2);
    }

    @Test
    void filterByCategory_returnsOnlyMatchingExpenses() {
        restTemplate.postForEntity(baseUrl(), sampleRequest("Coffee", "4.50", "Food", "2026-07-01"), Expense.class);
        restTemplate.postForEntity(baseUrl(), sampleRequest("Lunch", "9.00", "Food", "2026-07-02"), Expense.class);
        restTemplate.postForEntity(baseUrl(), sampleRequest("Bus ticket", "1.20", "Transport", "2026-07-02"), Expense.class);

        Expense[] foodOnly = restTemplate.getForObject(baseUrl() + "?category=Food", Expense[].class);

        assertThat(foodOnly).hasSize(2);
        assertThat(List.of(foodOnly)).allMatch(e -> e.getCategory().equalsIgnoreCase("Food"));
    }

    @Test
    void total_withoutCategory_sumsAllExpenses() {
        restTemplate.postForEntity(baseUrl(), sampleRequest("Coffee", "4.50", "Food", "2026-07-01"), Expense.class);
        restTemplate.postForEntity(baseUrl(), sampleRequest("Bus ticket", "1.20", "Transport", "2026-07-02"), Expense.class);

        TotalResponse total = restTemplate.getForObject(baseUrl() + "/total", TotalResponse.class);

        assertThat(total.getTotal()).isEqualByComparingTo("5.70");
    }

    @Test
    void total_withCategory_sumsOnlyThatCategory() {
        restTemplate.postForEntity(baseUrl(), sampleRequest("Coffee", "4.50", "Food", "2026-07-01"), Expense.class);
        restTemplate.postForEntity(baseUrl(), sampleRequest("Lunch", "9.00", "Food", "2026-07-02"), Expense.class);
        restTemplate.postForEntity(baseUrl(), sampleRequest("Bus ticket", "1.20", "Transport", "2026-07-02"), Expense.class);

        TotalResponse total = restTemplate.getForObject(baseUrl() + "/total?category=Food", TotalResponse.class);

        assertThat(total.getTotal()).isEqualByComparingTo("13.50");
    }

    @Test
    void deleteExpense_removesIt() {
        Expense created = restTemplate.postForObject(baseUrl(), sampleRequest("Coffee", "4.50", "Food", "2026-07-01"), Expense.class);

        restTemplate.delete(baseUrl() + "/" + created.getId());

        Expense[] all = restTemplate.getForObject(baseUrl(), Expense[].class);
        assertThat(all).isEmpty();
    }

    @Test
    void deleteExpense_withUnknownId_returns404() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/does-not-exist",
                org.springframework.http.HttpMethod.DELETE,
                null,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
