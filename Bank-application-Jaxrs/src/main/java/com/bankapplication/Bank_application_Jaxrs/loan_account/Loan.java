package com.bankapplication.Bank_application_Jaxrs.loan_account;

public class Loan {
	private int id; 
	private int fixedDepositId; 
	private float amount;
	private float interestSoFar;
	private final float loanAmountFixed; 
	
	{
		interestSoFar = 0; 
	}
	
	public Loan() {
		loanAmountFixed = 0;
	}
	
	public Loan(int fixedDepositId, float amount) {
		this.fixedDepositId = fixedDepositId;
		this.amount = amount;
		loanAmountFixed = amount;
	}
	
	public Loan(int id, int fixedDepositId, float amount) {
		this.id = id; 
		this.fixedDepositId = fixedDepositId;
		this.amount = amount;
		loanAmountFixed = amount; 
	}
	
	public Loan(LoanVM loan) {
		this.fixedDepositId = loan.getFixedDepositId();
		this.amount = loan.getAmount();
		loanAmountFixed = amount; 
	}
	
	public float getLoanAmountFixed() {
		return loanAmountFixed;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getFixedDepositId() {
		return fixedDepositId;
	}
	
	public void setFixedDepositId(int fixedDepositId) {
		this.fixedDepositId = fixedDepositId;
	}
	public float getAmount() {
		return amount;
	}
	synchronized public void setAmount(float amount) {
		this.amount = amount;
	}
	public float getInterestSoFar() {
		return interestSoFar;
	}

	synchronized public void setInterestSoFar(float interestSoFar) {
		this.interestSoFar = interestSoFar;
	} 
	
	
	@Override
	public String toString() {
		return "Current Amount: " + amount + " interest so far: " + interestSoFar + " Original Amount: " +  loanAmountFixed;
	}
}
