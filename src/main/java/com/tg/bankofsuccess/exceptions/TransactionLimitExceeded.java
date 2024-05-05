package com.tg.bankofsuccess.exceptions;

public class TransactionLimitExceeded extends Exception{
	public TransactionLimitExceeded(String message) {
		super(message);
	}
}
