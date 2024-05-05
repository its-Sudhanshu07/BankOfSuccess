package com.tg.bankofsuccess.executive;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.tg.bankofsuccess.exceptions.AccountNotCreatedException;
import com.tg.bankofsuccess.models.Account;
import com.tg.bankofsuccess.models.CurrentAccount;
import com.tg.bankofsuccess.models.SavingsAccount;
import com.tg.bankofsuccess.utilities.Constants;
import com.tg.bankofsuccess.utilities.LogHandlerV1;

//Controller class for managing accounts and their operations
public class AccountController {

	// Log handler instance for logging events
	private static final LogHandlerV1 log = new LogHandlerV1();

	// Manager instance for managing accounts
	AccountManager manager = new AccountManager();
	Scanner sc = new Scanner(System.in);

	// Start the account management system
	public void start() throws AccountNotCreatedException {
		
		Account account1 = null, account2 = null;
		int choice = 0;
		
		while (true) {
			
			// Display menu options
			System.out.println("Choose an option:\r\n" + "1. Open Account\r\n" + "2. Deposit Funds\r\n"
					+ "3. Withdraw Funds\r\n" + "4. Transfer Funds\r\n" + "5. Close Account\r\n" + "6. Exit");
			
			choice = Integer.parseInt(sc.nextLine());
			
			switch (choice) {
			case 1:
				
				// Open new accounts
				account1 = open(account1);
				account2 = open(account2);
				break;

			case 2:
				
				// Deposit funds into an account
				if (account1 != null) {
					initiateDeposit(account1);
				} else {
					throw new AccountNotCreatedException("No account created yet");
				}
				break;

			case 3:
				
				// Withdraw funds from an account
				if (account1 != null) {
					initiateTransaction(account1);
				} else {
					throw new AccountNotCreatedException("No account created yet");
				}
				break;

			case 4:
				
				// Transfer funds between accounts
				if (account1 != null && account2 != null) {
					initiateTransfer(account1, account2);
				} else {
					throw new AccountNotCreatedException("No account created yet");
				}
				break;
				
			case 5:
				
				// Close an account
				if (account1 != null) {
					close(account1);
				} else {
					throw new AccountNotCreatedException("No account created yet");
				}
				break;

			case 6:
				System.out.println("Exiting Bank app...");
				return;

			default:
				log.log(Constants.ERROR, "Wrong Input! Try again");
				// Log an error for invalid input
				break;
			}
		}
	}

	// Open a new account
	public Account open(Account account) {

		String accountType = acceptUserChoice();
		account = createAccount(accountType);
		boolean status = manager.open(account);

		// Check if the account was successfully opened
		if (status) {
			log.log(Constants.INFO,
					"Congratulations! " + account.getName()
							+ " your account has been created successfully with account number "
							+ account.getAccountNumber() + " and with user ID " + account.getId() + " on "
							+ account.getDate() + " and your account has been activated.");
		} else {
			log.log(Constants.INFO,
					"Your request has been denied from manager please ensure you are above 18 and recheck your phone number.");
		}
		return account;
	}

	// Initiate a fund transfer between accounts
	private void initiateTransfer(Account sender, Account receiver) {
		
		// Transfers funds from one account to another
		System.out.println("Enter the account number of receiver");
		String receiverAccountNumber = sc.nextLine();
		
		System.out.println("Enter the amount to transfer");
		double amountToTransfered = Double.parseDouble(sc.nextLine());
		
		System.out.println("Enter your PIN number");
		int pinNumber = Integer.parseInt(sc.nextLine());
		
		if (receiver.getAccountNumber().contentEquals(receiverAccountNumber)) {
			try {
				if (manager.transferFunds(sender, receiver, amountToTransfered, pinNumber)) {
					log.log(Constants.INFO, "Transaction successful! Rs " + amountToTransfered + " has been sent to "
							+ receiver.getName() + ". Updated Balance is Rs " + sender.getAccountBalance());
				}
			} catch (Exception e) {
				log.log(Constants.WARNING,
						getClass().getSimpleName() + " " + "- initiateTransfer method - " + " " + e.getMessage());
				System.out.println("Try again!");
			}
		} else {
			log.log(Constants.INFO, "There is no account associated with account number " + receiverAccountNumber
					+ " Please try again!");
		}
	}

	// Initiate a deposit into an account
	private void initiateDeposit(Account account) {
		
		System.out.println("Enter your account number");
		String accountNumber = sc.nextLine();
		
		System.out.println("Enter the amount to deposit");
		double amount = Double.parseDouble(sc.nextLine());
		
		System.out.println("Enter the PIN of your account");
		int pin = Integer.parseInt(sc.nextLine());
		
		try {
			if (manager.isDeposited(account, accountNumber, amount, pin)) {
				log.log(Constants.INFO, "Deposit Completed. Your Account Balance is now " + account.getAccountBalance());
			}
		} catch (Exception e) {
			log.log(Constants.WARNING,
					getClass().getSimpleName() + " " + "- initiateDeposit method - " + " " + e.getMessage());
		}
	}

	// Initiate a transaction (withdrawal) from an account
	private void initiateTransaction(Account account) {
		
		System.out.println("Enter your account number");
		String accountNumber = sc.nextLine();
		
		System.out.println("Enter the amount to withdraw");
		double amount = Double.parseDouble(sc.nextLine());
		
		System.out.println("Enter the PIN of your account");
		int pin = Integer.parseInt(sc.nextLine());
		
		try {
			if (manager.isWithdrawn(account, accountNumber, amount, pin)) {
				log.log(Constants.INFO, "Transaction Completed. Your Account Balance is now " + account.getAccountBalance());
			}
		} catch (Exception e) {
			log.log(Constants.WARNING,
					getClass().getSimpleName() + " " + "- initiateTransaction method - " + " " + e.getMessage());
		}
	}

	// Accept the user's choice of account type
	private String acceptUserChoice() {
		
		System.out.println("Which account you want to open(Savings/Current)");
		String choice = sc.nextLine();
		choice = choice.toLowerCase();
		return choice;
	}

	// Create an account based on the account type should be implemented in accountfactory
	private Account createAccount(String accountType) {
		
		// Creates a new account using the AccountFactory
		Account account = AccountFactory.create(accountType);
		getCommonAccountInformarion(account);
		
		if (accountType.contains(Constants.SAVINGS)) {
			savingsAccountinformation(account);
		} else {
			currentAccountInformation(account);
		}
		return account;
	}

	// Open a savings account
	private void savingsAccountinformation(Account account) {
		
		// Casts the Account to a SavingsAccount
		SavingsAccount savings = (SavingsAccount) account;
		
		try {
			account.setAccountType(Constants.SAVINGS);
			
			System.out.println("Enter date of birth in (dd-MM-yyyy) format");
			String dob = sc.nextLine();
			
			// Creates a date format for parsing
			SimpleDateFormat sdf = new SimpleDateFormat(Constants.DATE_FORMAT);
			Date date = sdf.parse(dob);
			
			savings.setDateOfBirth(date);
			savings.setAge(calculateAge(date));
			
		} catch (ParseException e) {
			log.log(Constants.WARNING,
					getClass().getSimpleName() + " " + "- openSavingsAccount method - " + " " + e.getMessage());
		}
		System.out.println("Enter your gender");
		savings.setGender(sc.nextLine());
		
		System.out.println("Enter your Phone number");
		savings.setPhoneNumber(Long.parseLong(sc.nextLine()));
	}

	// Open a current account
	private void currentAccountInformation(Account account) {
		
		account.setAccountType(Constants.CURRENT);
		CurrentAccount current = (CurrentAccount) account;
		
		System.out.println("Enter your company's name");
		current.setCompanyName(sc.nextLine());
		
		System.out.println("Enter your website");
		current.setUserWebsite(sc.nextLine());
		
		System.out.println("Enter your registration number");
		current.setRegistrationNumber(Long.parseLong(sc.nextLine()));
	}

	// Calculate age based on date of birth
	private int calculateAge(Date dob) {
		
		Calendar birthCalendar = Calendar.getInstance();
		birthCalendar.setTime(dob);
		
		Calendar currentCalendar = Calendar.getInstance();
		// Returns the age based on the difference in years
		return currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
	}

	// Get the current date in a specific format
	private String currentDate() {
		
		Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		
		String currentDateTime = dateFormat.format(currentDate);
		// Returns the formatted current date
		return currentDateTime;
	}

	// Close an account
	private void close(Account account) {
		
		if (manager.close(account)) {
			// If the account closure is successful
			account.setAccountStatus(false);
			account.setAccountClosingDate(currentDate());
			
			log.log(Constants.INFO,
					"Account number " + account.getAccountNumber() + " has been closed on " + currentDate());
		}
	}

	// Create PIN for an account
	private void createPin(Account account) {
		
		// Creates a 4-digit PIN for the account
		String userPin = account.getPinCode();
		if (userPin == null) {
			while (true) {
				
				System.out.println("Please enter a PIN of 4 digits ");
				String pin = sc.nextLine();
				
				System.out.println("Please re-enter the PIN");
				String confirmPin = sc.nextLine();
				
				if (pin.equals(confirmPin) && pin.length() == 4) {
					account.setPinCode(pin);
					System.out.println("PIN has been set");
					break;
				} else {
					log.log(Constants.ERROR, "PIN mismatched, Please try again");
				}
			}
		}
	}

	// Get common account information
	private Account getCommonAccountInformarion(Account account) {
		
		// Collects common information required for any account
		int choice;

		System.out.println("Enter your first name");
		String firstName = sc.nextLine();
		
		System.out.println("Enter your second name");
		String secondName = sc.nextLine();
		
		account.setName(firstName + " " + secondName);
		
		System.out.println("Please enter the initial amount you want to deposit");
		account.setDeposit(Double.parseDouble(sc.nextLine()));
		
		System.out.println("Please select the privilege for your account");
		System.out.println("1. SILVER \n2. GOLD \n3. PLATINUM");
		
		choice = Integer.parseInt(sc.nextLine());
		
		switch (choice) {
		case 1:
			account.setPrivilage(Constants.SILVER);
			break;
		case 2:
			account.setPrivilage(Constants.GOLD);
			break;
		case 3:
			account.setPrivilage(Constants.PLATINUM);
			break;
		default:
			break;
		}
		if (account.getPrivilege().contains(Constants.SILVER)) {
			account.setLimit(25000);
		} else if (account.getPrivilege().contains(Constants.GOLD)) {
			account.setLimit(50000);
		} else {
			account.setLimit(100000);
		}
		createPin(account);
		return account;
	}
}