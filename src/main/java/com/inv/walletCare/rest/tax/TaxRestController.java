package com.inv.walletCare.rest.tax;

import com.inv.walletCare.logic.entity.tax.Tax;
import com.inv.walletCare.logic.entity.tax.TaxRepository;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    /***
     * Get a tax by id
     * @param id Tax id
     * @return Tax
     */
    @GetMapping("/{id}")
    public Tax getTax(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Optional<Tax> tax = taxRepository.findByIdAndUserId(id, user.getId());
        if (tax.isEmpty()) {
            throw new FieldValidationException("id", "El impuesto no existe o no pertenece al usuario");
        }

        return tax.get();
    }


    @PostMapping
    public Tax createTax(@RequestBody Tax tax) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Optional<Tax> existingTax = taxRepository.findByNameAndOwnerId(tax.getName(), user.getId());
        if (existingTax.isPresent()) {
            throw new FieldValidationException("name", "El impuesto ya existe, por favor elija otro nombre");
        }

        Tax newTax = new Tax();
        newTax.setName(tax.getName());
        newTax.setDescription(tax.getDescription());
        newTax.setPercentage(tax.getPercentage());
        newTax.setDeleted(false);
        newTax.setOwner(user);
        newTax.setCreatedAt(new Date());
        newTax.setUpdatedAt(new Date());
        return taxRepository.save(newTax);
    }


    /***
     * Create a new tax
     * @param id Tax id
     */
    @DeleteMapping("/{id}")
    public void deleteTax(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Optional<Tax> tax = taxRepository.findByIdAndUserId(id, user.getId());
        if (tax.isEmpty()) {
            throw new FieldValidationException("id", "El impuesto no existe o no pertenece al usuario");
        }

        tax.get().setUpdatedAt(new Date());
        tax.get().setDeletedAt(new Date());
        tax.get().setDeleted(true);
        taxRepository.save(tax.get());
    }

    /***
     * Update a tax
     * @param id Tax id
     * @param tax Tax object
     * @return Updated tax
     */
    @PutMapping("/{id}")
    public Tax updateTax(@PathVariable Long id, @RequestBody Tax tax) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Optional<Tax> existingTax = taxRepository.findByNameAndOwnerId(tax.getName(), user.getId());
        if (existingTax.isPresent() && existingTax.get().getId() != id) {
            throw new FieldValidationException("id", "El impuesto no existe o no pertenece al usuario");
        }

        tax.setPercentage(existingTax.get().getPercentage());
        tax.setName(existingTax.get().getName());
        tax.setDescription(existingTax.get().getDescription());
        tax.setUpdatedAt(new Date());
        return taxRepository.save(tax);
    }
}