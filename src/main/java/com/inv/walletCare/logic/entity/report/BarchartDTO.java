package com.inv.walletCare.logic.entity.report;

import java.util.List;

/**
 * Object to transfer expense report details to report service.
 */
public class BarchartDTO {
    private String category;
    private List<BarchartItemDTO> data;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<BarchartItemDTO> getData() {
        return data;
    }

    public void setData(List<BarchartItemDTO> data) {
        this.data = data;
    }
}