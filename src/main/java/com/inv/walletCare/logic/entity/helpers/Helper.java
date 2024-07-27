package com.inv.walletCare.logic.entity.helpers;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * This class is for various functionalities
 */
@Service
public class Helper {
    /**
     * This Method reverse the signs, makes the positives ammounts in negatives and the negatives in positives
     * @param ammount is the ammount that needs a revers
     * @return is the ammount with the reverse sign
     */
    public static BigDecimal reverse(BigDecimal ammount){
        double value = ammount.doubleValue();
        value *= -1;
        return new BigDecimal(value);
    }
}
