package com.inv.walletCare.logic.entity.report;

import java.math.BigDecimal;

/**
 * Object to transfer expense report details to report service.
 */
public class BarchartItemDTO {
    private String month;
    private BigDecimal amount;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
