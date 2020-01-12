package com.silionie.parser.financialdigital.utility;

public enum CurrencyTypes {
	EUR("EUR"),
	GBP("GBP"),
	USD("USD");

	private final String code;

	CurrencyTypes(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
}
