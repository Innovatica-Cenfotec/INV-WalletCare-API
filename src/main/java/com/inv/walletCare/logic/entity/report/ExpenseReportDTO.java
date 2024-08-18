package com.inv.walletCare.logic.entity.report;

import java.util.List;

/**
 * Object to transfer expense report details to report service.
 */
public class ExpenseReportDTO {
    private String category;
    private List<Double> amount;
}