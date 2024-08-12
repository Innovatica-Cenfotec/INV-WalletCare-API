package com.inv.walletCare.rest.tools;

import com.inv.walletCare.logic.entity.tools.loans.LoanDTO;
import com.inv.walletCare.logic.entity.tools.loans.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/tools")
@RestController
public class ToolsRestController {
    @Autowired
    private LoanService loanService;
    @PostMapping("/loan-calculator")
    public LoanDTO loanCalculator(@RequestBody LoanDTO loanInformation){
        return  loanService.loanCalculation(loanInformation);
    }
}
