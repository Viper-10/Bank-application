package com.bankapplication.Bank_application_Jaxrs.fixed_deposit;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import com.bankapplication.Bank_application_Jaxrs.loan_account.Loan;

public class FixedDeposit {
	private LocalDateTime creationTime; 
	private LocalDateTime endingTime; 
	private float principal;
	private int id; 
    private boolean isAlive;
    List<Loan> loans;
    
	{ 
		isAlive = true; 
		this.creationTime = LocalDateTime.now();
		loans = new LinkedList<>();
	}
	
	public FixedDeposit(){
		
	}
	
	public FixedDeposit(int closeAfter, float principal){
		this.endingTime = creationTime.plusMinutes(closeAfter);
		this.principal = principal;
	}
	
	public FixedDeposit(FixedDepositVM fd) {
		this.endingTime = creationTime.plusMinutes(fd.getCloseAfter());
		this.principal = fd.getPrincipal();
	}
	
	public FixedDeposit(LocalDateTime creationTime, LocalDateTime endingTime, float principal, boolean isAlive) {
		this.creationTime = creationTime; 
		this.endingTime = endingTime; 
		this.principal = principal; 
		this.isAlive = isAlive; 	
	}
	
	public FixedDeposit(int id,  float principal, LocalDateTime creationTime, LocalDateTime endingTime, boolean isAlive) {
		this.id = id; 
		this.creationTime = creationTime; 
		this.endingTime = endingTime; 
		this.principal = principal; 
		this.isAlive = isAlive; 	
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public LocalDateTime getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(LocalDateTime creationTime) {
		this.creationTime = creationTime;
	}

	public LocalDateTime getEndingTime() {
		return endingTime;
	}

	public void setEndingTime(LocalDateTime endingTime) {
		this.endingTime = endingTime;
	}

	public float getPrincipal() {
		return principal;
	}

	public void setPrincipal(float principal) {
		this.principal = principal;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	
	public void addLoan(Loan loan) {
		loan.setId(loans.size());
		loans.add(loan);
	}
	
	public void addLoanWithId(Loan loan) {
		loans.add(loan);
	}
	
	@Override
	public String toString() {
		return "Id: " + id + " Creation time: " + creationTime + " Ending time: " + endingTime + " principal: " + principal + " alive: " + isAlive;
	}

	
}
