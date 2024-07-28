package com.inv.walletCare.rest.income;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.IncomeExpenceType;
import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.income.Income;
import com.inv.walletCare.logic.entity.income.IncomeRepository;
import com.inv.walletCare.logic.entity.tax.Tax;
import com.inv.walletCare.logic.entity.tax.TaxRepository;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/incomes")
public class IncomeRestController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TaxRepository taxRepository;

    @GetMapping
    public List<Income> getIncomes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return incomeRepository.findAllByUserId(currentUser.getId());
    }

    @PostMapping
    public Income addIncome(@Validated(OnCreate.class) @RequestBody Income income) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Validate that the income name is unique for the user
        Optional<Income> existingIncome = incomeRepository.findByNameAndOwnerId(income.getName(), currentUser.getId());
        if (existingIncome.isPresent()) {
            throw new FieldValidationException("name",
                    "El nombre del ingreso que ha ingresado ya está en uso. Por favor, ingresa uno diferente.");
        }

        // Validate that the account IDs are valid
        if (income.getIncomeAllocations() != null) {
            income.getIncomeAllocations().forEach(incomeAllocation -> {
                Optional<Account> account = accountRepository.findById(incomeAllocation.getAccount().getId());
                if (account.isEmpty()) {
                    throw new FieldValidationException("incomeAllocations",
                            "La cuenta con el ID " + incomeAllocation.getAccount().getId() + " no existe.");
                }

                // Validate that the account belongs to the current user
                if (account.get().getOwner().getId() != currentUser.getId()) {
                    throw new FieldValidationException("incomeAllocations",
                            "La cuenta con el ID " + incomeAllocation.getAccount().getId() +
                                    " no pertenece al usuario actual.");
                }
            });
        }

        if (income.isTaxRelated()) {
            // Validate that the tax IDs are valid
            Optional<Tax> tax = taxRepository.findById(income.getTax().getId());
            if (tax.isEmpty()) {
                throw new FieldValidationException("tax",
                        "El impuesto es requerido para los ingresos relacionados con impuestos.");
            }

            // Validate that the tax belongs to the current user
            if (tax.get().getOwner().getId() != currentUser.getId()) {
                throw new FieldValidationException("tax",
                        "El impuesto con el ID " + income.getTax().getId()
                                + " no existe o no pertenece al usuario actual.");
            }

            if (income.getFrequency() == null) {
                throw new FieldValidationException("frequency",
                        "La frecuencia es requerida para los ingresos relacionados con impuestos.");
            }

            if (income.getFrequency() == FrequencyTypeEnum.OTHER
                    && income.getScheduledDay() <= 1 || income.getScheduledDay() >= 31) {
                throw new FieldValidationException("scheduledDay",
                        "El día programado es requerido para los ingresos relacionados con impuestos.");
            }
        }

        Income newIncome = new Income();
        newIncome.setName(income.getName());
        newIncome.setAmount(income.getAmount());
        newIncome.setOwner(currentUser);
        newIncome.setTemplate(income.isTemplate());
        newIncome.setAmountType(income.getAmountType());
        newIncome.setFrequency(income.getFrequency());
        newIncome.setScheduledDay(income.getScheduledDay());
        newIncome.setTaxRelated(income.isTemplate());
        newIncome.setTax(income.getTax());
        newIncome.setCreatedAt(new Date());
        newIncome.setUpdatedAt(new Date());
        newIncome.setDeleted(false);
        newIncome.setType(income.getType());
        newIncome.setIncomeAllocations(income.getIncomeAllocations());
        return incomeRepository.save(newIncome);
    }

    @GetMapping("/{id}")
    public Income getIncomeById(@PathVariable Long id) {
        Optional<Income> income = incomeRepository.findById(id);
        if (income.isEmpty()) {
            throw new IllegalArgumentException("Ingreso no encontrado o no pertenece al usuario actual.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Check if the income belongs to the current user
        if (income.get().getOwner().getId() != currentUser.getId()) {
            throw new IllegalArgumentException("Ingreso no encontrado o no pertenece al usuario actual.");
        }

        return income.get();
    }
    @PutMapping ("/{id}")
    public Income updateIncome(@Validated(OnUpdate.class)@PathVariable Long id,@RequestBody Income income){
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        User currentUser=(User) authentication.getPrincipal();
        Optional<Income>existingIncome=incomeRepository.findByIdAndUserId(id,currentUser.getId());
        if (existingIncome.isEmpty()){
            throw new IllegalArgumentException("El ingreso no se encontró o no pertenece al usuario actual");
        }
        var existingIncomeName = incomeRepository.findByNameAndOwnerId(income.getName(), currentUser.getId());
        if (existingIncomeName.isPresent()&&existingIncome.get().getId()!=existingIncomeName.get().getId()){
            throw new FieldValidationException("name","El nombre de la cuenta que ha ingresado ya existe,porfavor utilice otro");
        }
        //if (existingIncome.get().getType() == IncomeExpenceType.RECURRENCE){}
        existingIncome.get().setUpdatedAt(new Date());
        existingIncome.get().setName(income.getName());
        existingIncome.get().setDescription(income.getDescription());
        existingIncome.get().setAmount(income.getAmount());
        existingIncome.get().setAmountType(income.getAmountType());
        existingIncome.get().setTax(income.getTax());
        existingIncome.get().setFrequency(income.getFrequency());
        return  incomeRepository.save(existingIncome.get());

    }


}
