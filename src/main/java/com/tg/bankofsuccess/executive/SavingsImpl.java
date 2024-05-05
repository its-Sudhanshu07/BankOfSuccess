package com.tg.bankofsuccess.executive;

import com.tg.bankofsuccess.models.Account;
import com.tg.bankofsuccess.models.SavingsAccount;
import com.tg.bankofsuccess.utilities.Constants;

public class SavingsImpl implements IAccount{

	public boolean open(Account account) {

		SavingsAccount saving = (SavingsAccount) account;

		boolean isQualified = false;

		if (account.getAccountType().contains(Constants.SAVINGS)) {
			if (saving.getAge() >= 18) {
				isQualified = true;
			}
		}
		return isQualified;
	}

}
