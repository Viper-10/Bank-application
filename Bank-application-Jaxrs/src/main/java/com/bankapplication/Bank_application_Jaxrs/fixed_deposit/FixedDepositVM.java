package com.bankapplication.Bank_application_Jaxrs.fixed_deposit;

public class FixedDepositVM {
	private int closeAfter; 
	private float principal;
	private int userId; 
//	int chosenInterestRate; 
	
	public FixedDepositVM(){
		
	}
	
	public FixedDepositVM(int userId, int principal,int closeAfter){
		this.userId = userId; 
		this.principal = principal; 
		this.closeAfter = closeAfter;
//		this.chosenInterestRate = chosenInterestRate;
	}

	public int getCloseAfter() {
		return closeAfter;
	}

	public void setCloseAfter(int closeAfter) {
		this.closeAfter = closeAfter;
	}

	public float getPrincipal() {
		return principal;
	}

	public void setPrincipal(float principal) {
		this.principal = principal;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	
}
