package com.tg.bankofsuccess.executive;

import com.tg.bankofsuccess.models.Account;
import com.tg.bankofsuccess.models.CurrentAccount;
import com.tg.bankofsuccess.models.SavingsAccount;
import com.tg.bankofsuccess.utilities.Constants;

public class AccountFactory {
	
    public static Account create(String accountType){
    	
        //Here will create either savings or current account object and return 
       if(accountType.contains(Constants.SAVINGS)) {
    	   return new SavingsAccount();
       }else {
    	   return new CurrentAccount();
       }
    }
}