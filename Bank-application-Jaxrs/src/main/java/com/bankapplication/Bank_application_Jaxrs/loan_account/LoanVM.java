package com.bankapplication.Bank_application_Jaxrs.loan_account;

public class LoanVM {
	private int customerId; 
	private int fixedDepositId; 
	private float amount;
	
	public LoanVM() {}
	
	public LoanVM(int customerId, int fixedDepositId, float amount) {
		this.customerId = customerId;
		this.fixedDepositId = fixedDepositId;
		this.amount = amount;
	}
	
	public int getCustomerId() {
		return customerId;
	}
	public void setCustomerId(int customerId) {
		this.customerId = customerId;
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
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	
}
