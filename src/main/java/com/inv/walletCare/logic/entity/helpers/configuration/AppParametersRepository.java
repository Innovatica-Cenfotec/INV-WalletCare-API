package com.inv.walletCare.logic.entity.helpers.configuration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppParametersRepository extends JpaRepository<AppParameters, Long> {
    @Query("SELECT u FROM AppParameters u WHERE u.paramKey = ?1")
    Optional<AppParameters> findByParamKey(String key);
}
