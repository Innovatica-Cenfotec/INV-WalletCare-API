package com.inv.walletCare.logic.entity.report;

/**
 * Object to transfer pie chart report details to report service.
 */
public class PiechartDTO {
    private String category;
    private Long data;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }
}
