package com.abc;

import java.time.LocalDateTime;
/**
* <h1>Transaction holds the amount and transaction date</h1>
* 
* @author  Raymond Zhang
* @version 1.0
* @since   2016-05-29
*/

public class Transaction {
    private final double amount; 

	private LocalDateTime transactionDate;

    /**
     * Constructor.
     * 
     * @param amount (required)
     * transactionDte is set as current date time.
     */
    
    public Transaction(double amount) {
        this.amount = amount;
        this.transactionDate = LocalDateTime.now();
    }
    
    /**
     * @return transaction amount
     */
    public double getAmount(){
    	return amount;
    }
    
    /**
     * @return the transaction date & time
     */
    public LocalDateTime getTransactionDate(){
    	return transactionDate;
    }
    
    /**
     * @param the transaction date & time
     */
    public void setTransactionDate(LocalDateTime transactionDate){
    	this.transactionDate = transactionDate;
    }
    
    @Override
    public String toString(){
    	StringBuffer result = new StringBuffer();
    	if(amount > 0){
    		result.append(String.format("  deposit $%,.2f\n", amount));
    	}else{
    		result.append(String.format("  withdrawal $%,.2f\n", -amount));
    	}
    	return result.toString();
    	
    }

}
