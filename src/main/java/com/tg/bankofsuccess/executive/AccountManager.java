package com.tg.bankofsuccess.executive;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.tg.bankofsuccess.exceptions.AccountNotActiveException;
import com.tg.bankofsuccess.exceptions.InvalidAmountFoundException;
import com.tg.bankofsuccess.exceptions.InvalidPinNumberFoundException;
import com.tg.bankofsuccess.exceptions.LowBalanceException;
import com.tg.bankofsuccess.exceptions.NoAccountFoundException;
import com.tg.bankofsuccess.exceptions.TransactionLimitExceeded;
import com.tg.bankofsuccess.exceptions.WrongAccountNumberFoundException;
import com.tg.bankofsuccess.exceptions.WrongPinNumberException;
import com.tg.bankofsuccess.models.Account;
import com.tg.bankofsuccess.utilities.Constants;
import com.tg.bankofsuccess.utilities.LogHandlerV1;
import com.tg.bankofsuccess.utilities.TransactionLog;

public class AccountManager {

    // Initialize LogHandlerV1 and TransactionLog
	private static final LogHandlerV1 LOG = new LogHandlerV1();
	private TransactionLog transactionLog = new TransactionLog();

    // Method to open an account
	public boolean open(Account account) {

		// Create an instance of IAccountImpl based on the account type
		IAccount accImpl = AccountImplFactory.create(account);
		
		// Call the open method of the corresponding account implementation
		boolean isQualified = accImpl.open(account);

		// If account is qualified, generate ID, account number, and set status and date
		if (isQualified) {

			generateId(account);
			generateAccountNumber(account);
			account.setAccountStatus(isQualified);
			String currentDateTime = currentDate();
			account.setDate(currentDateTime);
		}
		return isQualified;
	}

    // Method to generate ID for the account
	public void generateId(Account account) {

		Random random = new Random();
		account.setId(random.nextLong(10000, 99999));
	}

    // Method to generate account number for the account
	public void generateAccountNumber(Account account) {

		Random random = new Random();
		long accountNumber = 1000;
		long randomNum = random.nextLong() % 1000000L;
		randomNum = Math.abs(randomNum);
		account.setAccountNumber(String.valueOf(accountNumber) + String.valueOf(randomNum));
	}

    // Method to withdraw funds from an account
	public boolean isWithdrawn(Account account, String accountNumber, double amount, int pinNumber)
			throws TransactionLimitExceeded, AccountNotActiveException, InvalidPinNumberFoundException,
			InvalidAmountFoundException, WrongPinNumberException, LowBalanceException, WrongAccountNumberFoundException,
			NoAccountFoundException {

		boolean isWithdrawn = false;
		if (account != null) {
			
			if (account.getAccountStatus()) {
				
				if (account.getAccountNumber().contains(accountNumber)) {
					
					if (amount >= 1 && amount != 0) {
						
						if (pinNumber > 0) {
							
							if (account.getAccountBalance() >= amount) {
								
								if (!account.isOverLimit(amount)) {
									
									if (Integer.parseInt(account.getPinCode()) == pinNumber) {
										
										account.setDeposit(account.getAccountBalance() - amount);
										transactionLog.limitUpdater(account, amount);
										isWithdrawn = true;
										LOG.logTransaction(Constants.DEBIT, String.valueOf(amount), account);
									} else {
										throw new WrongPinNumberException("Wrong Pin Entered");
									}
								} else {
									throw new TransactionLimitExceeded("Your current transaction limit is Rs."
											+ account.getAvailableLimit());
								}
							} else {
								throw new LowBalanceException(
										"Insufficient balance.");
							}
						} else {
							throw new InvalidPinNumberFoundException("Invalid amount!");
						}
					} else {
						throw new InvalidAmountFoundException("Invalid amount!");
					}
				} else {
					throw new WrongAccountNumberFoundException("Wrong account number");
				}
			} else {
				throw new AccountNotActiveException(
						"Your account has been deactivated.");
			}
		} else {
			throw new NoAccountFoundException("Account not found! Please ensure account is created");
		}
		return isWithdrawn;
	}

    // Method to transfer funds between accounts
	public boolean transferFunds(Account sender, Account receiver, double amountToTransfer, int pinNumber)
			throws AccountNotActiveException, WrongPinNumberException, LowBalanceException, TransactionLimitExceeded {
		
		// Declaration
		boolean isTransferred = false;
		
		// 1. Is from account active
		if (sender.getAccountStatus()) {
			// 2. Is to account active
			if (receiver.getAccountStatus()) {
				// 3. Check if pin number is valid
				if (Integer.parseInt(sender.getPinCode()) == pinNumber) {
					// 4. Check if sufficient funds are available in from account
					if (sender.getAccountBalance() >= amountToTransfer) {
						// 5. Check if the transfer limit is exceeded
						if (!sender.isOverLimit(amountToTransfer)) {
							// 6. Deduct amount from sender
							sender.setDeposit(sender.getAccountBalance() - amountToTransfer);
							// 7. Add amount to receiver
							receiver.setDeposit(receiver.getAccountBalance() + amountToTransfer);
							// 8. Add into Log the transfer made
							transactionLog.limitUpdater(sender, amountToTransfer);
							// 9. Update the status of transfer
							isTransferred = true;
							LOG.logTransaction(Constants.DEBIT, String.valueOf(amountToTransfer), sender);
							LOG.logTransaction(Constants.CREDIT, String.valueOf(amountToTransfer), receiver);

						} else {
							throw new TransactionLimitExceeded("Your current transaction limit is Rs."
									+ sender.getAvailableLimit());
						}
					} else {
						throw new LowBalanceException("Insufficient balance.");
					}
				} else {
					throw new WrongPinNumberException("Wrong PIN Entered");
				}
			} else {
				throw new AccountNotActiveException(
						"Account has been deactivated.");
			}
		} else {
			throw new AccountNotActiveException(
					"Account has been deactivated.");
		}
		// 10. Return the status
		return isTransferred;
	}

    // Method to deposit funds into an account
	public boolean isDeposited(Account account, String accountNumber, double amount, int pinNumber)
			throws WrongPinNumberException, LowBalanceException, WrongAccountNumberFoundException,
			AccountNotActiveException {
		
		boolean isDeposited = false;
		if (account.getAccountStatus()) {
			
			if (account.getAccountNumber().contains(accountNumber)) {
				
				if (Integer.parseInt(account.getPinCode()) == pinNumber) {
					account.setDeposit(account.getAccountBalance() + amount);
					transactionLog.limitUpdater(account, amount);
					isDeposited = true;
					LOG.logTransaction(Constants.CREDIT, String.valueOf(amount), account);
				} else {
					throw new WrongPinNumberException("Sorry! It seems like your entred wrong PIN");
				}
			} else {
				throw new WrongAccountNumberFoundException("Sorry! It seems like you entred wrong account number");
			}
		} else {
			throw new AccountNotActiveException(
					"Sorry! It seems like your account has been deactivated, Please contact the branch manager.");
		}
		return isDeposited;
	}

    // Method to get current date and time
	protected static String currentDate() {
		
		Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		
		String currentDateTime = dateFormat.format(currentDate);
		return currentDateTime;
	}

    // Method to close an account
	public boolean close(Account account) {
		
		if (account.getAccountStatus()) {
			return true;
		} else {
			LOG.log(Constants.INFO, "Account is already deactivated");
			return false;
		}
	}
}
