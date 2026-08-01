package com.diligent.expensetracker.model;

import java.math.BigDecimal;

public class TotalResponse {

    private String category;
    private BigDecimal total;

    public TotalResponse() {
    }

    public TotalResponse(String category, BigDecimal total) {
        this.category = category;
        this.total = total;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
