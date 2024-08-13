package com.inv.walletCare.logic.entity.tools.exchange;

/**
 * This class is a DTO for currency exchange Information
 */
public class CurrencyExchangeDTO {
    /**
     * The currency code from which the exchange is made.
     */
    private String currencyFrom;

    /**
     * The currency code to which the exchange is made.
     */
    private String currencyTo;

    /**
     * The amount of currency to be exchanged.
     */
    private double amount;

    /**
     * The value of the exchange.
     */
    private double exchangeValue;


    public CurrencyExchangeDTO() {
    }

    public CurrencyExchangeDTO(String currencyFrom, String currencyTo, double amount, double exchangeValue) {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.amount = amount;
        this.exchangeValue = exchangeValue;
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public void setCurrencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public void setCurrencyTo(String currencyTo) {
        this.currencyTo = currencyTo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getExchangeValue() {
        return exchangeValue;
    }

    public void setExchangeValue(double exchangeValue) {
        this.exchangeValue = exchangeValue;
    }
}
