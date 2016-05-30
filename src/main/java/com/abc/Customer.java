package com.abc;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class Customer {
    private String name;
    private List<Account> accounts;

    public Customer(String name) {
        this.name = name;
        this.accounts = new ArrayList<Account>();
    }

    public String getName() {
        return name;
    }

    public Customer openAccount(Account account) {
        synchronized(accounts){
        	accounts.add(account);
        }
        return this;
    }

    public int getNumberOfAccounts() {
    	synchronized(accounts){
    		return accounts.size();
    	}
    }

    public double totalInterestEarned() {
        double total = 0.0;
        synchronized(accounts){
        	for (Account a : accounts)
        		total += a.getInterestEarned();
        }
        return total;
    }

    public String getStatement() {
        StringBuffer statement = new StringBuffer();
        statement.append("Statement for ").append(name).append("\n\n");
        double total = 0.0;
        for (Account a : accounts) {
        	statement.append(a).append("\n");
            total += a.getBalance();
        }
        statement.append(String.format("Total In All Accounts $%,.2f", total));
        return statement.toString();
    }
    
    //This process needs to be in one atomic transaction, if failed in the middle, whole transaction
    //need to be rolled back, no coding logic is implemented here, in real application, this can be
    //handled by transaction manager.
    public void transfer(Account accountFrom, Account accountTo, final double amount) throws Exception{
    	
    	if (accountFrom == null) {
    		throw new IllegalArgumentException("from account cannot be null");
    	}
    	if (accountTo == null) {
    		throw new IllegalArgumentException("to account cannot be null");
    	}
    	if (amount < 0) {
    		throw new IllegalArgumentException("amount must be greater than zero");
    	}
    	
    	try{
    		accountFrom.withdraw(amount);	
    	}catch(Exception e){
    		throw new Exception ("Cannot transfer from the account");
    	}
    	try{
    		accountTo.deposit(amount);	
    	}catch(Exception e){
    		throw new Exception ("Cannot transfer to the account");
    	}
    }
}