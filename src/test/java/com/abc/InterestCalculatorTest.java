package com.abc;

import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

public class InterestCalculatorTest {
	private static final double DOUBLE_DELTA = 1e-10;
	
	@Test
	public void testCheckingInterestCalculator(){
		InterestCalculator calculator = InterestCalculatorBuilder.buildInterestCalculator(Account.AccountType.CHECKING);
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transaction transaction1 = new Transaction(1000.0);
		transaction1.setTransactionDate(LocalDateTime.of(2016, Month.MAY, 1, 10, 10, 30));
		
		Transaction transaction2 = new Transaction(2000.0);
		transaction2.setTransactionDate(LocalDateTime.of(2016, Month.MAY, 11, 10, 10, 30));

		transactions.add(transaction1);
		transactions.add(transaction2);
		
		double amount = calculator.calculateInterest(transactions, LocalDateTime.of(2016, Month.MAY, 16, 10, 10, 30));
		double expectedAmount = 1000 * (Math.pow(1+0.001/365, 15) -1) + 2000 * (Math.pow(1+0.001/365, 5) -1);
		assertEquals(expectedAmount, amount , DOUBLE_DELTA);
	}
	
	@Test
	public void testSavingInterestCalculator(){
		InterestCalculator calculator = InterestCalculatorBuilder.buildInterestCalculator(Account.AccountType.SAVINGS);
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transaction transaction1 = new Transaction(500.0);
		transaction1.setTransactionDate(LocalDateTime.of(2016, Month.MAY, 1, 10, 10, 30));
		
		Transaction transaction2 = new Transaction(2000.0);
		transaction2.setTransactionDate(LocalDateTime.of(2016, Month.MAY, 11, 10, 10, 30));

		Transaction transaction3 = new Transaction(-1501.0);
		transaction3.setTransactionDate(LocalDateTime.of(2016, Month.MAY, 21, 10, 10, 30));

		transactions.add(transaction1);
		transactions.add(transaction2);
		transactions.add(transaction3);
		
		double amount = calculator.calculateInterest(transactions, LocalDateTime.of(2017, Month.MAY, 21, 10, 10, 30));
		double expectedAmount = 500 * (Math.pow(1+0.001/365, 10) -1) ;
		expectedAmount += (500 * Math.pow(1+0.001/365, 10) + 2000.0) * (Math.pow(1+0.002/365, 10) -1);
		expectedAmount = (expectedAmount + 2500 - 1501) * Math.pow(1+0.001/365, 311); 
		expectedAmount *= Math.pow(1+0.002/365, 54);
		
		assertEquals(expectedAmount - 999, amount , DOUBLE_DELTA);
	}
	
	@Test
	public void testMaxiInterestCalculator(){
		InterestCalculator calculator = InterestCalculatorBuilder.buildInterestCalculator(Account.AccountType.MAXI_SAVINGS);
		List<Transaction> transactions = new ArrayList<Transaction>();
		Transaction transaction1 = new Transaction(1000.0);
		transaction1.setTransactionDate(LocalDateTime.of(2016, Month.MAY, 1, 10, 10, 30));
		
		Transaction transaction2 = new Transaction(-500.0);
		transaction2.setTransactionDate(LocalDateTime.of(2016, Month.MAY, 11, 10, 10, 30));
		
		transactions.add(transaction1);
		transactions.add(transaction2);
		
		double amount = calculator.calculateInterest(transactions, LocalDateTime.of(2016, Month.MAY, 31, 10, 10, 30));
		double expectedAmount = 1000 * (Math.pow(1+0.05/365, 10)) ;
		expectedAmount = (expectedAmount - 500) * Math.pow(1+0.001/365, 10);
		expectedAmount *= Math.pow(1+0.05/365, 10); 
		assertEquals(expectedAmount - 500, amount , DOUBLE_DELTA);
	}
}
