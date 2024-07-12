package com.inv.walletCare.rest.salary;

import com.inv.walletCare.logic.entity.auth.encryption.EncryptionService;
import com.inv.walletCare.logic.entity.salary.Salary;
import com.inv.walletCare.logic.entity.salary.SalaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/salary")
public class SalaryController {
    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private SalaryRepository salaryRepository;

    @PostMapping("/register")
    public String registerSalary(@RequestBody Salary salary) {
        try {
            String encryptedSalary = encryptionService.encrypt(salary.getAmount());
            salary.setAmount(encryptedSalary);
            salaryRepository.save(salary);
            return "Salary registered successfully";
        } catch (Exception e) {
            throw new RuntimeException("Error while encrypting salary", e);
        }
    }

    @GetMapping("/{id}")
    public Salary getSalaryById(@PathVariable Long id) {
        Salary salary = salaryRepository.findById(id).orElseThrow (() -> new RuntimeException("Salary not found"));
        try {
            String decryptedSalary = encryptionService.decrypt(salary.getAmount());
            salary.setAmount(decryptedSalary);
            return salary;
        } catch (Exception e) {
            throw new RuntimeException("Error while decrypting salary", e);
        }
    }
}
