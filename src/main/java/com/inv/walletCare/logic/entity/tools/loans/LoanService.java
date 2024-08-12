package com.inv.walletCare.logic.entity.tools.loans;

import com.inv.walletCare.logic.entity.helpers.Helper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class LoanService {
    public LoanDTO  loanCalculation(LoanDTO loanInformation) {
        var negativePaymentDeadline = Helper.reverse(BigDecimal.valueOf(loanInformation.getPaymentDeadline().longValue())).longValue();
        var interestRate = interestRatePercentCalculation(loanInformation.getInterestRate()).doubleValue();

        var fee = (loanInformation.getAmmount().doubleValue() / ((1 - ((Math.pow(1 + interestRate, negativePaymentDeadline)))) / interestRate));
        loanInformation.setFee(BigDecimal.valueOf(fee).setScale(2, RoundingMode.UP));
        return loanInformation;
    }

    private BigDecimal interestRatePercentCalculation(BigDecimal interestrate) {
        var intRate = interestrate.doubleValue() / 100;
        return BigDecimal.valueOf(intRate);
    }
}
