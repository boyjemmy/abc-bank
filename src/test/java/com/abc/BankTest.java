package com.abc;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

/**
* <h1>BankTest</h1>
* <p> MockupInterestCalculator is used in this test</p>
* <p> As this is Unit Test for Bank and Account functionality, the calculator logic will be tested 
* 	in separate test cases.
* </p>
*/

public class BankTest {
    private static final double DOUBLE_DELTA = 1e-15;

    @Test
    public void customerSummary() {
        Bank bank = new Bank();
        Customer john = new Customer("John");
        john.openAccount(new Account(Account.AccountType.CHECKING));
        bank.addCustomer(john);

        assertEquals("Customer Summary\n - John (1 account)", bank.customerSummary());
        
        Customer bill = new Customer("Bill");
        bill.openAccount(new Account(Account.AccountType.SAVINGS));
        bill.openAccount(new Account(Account.AccountType.CHECKING));
        bank.addCustomer(bill);
        
        assertEquals("Customer Summary\n - John (1 account)\n - Bill (2 accounts)", bank.customerSummary());
        
    }

    @Test
    public void checkingAccount() {
        Bank bank = new Bank();
        Account checkingAccount = new Account(Account.AccountType.CHECKING);
        Customer bill = new Customer("Bill").openAccount(checkingAccount);
        bank.addCustomer(bill);

        checkingAccount.deposit(100.0);
        checkingAccount.setInterestCalculator(new mockUpInterestCalculator(Account.AccountType.CHECKING));
        assertEquals(0.1, bank.totalInterestPaid(), DOUBLE_DELTA);
    }

    @Test
    public void savings_account() {
        Bank bank = new Bank();
        Account checkingAccount = new Account(Account.AccountType.SAVINGS);
        bank.addCustomer(new Customer("Bill").openAccount(checkingAccount));

        checkingAccount.deposit(1500.0);
        checkingAccount.setInterestCalculator(new mockUpInterestCalculator(Account.AccountType.SAVINGS));
        assertEquals(2.0, bank.totalInterestPaid(), DOUBLE_DELTA);
    }

    @Test
    public void maxi_savings_account() {
        Bank bank = new Bank();
        Account checkingAccount = new Account(Account.AccountType.MAXI_SAVINGS);
        bank.addCustomer(new Customer("Bill").openAccount(checkingAccount));

        checkingAccount.deposit(3000.0);
        checkingAccount.setInterestCalculator(new mockUpInterestCalculator(Account.AccountType.MAXI_SAVINGS));
        assertEquals(170.0, bank.totalInterestPaid(), DOUBLE_DELTA);
    }
}


class mockUpInterestCalculator implements InterestCalculator{
	Account.AccountType accountType;
	
	mockUpInterestCalculator(Account.AccountType accountType){
		this.accountType = accountType;
	}
	public double calculateInterest(final List<Transaction> transactions, final LocalDateTime curDate){
		double amount = 0.0;
		for(Transaction transaction : transactions){
			amount += transaction.getAmount();
		}
		switch(accountType){
        case SAVINGS:
            if (amount <= 1000)
                return amount * 0.001;
            else
                return 1 + (amount-1000) * 0.002;
        case MAXI_SAVINGS:
            if (amount <= 1000)
                return amount * 0.02;
            if (amount <= 2000)
                return 20 + (amount-1000) * 0.05;
            return 70 + (amount-2000) * 0.1;
        default:
            return amount * 0.001;
		}
    }
}


