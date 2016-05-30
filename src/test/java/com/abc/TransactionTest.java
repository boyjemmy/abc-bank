package com.abc;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class TransactionTest {
   
	@Test
    public void transaction() {
        Transaction t = new Transaction(5.0);
        assertTrue(t instanceof Transaction);
        assertNotNull(t.getTransactionDate());
    }
}