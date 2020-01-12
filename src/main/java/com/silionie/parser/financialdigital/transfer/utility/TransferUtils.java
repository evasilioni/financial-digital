package com.silionie.parser.financialdigital.transfer.utility;

import com.google.gson.Gson;
import com.silionie.parser.financialdigital.account.entities.Account;
import com.silionie.parser.financialdigital.account.repository.AccountRepository;
import com.silionie.parser.financialdigital.transfer.entities.Transfer;
import com.silionie.parser.financialdigital.utility.CurrencyTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TransferUtils {

    private AccountRepository accountRepository;
    private RestTemplate restTemplate;

    @Value("${local.properties.exchangeUri}")
    private String exchangeUri;

    public TransferUtils(AccountRepository accountRepository, RestTemplate restTemplate) {
        this.accountRepository = accountRepository;
        this.restTemplate = restTemplate;
    }

    public LocalDateTime getDateTime(String value, int hour, int minute, int second){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        return LocalDateTime.of(LocalDate.parse(value, formatter).getYear(), LocalDate.parse(value, formatter).getMonth(), LocalDate.parse(value, formatter).getDayOfMonth(), hour,  minute, second);
    }

    public boolean isAccountActive(Long accountId){
        List<Long> activeIds = accountRepository.findByActiveTrue().stream().map(Account::getId).collect(Collectors.toList());
        return activeIds.stream().anyMatch(a -> a.equals(accountId));
    }

    public BigDecimal getRateOverDifferentCurrencies(String sourceCurrency, String destinationCurrency){
        String base = CurrencyTypes.valueOf(destinationCurrency).getCode();
        String symbols = CurrencyTypes.valueOf(sourceCurrency).getCode();
        String jsonResponse = restTemplate.getForObject(exchangeUri, String.class, base, symbols);

        Map jsonJavaRootObject = new Gson().fromJson(jsonResponse, Map.class);
        Map<String, Double> rates = (Map)jsonJavaRootObject.get("rates");
        return BigDecimal.valueOf(rates.get(symbols));
    }

    public List<Transfer> transfersOfNotDeletedAccounts(List<Transfer> persistedTransfers){
        List<Transfer> transfers = new ArrayList<>();
        persistedTransfers.forEach(t -> {
            if(isAccountActive(t.getSource()) && isAccountActive(t.getDestination())){
                transfers.add(t);
            }
        });

        return transfers;
    }

    public String getCurrencyByAccountId(Long id){
        return accountRepository
                .findById(id).get().getCurrency();
    }
}
