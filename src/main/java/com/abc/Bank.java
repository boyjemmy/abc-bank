package com.abc;

import java.util.concurrent.CopyOnWriteArrayList;

/**
* <h1>Bank/h1>
* <p>For similicity, the bank assume name is unique for customers, this can be easily extended to add SSN for customer</p>
* @author  Raymond Zhang
* @version 1.0
* @since   2016-05-29
*/
public class Bank {

    //User CopyOnWriteArrayList for currency read and write
	private CopyOnWriteArrayList<Customer> customers;
    private String firstCustomer;

    public Bank() {
    	customers = new CopyOnWriteArrayList<Customer>();
    }

    public void addCustomer(Customer customer) {
    	//Synchronized with lock on firstCustomer
    	synchronized(this){
    		if(firstCustomer == null){
    			firstCustomer = customer.getName();
    		}
    	}
    	
    	customers.add(customer);
    }

    //Changed to use Stringbuffer instead, added synchronization on customers
    public String customerSummary() {
        StringBuffer summary = new StringBuffer("Customer Summary");
        synchronized(customers){
        	for (Customer c : customers)
        		summary.append("\n - ")
        		.append( c.getName())
        		.append(" (")
        		.append(format(c.getNumberOfAccounts(), "account"))
        		.append(")");
        }
        return summary.toString();
    }

    //added synchronization on customers
    public double totalInterestPaid() {
        double total = 0.0;
        synchronized(customers){
        	for(Customer c: customers)
        		total += c.totalInterestEarned();
        }
        return total;
    }

    public String getFirstCustomer() {
        return firstCustomer;
    }
    

    //Make sure correct plural of word is created based on the number passed in:
    //If number passed in is 1 just return the word otherwise add an 's' at the end
    private static String format(final int number, final String word) {
        StringBuffer result = new StringBuffer();
        result.append(number).append(" ").append(word);
        if(number > 1)
        	result.append("s");
        return result.toString();
    }
}