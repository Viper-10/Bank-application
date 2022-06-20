package com.bankapplication.Bank_application_Jaxrs.threads;

import com.bankapplication.Bank_application_Jaxrs.account.Account;
import com.bankapplication.Bank_application_Jaxrs.customer.Customer;
import com.bankapplication.Bank_application_Jaxrs.fixed_deposit.FixedDeposit;
import com.bankapplication.Bank_application_Jaxrs.transaction.Transaction;

public class FixedDepositThread implements Runnable{ 
	FixedDeposit fd; 	
	Customer c; 
	Account acc; 
	int chosenTimePeriod; 
	float interest; 
	private static final float CANCEL_INTEREST_PERCENTAGE = 0.05f;
	private static final float CALCEL_FEE_PERCENT = 0.5f; 

	public FixedDepositThread(){}
	
	public FixedDepositThread(FixedDeposit fd, Customer c){
		this.fd = fd; 
		this.c = c; 
		this.acc = c.getAccount();
		chosenTimePeriod = fd.getEndingTime().getMinute() - fd.getCreationTime().getMinute();
	}
	
	@Override
	public void run() {
		acc.setBalance(acc.getBalance() - fd.getPrincipal());
		int t = chosenTimePeriod; 
		float interest = (1 * fd.getPrincipal())/100;
		System.out.println(fd);
		
		while(fd.isAlive() && t > 0) {
			try{
				Thread.sleep(60000);
			}catch(InterruptedException e) {
				e.printStackTrace();
			}			
		
//			System.out.println("Before adding interest balance: "+ acc.getBalance());
			acc.setBalance(acc.getBalance() + interest);
//			System.out.println("After adding interest balance: "+ acc.getBalance());
			c.addTransaction(new Transaction("Credit FD interest", interest, acc.getBalance()));

			t--;
		}		

//		Then it means the customer chose to close the fd before it ended
		
		if(t != 0) {
//			System.out.println("User cancelled the thread");
//			Taking back all the interest credited to him
			acc.setBalance(acc.getBalance() - (interest * (chosenTimePeriod - t)));		
			
//			System.out.println("User balance restored to : " + acc.getBalance());
			
			float fee = Math.max((CALCEL_FEE_PERCENT * fd.getPrincipal())/100, 500);
//			System.out.println("You'll be charged " + fee + " for cancelling the fixed deposit. A 0.05 percent of interest shall be given for all the days");
			
			acc.setBalance(acc.getBalance() - fee);
			
			float interestGivenOnCancel = (CANCEL_INTEREST_PERCENTAGE * fd.getPrincipal())/100;
			c.addTransaction(new Transaction("FD cancel fee", fee + interest * (chosenTimePeriod - t), acc.getBalance()));
			acc.setBalance(acc.getBalance() + (interestGivenOnCancel * chosenTimePeriod * fd.getPrincipal())/100);
			c.addTransaction(new Transaction("Interest after cancel", (interestGivenOnCancel * chosenTimePeriod * fd.getPrincipal())/100, acc.getBalance()));
		}	
		
		// Giving the customer his principal back
		acc.setBalance(acc.getBalance() + fd.getPrincipal());
		c.addTransaction(new Transaction("Close FD: " + fd.getId(), fd.getPrincipal(), acc.getBalance()));
		fd.setAlive(false);
	}
}
