package com.inv.walletCare.logic.entity.tools.exchange;


/**
 * This class is a DTO for currency codes
 */
public class CurrencyCodesDTO {

    /**
     * The code of the currency.
     */
    private String currencyCode;

    /**
     * The name of the currency.
     */
    private String currencyName;

    public CurrencyCodesDTO() {
    }

    public CurrencyCodesDTO(String currencyCode, String currencyName) {
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }
}
