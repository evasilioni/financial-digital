package com.silionie.parser.financialdigital.transfer.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.silionie.parser.financialdigital.transfer.entities.Transfer;
import com.silionie.parser.financialdigital.transfer.service.TransferService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/transfer")
public class TransferController {

	private TransferService transferService;

	public TransferController(TransferService accountService) {
		this.transferService = accountService;
	}

	@RequestMapping(
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public @ResponseBody ResponseEntity<List<Transfer>> getTransfers(Sort sort,
																	 @RequestParam(value = "filter", required = false) String filter,
																	 @RequestParam(value = "value", required = false) String value){
		List<Transfer> transfers = transferService.get(sort);
		if(filter != null && value != null){
			transfers = transferService.getByFilter(filter, value);
		}
		return ResponseEntity.status(HttpStatus.OK).body(transfers);
	}

	@RequestMapping(
			method = RequestMethod.POST,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public @ResponseBody ResponseEntity<String> saveTransfer(@RequestBody Transfer transfer){
		try {
			Transfer persisted  = transferService.add(transfer);
			return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writeValueAsString(persisted));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}
}
