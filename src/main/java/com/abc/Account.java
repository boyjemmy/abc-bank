package com.abc;

import static java.lang.Math.abs;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
/**
* <h1>Customer Account</h1>
* <p> Three types of accounts are defined: CHECKING, SAVINGS, MAXI_SAVINGS</p>
* @author  Raymond Zhang
* @version 1.0
* @since   2016-05-29
*/

public class Account {

	//Use Enum instead of static int
	public static enum AccountType{
        CHECKING, SAVINGS, MAXI_SAVINGS
    }

	//Use final and assume account type is not changable once created.
    private final AccountType accountType;
    
    //Make this private
    private List<Transaction> transactions;
    
    private volatile InterestCalculator interestCalculator;
    
    

	//Add a balance to avoid calculation based on transactions everytime the balance is needed.
    private double balance;

    /**
     * Constructor.
     * 
     * @param amount (required)
     * Initial balance is 0.0
     */
    public Account(AccountType accountType) {
        this.accountType = accountType;
        this.transactions = new ArrayList<Transaction>();
        this.balance = 0.0;
    }

    /**
    * @param final amount (required), amount has to be greater than 0
    */
    public void deposit(final double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than zero");
        } else {
        	synchronized(this){
        		transactions.add(new Transaction(amount));
        		balance += amount;
        	}
        }
    }
    
    /**
     * @param final amount (required), amount has to be greater than 0 and less than balance.
     */
    public void withdraw(final double amount) {
    	if (amount <= 0) {
    		throw new IllegalArgumentException("amount must be greater than zero");
    	} else {
    		synchronized(this){
    			if(balance < amount){
    				throw new IllegalArgumentException("amount cannot be greater than current balance");
    			}else{
    				synchronized(this){
    	        		transactions.add(new Transaction(-amount));
    	        		balance -= amount;
    	        		System.out.println(balance +"," + amount);
    	        	}
    			}
        	}
    	}
    }

    //Assuming the rates given in the requirment are annual compound rate
    public double getInterestEarned() {
    	return getInterestCalculator().calculateInterest(transactions,LocalDateTime.now());
    }
    
    public void setInterestCalculator(InterestCalculator interestCalculator) {
		this.interestCalculator = interestCalculator;
	}
    
    //Default the interest calculator is derived based on the accountType
    //It can be overridden by explicitly setting it
    //interestCalculator is set as volatile
    private InterestCalculator getInterestCalculator(){
    	if(interestCalculator == null){
    		return InterestCalculatorBuilder.buildInterestCalculator(this.accountType);
    	}else
    		return interestCalculator;
    }

    public double getBalance(){
    	return balance;
    }
    
    public AccountType getAccountType() {
        return accountType;
    }
    
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();

       //Translate to pretty account type
        switch(accountType){
            case CHECKING:
                s.append("Checking Account\n");
                break;
            case SAVINGS:
            	s.append("Savings Account\n");
                break;
            case MAXI_SAVINGS:
            	s.append("Maxi Savings Account\n");
                break;
        }
        synchronized(this){
        	for(Transaction transaction : transactions){
        		s.append(transaction);
        	}
        }
        //Now total up all the transactions
        s.append(String.format("Total $%,.2f\n", getBalance()));
        return s.toString();
    }
}

//Below code are used to implement the different interest rate calculations for daily accrual

interface InterestCalculator {
   double calculateInterest(final List<Transaction> transactions,final LocalDateTime curDate);
}

class InterestCalculatorBuilder {
	public static InterestCalculator  buildInterestCalculator(Account.AccountType accountType ){
		switch(accountType){
			case  CHECKING: return CheckingAcctInterestCalculator.getInstance();
			case  SAVINGS: return SavingsAcctInterestCalculator.getInstance();
			case  MAXI_SAVINGS: return MaxiAcctInterestCalculator.getInstance();
		}
		return null;
	}
}

class CheckingAcctInterestCalculator implements InterestCalculator{
	
	//Assume the rate given in the requirement is annual compound rate
	private static final double RATE = 0.001/365;
	public static CheckingAcctInterestCalculator instance = new CheckingAcctInterestCalculator();
	
	private CheckingAcctInterestCalculator(){}
	
	public static CheckingAcctInterestCalculator getInstance(){
		return instance;
	}
	/*
	 * Flat rate for all balances
	 */
	public double calculateInterest(final List<Transaction> transactions, final LocalDateTime curDate){
		double interest = 0.0;
		LocalDateTime priorDate = null;
		double amount = 0.0;
		for(Transaction transaction : transactions){
			double tempInterest = 0.0;
			if(priorDate != null){
				long dayCnt = ChronoUnit.DAYS.between(priorDate,transaction.getTransactionDate());
				tempInterest = amount * (Math.pow(1+RATE, dayCnt) - 1);
				interest += tempInterest;
			}
			priorDate = transaction.getTransactionDate();
			amount += transaction.getAmount() + tempInterest;
		}
		
		interest += amount * (Math.pow(1+RATE,ChronoUnit.DAYS.between(priorDate,curDate))-1);
	
	return interest;
	}
}

//5% assuming no withdrawals in the past 10 days otherwise 0.1%

class MaxiAcctInterestCalculator implements InterestCalculator{
	//Assume the rate given in the requirement is annual compound rate
	private static final double RATE_LOW = 0.001/365;
	private static final double RATE_HIGH = 0.05/365;
	private static final int DAY_INTERVAL = 10;
	
	private static MaxiAcctInterestCalculator instance = new MaxiAcctInterestCalculator();
	
	private MaxiAcctInterestCalculator(){}
	
	public static MaxiAcctInterestCalculator getInstance(){
		return instance;
	}
	
	/*
	 * if no withdrawal within last 10 days, use high rate
	 * otherwise use low rate
	 */
	public double calculateInterest(final List<Transaction> transactions, final LocalDateTime curDate){
		double interest = 0.0;
		LocalDateTime priorDate = null;
		double amount = 0.0;
		boolean isWithdraw = false;
		for(Transaction transaction : transactions){
			double tempInterest = 0.0;
			if(priorDate != null){
				long dayCnt = ChronoUnit.DAYS.between(priorDate,transaction.getTransactionDate());
				if(isWithdraw){
					if(dayCnt <  DAY_INTERVAL){
					tempInterest = amount * (Math.pow(1+RATE_LOW,dayCnt)-1);
					}else {
						tempInterest = amount * (Math.pow(1+RATE_LOW,DAY_INTERVAL)-1) * (Math.pow(1+RATE_HIGH,dayCnt - DAY_INTERVAL)-1);
					}
				}else{
					tempInterest = amount * (Math.pow(1+RATE_HIGH,dayCnt)-1);
				}
			}
		
			isWithdraw = transaction.getAmount() <0 ? true : false; 
			amount += transaction.getAmount() + tempInterest;
			interest += tempInterest;
			priorDate = transaction.getTransactionDate();
		}
		
		long dayCnt =ChronoUnit.DAYS.between(priorDate,curDate);
		if(isWithdraw){
			if(dayCnt <  DAY_INTERVAL){
				interest += amount * (Math.pow(1+RATE_LOW,dayCnt)-1);
			}else{
				interest += amount * (Math.pow(1+RATE_LOW,DAY_INTERVAL)) * (Math.pow(1+RATE_HIGH,dayCnt - DAY_INTERVAL)) - amount;
			}
		}else
			interest += amount * (Math.pow(1+RATE_HIGH,dayCnt)-1);
	
		return interest;
	}
}

class SavingsAcctInterestCalculator implements InterestCalculator{
	//Assume the rate given in the requirement is annual compound rate
	private static final double RATE_LOW = 0.001/365;
	private static final double RATE_HIGH = 0.002/365;
	private static final double AMOUNT_HIGH = 1000.0;
	
	private static SavingsAcctInterestCalculator instance = new SavingsAcctInterestCalculator();
	
	private SavingsAcctInterestCalculator(){}
	
	public static SavingsAcctInterestCalculator getInstance(){
		return instance;
	}
	
	/*
	 * if balance > AMOUNT_HIGH, use high rate
	 * otherwise use low rate
	 */
	public double calculateInterest(final List<Transaction> transactions,final LocalDateTime curDate){
		double interest = 0.0;
		LocalDateTime priorDate = null;
		double amount = 0.0;
		for(Transaction transaction : transactions){
			double tempInterest = 0.0;
			if(priorDate != null){
				tempInterest= intraCalc(priorDate,transaction.getTransactionDate(),amount);
				amount +=tempInterest;
			}
			amount += transaction.getAmount();
			priorDate = transaction.getTransactionDate();
			interest += tempInterest;
		}
		
		if(amount < AMOUNT_HIGH)
			interest += intraCalc(priorDate,curDate,amount);
		else
			interest += amount * (Math.pow(1+RATE_HIGH,ChronoUnit.DAYS.between(priorDate,curDate))-1);
	
		return interest;
	}
	
	private double intraCalc(final LocalDateTime priorDate, final LocalDateTime curDate, double startAmount){
		long dayCnt = ChronoUnit.DAYS.between(priorDate,curDate);
		double result =0.0;
		while(startAmount < AMOUNT_HIGH && dayCnt > 0){
			result += startAmount * RATE_LOW;
			startAmount *= (1+RATE_LOW);
			dayCnt --;
		}
		
		if(dayCnt > 0){
			result += startAmount * (Math.pow(1+RATE_HIGH, dayCnt) - 1);
		}
	
		return result;
	}
}









