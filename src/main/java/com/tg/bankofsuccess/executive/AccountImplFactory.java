package com.tg.bankofsuccess.executive;

import com.tg.bankofsuccess.models.Account;
import com.tg.bankofsuccess.models.CurrentAccount;
import com.tg.bankofsuccess.models.SavingsAccount;

public class AccountImplFactory { 
    // Factory class for creating implementations of IAccount based on account type
    
    public static IAccount create(Account account) { 
        
        if (account instanceof CurrentAccount) { 
            // If the account is a current account
            return new CurrentImpl(); 
            // Return the implementation for current accounts
        } else if (account instanceof SavingsAccount) { 
            // If the account is a savings account
            return new SavingsImpl(); 
            // Return the implementation for savings accounts
        } else { 
            // If the account is of an unsupported type
            throw new IllegalArgumentException("Unsupported account type"); 
            // Throws an exception indicating an unsupported account type
        }
    }

}

