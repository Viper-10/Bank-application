package com.bankapplication.Bank_application_Jaxrs.threads;

import com.bankapplication.Bank_application_Jaxrs.customer.Customer;
import com.bankapplication.Bank_application_Jaxrs.loan_account.Loan;
import com.bankapplication.Bank_application_Jaxrs.transaction.Transaction;

public class EMIThread implements Runnable{
	Loan loan;
	float interestPercent = 90; 
	float emiAmount = 100;
	Customer c; 
	
	public EMIThread(Loan loan, Customer c){
		this.loan = loan; 
		this.c = c; 
	}
	
	@Override
	public void run() {
		while(loan.getAmount() != 0) {
			try{
				Thread.sleep(40000);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}
			float interestAmtFromEMI = (emiAmount * interestPercent)/100; 
			float principalAmtFromEMI = emiAmount - interestAmtFromEMI;
			
			
			System.out.println("Before ading emi " + loan);
			
			if(principalAmtFromEMI <= loan.getAmount()) {
				loan.setAmount(loan.getAmount() - principalAmtFromEMI);
			}else {
//				The balance amount is looted by the bank
				loan.setAmount(0);
			}
		
			if(interestAmtFromEMI <= loan.getInterestSoFar()) {				
				loan.setInterestSoFar(loan.getInterestSoFar() - interestAmtFromEMI);
			}else {
//				The balance amount is looted by the bank
				loan.setInterestSoFar(0);
			}

			c.addTransaction(new Transaction("EMI Paid", emiAmount, loan.getAmount() + loan.getInterestSoFar()));
			System.out.println("After ading emi " + loan);
			
			emiAmount += 500;
			interestPercent -= 10; 
		}
		
	}
}
