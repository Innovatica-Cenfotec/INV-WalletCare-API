package com.inv.walletCare.logic.entity.tools.loans;

import java.math.BigDecimal;

public class LoanDTO {
    private CurrencyTypeEnum currency;
    private BigDecimal amount;
    private Long paymentDeadline;
    private BigDecimal interestRate;
    private BigDecimal fee;

    public LoanDTO() {
    }

    public LoanDTO(CurrencyTypeEnum currency, BigDecimal amount, Long paymentDeadline, BigDecimal interestRate, BigDecimal fee) {
        this.currency = currency;
        this.amount = amount;
        this.paymentDeadline = paymentDeadline;
        this.interestRate = interestRate;
        this.fee = fee;
    }

    public CurrencyTypeEnum getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyTypeEnum currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getPaymentDeadline() {
        return paymentDeadline;
    }

    public void setPaymentDeadline(Long paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
}
