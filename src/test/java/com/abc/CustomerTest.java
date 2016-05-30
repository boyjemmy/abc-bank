package com.abc;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomerTest {
	private static final double DOUBLE_DELTA = 1e-15;
    @Test //Test customer statement generation
    public void testApp(){

        Account checkingAccount = new Account(Account.AccountType.CHECKING);
        Account savingsAccount = new Account(Account.AccountType.SAVINGS);

        Customer henry = new Customer("Henry").openAccount(checkingAccount).openAccount(savingsAccount);

        checkingAccount.deposit(100.0);
        savingsAccount.deposit(4000.0);
        savingsAccount.withdraw(200.0);

        assertEquals("Statement for Henry\n" +
                "\n" +
                "Checking Account\n" +
                "  deposit $100.00\n" +
                "Total $100.00\n" +
                "\n" +
                "Savings Account\n" +
                "  deposit $4,000.00\n" +
                "  withdrawal $200.00\n" +
                "Total $3,800.00\n" +
                "\n" +
                "Total In All Accounts $3,900.00", henry.getStatement());
    }

    @Test
    public void testOneAccount(){
        Customer oscar = new Customer("Oscar").openAccount(new Account(Account.AccountType.SAVINGS));
        assertEquals(1, oscar.getNumberOfAccounts());
    }

    @Test
    public void testTwoAccounts(){
        Customer oscar = new Customer("Oscar")
                .openAccount(new Account(Account.AccountType.SAVINGS));
        oscar.openAccount(new Account(Account.AccountType.CHECKING));
        assertEquals(2, oscar.getNumberOfAccounts());
    }

    @Test
    public void testThreeAcounts() {
        Customer oscar = new Customer("Oscar")
                .openAccount(new Account(Account.AccountType.SAVINGS));
        oscar.openAccount(new Account(Account.AccountType.CHECKING));
        oscar.openAccount(new Account(Account.AccountType.MAXI_SAVINGS));
        assertEquals(3, oscar.getNumberOfAccounts());
    }
    
    @Test
    public void testTransfer() throws Exception{
    	Account checkingAccount = new Account(Account.AccountType.CHECKING);
        Account savingsAccount = new Account(Account.AccountType.SAVINGS);

        Customer henry = new Customer("Henry").openAccount(checkingAccount).openAccount(savingsAccount);

        checkingAccount.deposit(1000.0);
        savingsAccount.deposit(4000.0);
        
        Throwable e = new Exception();
        try {
        	henry.transfer(null,savingsAccount, 500.0);
        } catch (Throwable ex) {
            e = ex;
        }
        
        assertTrue(e instanceof IllegalArgumentException);
        
        try {
        	henry.transfer(savingsAccount, null, 500.0);
        } catch (Throwable ex) {
            e = ex;
        }
        
        assertTrue(e instanceof IllegalArgumentException);
       
        try {
        	henry.transfer(checkingAccount, savingsAccount, -500.0);
        } catch (Throwable ex) {
            e = ex;
        }
        
        assertTrue(e instanceof IllegalArgumentException);
        
        try {
        	henry.transfer(checkingAccount, savingsAccount, 2000.0);
        } catch (Throwable ex) {
            e = ex;
        }
        
        assertTrue(e.getMessage().equals("Cannot transfer from the account"));
        
        henry.transfer(checkingAccount, savingsAccount, 500.0);
        
        
        assertEquals(500.0, checkingAccount.getBalance(), DOUBLE_DELTA);
        assertEquals(4500.0,  savingsAccount.getBalance(), DOUBLE_DELTA);
        
    }
    
    
}