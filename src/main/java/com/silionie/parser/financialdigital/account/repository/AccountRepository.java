package com.silionie.parser.financialdigital.account.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.silionie.parser.financialdigital.account.entities.Account;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByActiveTrue(Sort sort);

    List<Account> findByActiveTrue();

    List<Account> findByIban(String iban);

    List<Account> findByBalance(BigDecimal balance);

    List<Account> findByCurrency(String currency);
}
