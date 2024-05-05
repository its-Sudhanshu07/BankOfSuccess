package com.tg.bankofsuccess;

import com.tg.bankofsuccess.executive.AccountController;
import com.tg.bankofsuccess.utilities.Constants;
import com.tg.bankofsuccess.utilities.LogHandlerV1;

public class BankOfSuccess { 
    // Main class for the Bank of Success application
    
    private static final LogHandlerV1 log = new LogHandlerV1(); 
    // Logger instance for handling application logging
    
    public static void main(String[] args) { 
        
        log.log(Constants.INFO, "Log Initiated - main method"); 
        // Logs an informational message at the beginning of the method
        
        System.out.println("Welcome to Bank Of Success"); 
        
        AccountController controller = new AccountController(); 
        // Creates an instance of AccountController to manage accounts
        
        try {
            controller.start(); 
            // Starts the account controller to initiate the application workflow
            
            System.out.println("Thank you visit again!"); 
            // Thanks the user after the process completes
            
        } catch (Exception e) { 
            // Handles any exceptions thrown during the process
            
            log.log(Constants.WARNING, "Main method - start method - " + e.getMessage()); 
            // Logs a warning with details of the exception
        }
    }

}

