package com.silionie.parser.financialdigital;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.silionie.parser.financialdigital.account.entities.Account;
import com.silionie.parser.financialdigital.account.service.AccountService;
import com.silionie.parser.financialdigital.transfer.entities.Transfer;
import com.silionie.parser.financialdigital.transfer.service.TransferService;
import com.silionie.parser.financialdigital.utility.CurrencyTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private AccountService accountService;
    private TransferService transferService;
    private static final Logger LOG = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(AccountService accountService, TransferService transferService) {
        this.accountService = accountService;
        this.transferService = transferService;
    }

    @Override
    public void run(String... args) throws IOException {
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .withIgnoreQuotations(true)
                .build();
        processAccounts(parser);
        processTransfers(parser);
    }

    public void processAccounts(CSVParser parser) throws IOException {
        InputStream accountsStream = getClass().getClassLoader().getResourceAsStream("samplefiles/Accounts.csv");

        assert accountsStream != null;
        Reader reader = new InputStreamReader(accountsStream);
        try {
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();
            List<String[]> accountList = csvReader.readAll();

            saveAccounts(accountList);

            reader.close();
            csvReader.close();
        } catch (IOException e) {
            LOG.info("" + e);
        } finally {
            reader.close();
        }
    }

    public void processTransfers(CSVParser parser) throws IOException {
        InputStream transfersStream = getClass().getClassLoader().getResourceAsStream("samplefiles/Transfers.csv");

        assert transfersStream != null;
        Reader reader = new InputStreamReader(transfersStream);
        try {
            CSVReader csvReader = new CSVReaderBuilder(reader)
                    .withSkipLines(1)
                    .withCSVParser(parser)
                    .build();
            List<String[]> transferList = csvReader.readAll();

            saveTransfers(transferList);

            reader.close();
            csvReader.close();
        } catch (IOException e) {
            LOG.info("" + e);
        } finally {
            reader.close();
        }
    }

    private void saveAccounts(List<String[]> accountList){
        accountList.forEach(a -> {
            Account account = new Account();
            account.setId(Long.valueOf(a[0]));
            account.setBalance(BigDecimal.valueOf(Double.parseDouble(a[1])));
            account.setCurrency(CurrencyTypes.valueOf(a[2]).getCode());
            accountService.add(account);
        });
    }

    private void saveTransfers(List<String[]> transferList){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        transferList.forEach(a -> {
            Transfer transfer = new Transfer();
            transfer.setSource(Long.valueOf(a[0]));
            transfer.setDestination(Long.valueOf(a[1]));
            transfer.setAmount(BigDecimal.valueOf(Double.parseDouble(a[2])));
            transfer.setDescription(a[3]);
            transfer.setTransferDate(LocalDateTime.parse(a[4], formatter));
            try {
                transferService.add(transfer);
            } catch (Exception e) {
                LOG.info("" + e);
            }
        });
    }
}