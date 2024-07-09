package com.inv.walletCare.logic.entity.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmailTemplateRepository extends CrudRepository<EmailTemplate, Integer> {
    Optional<EmailTemplate> findByName(String name);
}
