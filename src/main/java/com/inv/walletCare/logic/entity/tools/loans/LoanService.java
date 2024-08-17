package com.inv.walletCare.logic.entity.tools.loans;

import com.inv.walletCare.logic.entity.helpers.Helper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class handles the loans calculations
 */
@Service
public class LoanService {

    /**
     * Calculates the loan details including the fee based on the provided loan information.
     *
     * @param loanInformation the loan information containing amount, interest rate, and payment deadline
     * @return the updated loan information with the calculated fee
     */
    public LoanDTO  loanCalculation(LoanDTO loanInformation) {
        var negativePaymentDeadline = Helper.reverse(BigDecimal.valueOf(loanInformation.getPaymentDeadline().longValue())).longValue();
        var interestRate = interestRatePercentCalculation(loanInformation.getInterestRate()).doubleValue();

        var fee = (loanInformation.getAmount().doubleValue() / ((1 - ((Math.pow(1 + interestRate, negativePaymentDeadline)))) / interestRate));
        loanInformation.setFee(BigDecimal.valueOf(fee).setScale(2, RoundingMode.UP));
        return loanInformation;
    }

    /**
     * Calculates the interest rate as a percentage.
     *
     * @param interestrate the interest rate as a BigDecimal
     * @return the interest rate as a percentage in BigDecimal format
     */
    private BigDecimal interestRatePercentCalculation(BigDecimal interestrate) {
        var intRate = interestrate.doubleValue() / 100;
        return BigDecimal.valueOf(intRate);
    }
}
