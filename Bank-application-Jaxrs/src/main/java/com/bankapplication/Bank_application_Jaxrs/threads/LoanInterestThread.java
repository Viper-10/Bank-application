package com.bankapplication.Bank_application_Jaxrs.threads;

import com.bankapplication.Bank_application_Jaxrs.customer.Customer;
import com.bankapplication.Bank_application_Jaxrs.loan_account.Loan;
import com.bankapplication.Bank_application_Jaxrs.transaction.Transaction;

public class LoanInterestThread implements Runnable{
	Loan loan;
	Customer c;
	// This thread goes and adds 10% of the principal amount in loan to the loan amount as interest
	float interestPercent = 10;
	
	public LoanInterestThread(Loan loan, Customer c){
		this.loan = loan; 
		this.c = c; 
	}
	
	@Override
	public void run() {
		while(loan.getAmount() != 0) {
			
			try {				
				Thread.sleep(20000);
			}catch(InterruptedException s) {
				s.getStackTrace();
			}
			
			System.out.println("Before adding interest: "+loan);
			float interest = (interestPercent * loan.getAmount())/100; 
			loan.setInterestSoFar(interest + loan.getInterestSoFar());
			c.addTransaction(new Transaction("Loan interest", interest, loan.getInterestSoFar() + loan.getAmount()));
			System.out.println("After adding interest: "+loan);
		}
		
		c.addTransaction(new Transaction("Close Loan", loan.getLoanAmountFixed(), c.getFixedDeposit(loan.getFixedDepositId()).getPrincipal() + loan.getLoanAmountFixed()));
	}
}
