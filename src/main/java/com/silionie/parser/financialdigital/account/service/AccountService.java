package com.silionie.parser.financialdigital.account.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.silionie.parser.financialdigital.utility.CurrencyTypes;
import org.apache.commons.collections.FunctorException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.silionie.parser.financialdigital.account.entities.Account;
import com.silionie.parser.financialdigital.account.repository.AccountRepository;

@Service
public class AccountService {

    private AccountRepository accountRepository;
    private static Random rnd = new Random();

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public List<Account> get(Sort sort) {
        boolean sorted = sort.get().anyMatch(s -> s.getProperty().equals("balance") || s.getProperty().equals("iban") || s.getProperty().equals("currency"));
        if(sorted){
            return accountRepository.findByActiveTrue(sort);
        }
        return accountRepository.findByActiveTrue();
    }

    public List<Account> getByFilter(String filter, String value) {
        switch (filter) {
            case "iban":
                return accountRepository.findByIban(value);
            case "balance":
                return accountRepository.findByBalance(BigDecimal.valueOf(Double.valueOf(value)));
            case "currency":
                return accountRepository.findByCurrency(value);
            default:
                return accountRepository.findAll();

        }
    }

    public Account add(BigDecimal balance, String currency) {
        Account account = new Account();
        String card = "BE";
        account.setIban(card + getRandomNumber(22));
        account.setBalance(balance);
        account.setCurrency(CurrencyTypes.valueOf(currency).getCode());
        account.setOpenDate(LocalDateTime.now());
        account.setActive(true);
        return accountRepository.save(account);
    }

    public Account add(Account account) {
        String card = "BE";
        account.setIban(card + getRandomNumber(22));
        account.setOpenDate(LocalDateTime.now());
        account.setActive(true);
        return accountRepository.save(account);
    }

    public Account update(Account account) throws Exception {
        Optional<Account> toBeUpdated = accountRepository.findById(account.getId());
        if (toBeUpdated.isPresent()) {
            toBeUpdated.get().setBalance(account.getBalance());
            toBeUpdated.get().setCurrency(account.getCurrency());
            toBeUpdated.get().setActive(account.isActive());
            return accountRepository.save(toBeUpdated.get());
        } else {
            throw new Exception("No accounts found for update");
        }
    }

    public Account remove(Long id) throws Exception {
        Optional<Account> toBeDeleted = accountRepository.findById(id);
        if (toBeDeleted.isPresent()) {
            toBeDeleted.get().setActive(false);
            return accountRepository.save(toBeDeleted.get());
        } else {
            throw new Exception("No accounts found for deletion");
        }
    }

    static String getRandomNumber(int digCount) {
        StringBuilder sb = new StringBuilder(digCount);
        for (int i = 0; i < digCount; i++)
            sb.append((char) ('0' + rnd.nextInt(10)));
        return sb.toString();
    }
}
