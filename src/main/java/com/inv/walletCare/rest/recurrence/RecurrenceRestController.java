package com.inv.walletCare.rest.recurrence;

import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.recurrence.Recurrence;
import com.inv.walletCare.logic.entity.recurrence.RecurrenceRepository;
import com.inv.walletCare.logic.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recurrences")
public class RecurrenceRestController {

    @Autowired
    private RecurrenceRepository recurrenceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/{id}")
    public List<Recurrence> getRecurrences(@PathVariable Long id) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        var account = accountRepository.findById(id);
        if(account.isEmpty()){
            throw new Exception("La cuenta indicada no existe, favor intenta con otra.");
        }
        return recurrenceRepository.findAllByOwnerAndAccountId(currentUser.getId(), account.get().getId());
    }

    @DeleteMapping("/{id}")
    public void deleteRecurrence(@PathVariable Long id) {
        Optional<Recurrence> recurrence = recurrenceRepository.findById(id);
        if (recurrence.isEmpty()) {
            throw new IllegalArgumentException("El id no existe");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if(!recurrence.get().getOwner().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("No puedes borrar una recurrencia que no te pertenece");
        }

        recurrenceRepository.delete(recurrence.get());
    }
}
