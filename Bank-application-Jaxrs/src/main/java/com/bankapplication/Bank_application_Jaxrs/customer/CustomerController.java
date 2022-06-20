package com.bankapplication.Bank_application_Jaxrs.customer;

import java.io.IOException;
import java.util.List;

import com.bankapplication.Bank_application_Jaxrs.exceptions.MinimumBalanceViolatedException;
import com.bankapplication.Bank_application_Jaxrs.exceptions.NotEnoughBalanceException;
import com.bankapplication.Bank_application_Jaxrs.fixed_deposit.FixedDeposit;
import com.bankapplication.Bank_application_Jaxrs.fixed_deposit.FixedDepositVM;
import com.bankapplication.Bank_application_Jaxrs.loan_account.Loan;
import com.bankapplication.Bank_application_Jaxrs.loan_account.LoanVM;
import com.bankapplication.Bank_application_Jaxrs.operations.AccountOperations;
import com.bankapplication.Bank_application_Jaxrs.threads.EMIThread;
import com.bankapplication.Bank_application_Jaxrs.threads.FixedDepositThread;
import com.bankapplication.Bank_application_Jaxrs.threads.LoanInterestThread;
import com.bankapplication.Bank_application_Jaxrs.transaction.Transaction;
import com.bankapplication.Bank_application_Jaxrs.transaction.TransactionVM;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

@Path("customers")
public class CustomerController {
	CustomerRepository repository = new CustomerRepository();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Customer> getCustomers() {
		return repository.getCustomers();
	}
	
	@GET
	@Path("transactions")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Transaction> getTransactions(){
		return repository.getTransactions();
	}
	
	@GET
	@Path("customer/{id}/transactions")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Transaction> getTransactionsOfCustomer(@PathParam("id") int id){
		return repository.getTransactionOfCustomer(id);
	}
	
	@PUT
	@Path("operations/deposit")
	public void handleDeposit(TransactionVM t) {
		Customer c = repository.getCustomer(t.holderId);
		AccountOperations.deposit(c.getAccount(), t.amount);
		
		Transaction temp = new Transaction("CashDeposit", t.amount, c.getAccount().getBalance());
        c.addTransaction(temp);
	}
	
	@PUT
	@Path("operations/withdraw")
	public void handleWithdraw(TransactionVM t) throws NotEnoughBalanceException, MinimumBalanceViolatedException {
		Customer c = repository.getCustomer(t.holderId);
		AccountOperations.withdraw(c.getAccount(), t.amount);
		
		Transaction temp = new Transaction("ATMWithdrawal", t.amount, c.getAccount().getBalance());
        c.addTransaction(temp);
	}
	
	@PUT
	@Path("operations/transfer")
	public void handleTransfer(TransactionVM t) throws NotEnoughBalanceException, MinimumBalanceViolatedException {
		Customer sender = repository.getCustomer(t.holderId);
		Customer receiver = repository.getCustomer(t.receiverId);
		
		
		AccountOperations.transfer(sender.getAccount(), receiver.getAccount(), t.amount);
		sender.addTransaction(new Transaction("TransferTo"+receiver.getId(), t.amount, sender.getAccount().getBalance()));
		receiver.addTransaction(new Transaction("TransferFrom"+sender.getId(), t.amount, receiver.getAccount().getBalance()));
	}
	
	
	@POST
	@Path("customer/operations/add-loan")
	public void addLoan(LoanVM loanVM) {
//		TODO: create a loan from fixed deposit. 
//		FRONTEND: If fd exists and fd has enough balance for loan amount
//		TODO: do the frontend checks for 8000 max loan amount. 
		
//		Start a thread putting interest on the loan amount, let's say 5% of the current amount, every minute.
//		Every 3 minutes A EMI thread will run and try to spend off the loan. Assume the EMI thread deposits 100.  
//		Initially 90% of EMI goes to interest(even if the amount exceeds it goes to bank) and 5% of EMI goes to principal. 
		
		Loan loan = new Loan(loanVM);
		int customerId = loanVM.getCustomerId();
		int fdId = loanVM.getFixedDepositId();
		Customer c = repository.getCustomer(customerId);
		
		c.getFixedDeposit(fdId).addLoan(loan);
		c.addTransaction(new Transaction("Open loan", loan.getAmount(), c.getFixedDeposit(fdId).getPrincipal() - loan.getAmount()));	
		
		Thread thread = new Thread(new LoanInterestThread(loan, c));
		thread.start();
		
		Thread newThread = new Thread(new EMIThread(loan, c));
		newThread.start();
	}
	
	@POST
	@Path("customer/operations/add-fixed-deposit")
	public void addFixedDeposit(FixedDepositVM fixedDepositDetails) {
		FixedDeposit fd = new FixedDeposit(fixedDepositDetails);
		Customer c = repository.getCustomer(fixedDepositDetails.getUserId());
		
		c.addFixedDeposit(fd);			
		System.out.println(c);
		System.out.println(fd);
		
		Thread t = new Thread(new FixedDepositThread(fd, c));
		c.addTransaction(new Transaction("Open FD: "+fd.getId(), fd.getPrincipal(), c.getAccount().getBalance() - fd.getPrincipal()));
		t.start();
	}
	
	
	@DELETE
	@Path("customer/{customerId}/fixed-deposits/{depositId}")
	public void endFixedDeposit(@PathParam("customerId") int customerId, @PathParam("depositId") int depositId) {
		FixedDeposit fd = repository.getCustomer(customerId).getFixedDeposit(depositId);
		fd.setAlive(false);		
	}
	
	@GET
	@Path("customer/{id}/fixed-deposits")
	public List<FixedDeposit> getFixedDepositOfCustomer(@PathParam("id") int id) {
		return repository.getCustomer(id).getFixedDeposits();
	}
	
	@GET
	@Path("customer/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Customer getCustomer(@PathParam("id") int id) {
		return repository.getCustomer(id);
	}
	
	@GET
	@Path("customer/{id}/fixed-deposits")
	@Produces(MediaType.APPLICATION_JSON)
	public List<FixedDeposit> getFixedDeposits(@PathParam("id") int id){
		return repository.getCustomer(id).getFixedDeposits();
	}
	
	@POST
	public Customer postCustomer(Customer c) {
		return repository.addCustomer(c);
	}
	
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Customer addCustomer(Customer c) {
		return repository.addCustomer(c);
	}
	
	@Provider
	public class CORSResponseFilter implements ContainerResponseFilter {

	    public void filter(
	        ContainerRequestContext reque1stContext,
	        ContainerResponseContext responseContext
	    ) throws IOException {
	        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
	        headers.add("Access-Control-Allow-Origin", "*"); //Allow Access from everywhere   
	        headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");            
	        headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type");
	    }
	}
}
