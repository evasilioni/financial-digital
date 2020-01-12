package com.silionie.parser.financialdigital.account.controller;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.silionie.parser.financialdigital.account.entities.Account;
import com.silionie.parser.financialdigital.account.service.AccountService;

import javax.websocket.server.PathParam;

@RestController
@RequestMapping(value = "/account")
public class AccountController {

	private AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@RequestMapping(
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public @ResponseBody ResponseEntity<List<Account>> getAccounts(Sort sort,
																   @RequestParam(value = "filter", required = false) String filter,
																   @RequestParam(value = "value", required = false) String value){
		List<Account> accounts = accountService.get(sort);
		if(filter != null && value != null){
			accounts = accountService.getByFilter(filter, value);
		}
		return ResponseEntity.status(HttpStatus.OK).body(accounts);
	}


	@RequestMapping(
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public @ResponseBody ResponseEntity<Account> saveAccount(@RequestParam("balance") BigDecimal balance,
															 @RequestParam("currency") String currency){
		Account persistedAccount = accountService.add(balance,currency);
		return ResponseEntity.status(HttpStatus.OK).body(persistedAccount);
	}

	@RequestMapping(
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public @ResponseBody ResponseEntity<String> updateAccount(@RequestBody Account account){
		try{
			Account updatedAccount = accountService.update(account);
			return ResponseEntity.status(HttpStatus.OK).body(new ObjectMapper().writeValueAsString(updatedAccount));
		}catch (Exception ex){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}

	@RequestMapping(
			value = "/{id}",
			method = RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public @ResponseBody ResponseEntity<String> deleteAccount(@PathVariable Long id){
		try{
			Account deletedAccount = accountService.remove(id);
			return ResponseEntity.status(HttpStatus.OK).body("Deleted Account: " + new ObjectMapper().writeValueAsString(deletedAccount));
		}catch (Exception ex){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}
}
