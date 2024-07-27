package com.inv.walletCare.rest.tax;

import com.inv.walletCare.logic.entity.tax.Tax;
import com.inv.walletCare.logic.entity.tax.TaxRepository;
import com.inv.walletCare.logic.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/taxes")
public class TaxRestController {

    @Autowired
    private TaxRepository taxRepository;

    /**
     * Get all the taxes for the current user
     * @return List of taxes
     */
    @GetMapping
    public List<Tax> getTaxes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        return taxRepository.findAllByUserId(currentUser.getId());
    }
}