package com.bankapplication.Bank_application_Jaxrs.transaction;

public class TransactionVM {
	public int holderId;
	public float amount;
	public int receiverId; 
	
	public TransactionVM(){
		holderId = 0;
		amount = 0; 
		receiverId = 0; 
	}
	
	public TransactionVM(int holderId, float amount){
		this.holderId = holderId; 
		this.amount = amount; 
	}
	
	public TransactionVM(int holderId,int receiverId,float amount){
		this.holderId = holderId; 
		this.receiverId = receiverId;
		this.amount = amount; 
	}	
	
	@Override
	public String toString() {
		return holderId + " " + receiverId + " " + amount;
	}
}
