package com.inv.walletCare.rest.tools;

import com.inv.walletCare.logic.entity.tools.exchange.CurrencyCodesDTO;
import com.inv.walletCare.logic.entity.tools.exchange.CurrencyExchangeDTO;
import com.inv.walletCare.logic.entity.tools.exchange.ExchangeService;
import com.inv.walletCare.logic.entity.tools.loans.LoanDTO;
import com.inv.walletCare.logic.entity.tools.loans.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequestMapping("/tools")
@RestController
public class ToolsRestController {
    @Autowired
    private LoanService loanService;

     @Autowired
     private ExchangeService exchangeService;


    /**
     * Handles the HTTP POST request for loan calculation.
     *
     * @param loanInformation the loan information provided in the request body
     * @return the calculated loan details
     */
    @PostMapping("/loan-calculator")
    public LoanDTO loanCalculator(@RequestBody LoanDTO loanInformation){
        return  loanService.loanCalculation(loanInformation);
    }

    /**
     * Handles the HTTP GET request to retrieve a list of currency codes.
     *
     * @return a list of currency codes
     * @throws IOException if an input or output exception occurs
     */
    @GetMapping("/currency-codes")
    public List<CurrencyCodesDTO> currencyCodes() throws IOException {
        return exchangeService.getCodes();
    }

    /**
     * Handles the HTTP POST request to perform a currency exchange.
     *
     * @param exchangeInformation the currency exchange information provided in the request body
     * @return the details of the currency exchange
     * @throws IOException if an input or output exception occurs
     */
    @PostMapping("/exchange")
    public CurrencyExchangeDTO curencyExchange(@RequestBody CurrencyExchangeDTO exchangeInformation) throws IOException {
         return exchangeService.getExchangeRate(exchangeInformation);
    }
}
