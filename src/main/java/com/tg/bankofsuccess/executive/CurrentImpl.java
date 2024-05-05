package com.tg.bankofsuccess.executive;

import com.tg.bankofsuccess.models.Account;
import com.tg.bankofsuccess.models.CurrentAccount;
import com.tg.bankofsuccess.utilities.Constants;

public class CurrentImpl implements IAccount{

	
	public boolean open(Account account) {
		
		boolean isQualified = false;
		
		if(account.getAccountType().contains(Constants.CURRENT)) {
			CurrentAccount current = (CurrentAccount) account;
			if(current.getRegistrationNumber() != null) {
				isQualified = true;
			}
		}
		return isQualified;
	}

}