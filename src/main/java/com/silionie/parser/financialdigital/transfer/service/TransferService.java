package com.silionie.parser.financialdigital.transfer.service;

import com.silionie.parser.financialdigital.account.repository.AccountRepository;
import com.silionie.parser.financialdigital.transfer.entities.Transfer;
import com.silionie.parser.financialdigital.transfer.repository.TransferRepository;
import com.silionie.parser.financialdigital.transfer.utility.TransferUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransferService {

	private TransferRepository transferRepository;
	private TransferUtils transferUtils;

	public TransferService(TransferRepository transferRepository,
						   TransferUtils transferUtils) {
		this.transferRepository = transferRepository;
		this.transferUtils = transferUtils;
	}

	public List<Transfer> get(Sort sort){
		boolean sorted = sort.get().anyMatch(s -> s.getProperty().equals("source") ||
				s.getProperty().equals("destination") || s.getProperty().equals("transferDate"));
		if(sorted){
			return transferUtils.transfersOfNotDeletedAccounts(transferRepository.findAll(sort));
		}

		return transferUtils.transfersOfNotDeletedAccounts(transferRepository.findAll());
	}

	public List<Transfer> getByFilter(String filter, String value) {
		switch (filter) {
			case "source":
				return transferRepository.findBySource(Long.valueOf(value));
			case "destination":
				return transferRepository.findByDestination(Long.valueOf(value));
			case "transferDate":
				return transferRepository.findByTransferDateBetween(transferUtils.getDateTime(value, 0, 0, 1),
						transferUtils.getDateTime(value, 23, 59, 59));
			default:
				return transferUtils.transfersOfNotDeletedAccounts(transferRepository.findAll());

		}
	}

	public Transfer add(Transfer transfer) throws Exception {
		String sourceCurrency = transferUtils.getCurrencyByAccountId(transfer.getSource());
		String destinationCurrency = transferUtils.getCurrencyByAccountId(transfer.getDestination());

		if(transferUtils.isAccountActive(transfer.getSource()) &&
				transferUtils.isAccountActive(transfer.getDestination())){
			if(!sourceCurrency.equals(destinationCurrency)){
				BigDecimal rate = transferUtils.getRateOverDifferentCurrencies(sourceCurrency, destinationCurrency);
				transfer.setAmount(transfer.getAmount().divide(rate, 2, RoundingMode.HALF_UP));
			}
			if(transfer.getTransferDate() == null){
				transfer.setTransferDate(LocalDateTime.now());
			}
			return transferRepository.save(transfer);
		}else {
			throw new Exception("Transfers can only be made between two active Accounts.");
		}
	}

}
