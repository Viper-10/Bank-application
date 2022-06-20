package com.bankapplication.Bank_application_Jaxrs;

import com.bankapplication.Bank_application_Jaxrs.account.Account;
import com.bankapplication.Bank_application_Jaxrs.constants.Constants;
import com.bankapplication.Bank_application_Jaxrs.cryptography.AddOneCryptography;
import com.bankapplication.Bank_application_Jaxrs.cryptography.CryptographyI;
import com.bankapplication.Bank_application_Jaxrs.customer.Customer;
import com.bankapplication.Bank_application_Jaxrs.database.DatabaseOperations;
import com.bankapplication.Bank_application_Jaxrs.exceptions.MinimumBalanceViolatedException;
import com.bankapplication.Bank_application_Jaxrs.exceptions.NotEnoughBalanceException;
import com.bankapplication.Bank_application_Jaxrs.operations.AccountOperations;
import com.bankapplication.Bank_application_Jaxrs.threads.StoreToDB;
import com.bankapplication.Bank_application_Jaxrs.transaction.Transaction;

import java.io.*;
import java.sql.Connection;
import java.util.*;

public class BankApplication {
    public static List<Customer> customers;
    private static final Map<Integer, Customer> getCustomer = new HashMap<>();
    private static final CryptographyI cryptography = new AddOneCryptography();
    private static final Scanner sc = new Scanner(System.in);
    public static boolean stop = false;

    public static void main(String[] args) throws IOException {
//        loadCustomersFromFile();

        customers = DatabaseOperations.loadCustomersFromDB();
        if(customers != null){
            for(Customer c : customers){
                getCustomer.put(c.getId(), c);
            }
        }

        Thread storePeriodically = new Thread(new StoreToDB());
        storePeriodically.start();
        printAllCustomersWithDetails();

        print("\n");
        print(Constants.WELCOME_MESSAGE);
        print("\n");
        print(Constants.ARE_YOU_CUSTOMER);

        char c;
        boolean newCustomer = false;

//        EXISTING CUSTOMER OR NEW CUSTOMER
        do {
            c = sc.next().charAt(0);

            if (c == Constants.NO) {
                print(Constants.CREATE_ACCOUNT_MESSAGE);
                newCustomer = true;
            } else if (c == Constants.YES) {
                print(Constants.WELCOME_BACK_MESSAGE);

            } else {
                print(Constants.ENTER_VALID_INPUT);
            }

        } while (c != Constants.YES && c != Constants.NO);

        Customer loggedInCustomer;

        // LOGIN AND AUTHENTICATION
        if (newCustomer) {
            loggedInCustomer = addNewCustomer();
        } else {
            loggedInCustomer = authenticateUser();
            while (loggedInCustomer == null) {
                print(Constants.PASSWORD_INCORRECT);
                loggedInCustomer = authenticateUser();
            }
        }

        print(Constants.DISPLAY_ALL_POSSIBLE_OPERATIONS);
        print("");

        int choice = sc.nextInt();
        Set<Integer> transactionChoices = new HashSet<>();
        transactionChoices.add(1);
        transactionChoices.add(2);
        transactionChoices.add(3);

//        BANK OPERATIONS
        while (choice != -1) {
            if (transactionChoices.contains(choice)) {
                if (loggedInCustomer.noOfTransactions() != 0 && loggedInCustomer.noOfTransactions() % 5 == 0) {
                    print(Constants.CHANGE_EVERY_5_TRANSACTIONS);
                    changePasswordHelper(loggedInCustomer);

                    if (loggedInCustomer.noOfTransactions() % 10 == 0 && notInTop3Users(loggedInCustomer)) {
                        print(Constants.MAINTANENCE_FEE_CHARGE_MESSAGE);
                        try {
                            AccountOperations.withdraw(loggedInCustomer.getAccount(), Constants.MAINTANENCE_FEE);
                        } catch (Exception e) {
                            print(e.toString());
                        }
                    }
                }
            }

            switch (choice) {
                case 1 :{
                	withdrawHelper(loggedInCustomer);
                	break;
                }

                case 2 : {
                	depositHelper(loggedInCustomer);
                	break;
                }

                case 3 : {
                	transferHelper(loggedInCustomer);
                	break;
                }

                case 4 : {
                    changePasswordHelper(loggedInCustomer);
                    print(Constants.DISPLAY_ALL_POSSIBLE_OPERATIONS);
                    break;
                }

                case 5 :{
                	getTopNCustomersHelper();
                	break;
                }

                case 6 : {
                	checkBalanceHelper(loggedInCustomer);
                	break;
                }

                case 7 : {
                	printCustomerTransactions(loggedInCustomer);
                	break;
                }

                case 8 : {
                    notInTop3Users(loggedInCustomer);
                    print(Constants.DISPLAY_ALL_POSSIBLE_OPERATIONS);
                    break;
                }

                default : {
                	print(Constants.ENTER_VALID_INPUT);
                }
            }
            choice = sc.nextInt();
        }

        storePeriodically.stop();

        print(Constants.SEE_YOU_AGAIN_MESSAGE);

        stop = true;
        try {
            storePeriodically.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DatabaseOperations.persistCustomersToDB();

//        persistCustomersToFile();
    }

    private static void loadCustomersFromFile() throws IOException {

        File file = new File("D:\\customers.txt");

        if (!file.exists()) {
            System.out.println(Constants.FILE_NOT_LOADED);
            return;
        }

        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;

        while (((line = br.readLine()) != null)) {
            Customer c = createCustomerFromDetails(line);
            customers.add(c);
            getCustomer.put(c.getId(), c);
        }

        Customer.idCount = customers.size() - Constants.DEFAULT_INPUT;
        Account.accountNumberCount = customers.size() - Constants.DEFAULT_INPUT + Constants.ACC_NO_INITIAL_SIZE;

        printAllCustomersWithDetails();
    }

    private static Customer createCustomerFromDetails(String line) {
        String[] fields = line.split(",");
        fields[4] = cryptography.decrypt(fields[4]);
        return Customer.createCustomerFromStringFields(fields);

    }

    private static void printAllCustomersWithDetails() {
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }

    private static void persistCustomersToFile() throws IOException {
        FileWriter fWriter = new FileWriter(
                "D:\\customers.txt");

        for (Customer c : customers) {
            String[] fields = c.toString().split(",");
            fields[4] = cryptography.encrypt(fields[4]);
            fWriter.write(fields[0] + "," + fields[1] + "," + fields[2] + "," + fields[3] + "," + fields[4] + "\n");
        }

        fWriter.close();
    }

    private static void print(String message) {
        System.out.println(message);
    }

    private static boolean invalidPassword(String password) {
        int lowercaseCount = 0, uppercaseCount = 0, numbers = 0;

        for (int i = 0; i < password.length(); ++i) {
            char c = password.charAt(i);

            if (Character.isLowerCase(c)) {
                lowercaseCount++;
            } else if (Character.isUpperCase(c)) {
                uppercaseCount++;
            } else if (c >= '0' && c <= '9') {
                numbers++;
            }
        }

        return !(lowercaseCount >= 2 && uppercaseCount >= 2 && numbers >= 2);
    }

    private static Customer addNewCustomer() {
        boolean passwordIncorrect = true;
        Customer loggedInCustomer = null;

        do {
            print(Constants.ENTER_NAME);
            sc.nextLine();
            String name = sc.nextLine();
            print(Constants.ENTER_PASSWORD_WITH_CONSTRAINT);
            String password = sc.next();

            if (invalidPassword(password)) {
                print(Constants.PASSWORD_CONSTRAINT_VIOLATION);
                continue;
            }

            print(Constants.CONFIRM_PASSWORD);
            String confirmPassword = sc.next();

            if (!confirmPassword.equals(password)) {
                print(Constants.CONFIRM_PASSWORD_NOT_MATCH);
                continue;
            }

            Customer newC = new Customer(name, password);
            newC.setId();
            newC.getAccount().initializeAccountNumber();
            customers.add(newC);
            getCustomer.put(newC.getId(), newC);
            loggedInCustomer = newC;
            passwordIncorrect = false;
        } while (passwordIncorrect);

        print(Constants.ACCOUNT_CREATION_SUCCESS);
        print("\n");
        loggedInCustomer.addTransaction(new Transaction("Opening", 10000, 10000));
        return loggedInCustomer;
    }

    private static Customer authenticateUser() {
        print(Constants.ENTER_NAME);
        sc.nextLine();
        String name = sc.nextLine();
        print(Constants.ENTER_PASSWORD);
        String password = sc.next();

        for (Customer c : customers) {
            if (c.getName().equals(name) && c.getPassword().equals(password)) {
                return c;
            }
        }

        return null;
    }

    private static void withdrawHelper(Customer loggedInCustomer) {
        print(Constants.ENTER_WITHDRAW_AMT);
        int amt = sc.nextInt();
        float bankBalance;
        try {
            bankBalance = AccountOperations.withdraw(loggedInCustomer.getAccount(), amt);
            print(Constants.WITHDRAW_SUCCESS + amt);
            print(Constants.CURRENT_BANK_BALANCE + bankBalance);

            Transaction t = new Transaction("ATMWithdrawal", amt, bankBalance);
            loggedInCustomer.addTransaction(t);

        } catch (NotEnoughBalanceException ne) {
            print(Constants.YOU_NEED + ne.getBalanceNeeded() + "\n" + Constants.BALANCE_INSUFFICIENT);
        } catch (MinimumBalanceViolatedException me) {
            print(me.getMessage());
        } finally {
            print(Constants.DISPLAY_ALL_POSSIBLE_OPERATIONS);
        }

    }

    private static void depositHelper(Customer loggedInCustomer) {
        print(Constants.ENTER_DEPOSIT_AMT);
        int amt = sc.nextInt();
        float bankBalance;

        bankBalance = AccountOperations.deposit(loggedInCustomer.getAccount(), amt);
        Transaction t = new Transaction("CashDeposit", amt, bankBalance);
        loggedInCustomer.addTransaction(t);
        print(Constants.DEPOSIT_SUCCESS + amt);
        print(Constants.CURRENT_BANK_BALANCE + bankBalance);

        print(Constants.DISPLAY_ALL_POSSIBLE_OPERATIONS);
    }

    private static void transferHelper(Customer loggedInCustomer) {
        print(Constants.ENTER_RECEIVER_BANK_ID);

        int receiverId = sc.nextInt();

        boolean properInput = !(receiverId == loggedInCustomer.getId() || !getCustomer.containsKey(receiverId));
        while (!properInput) {
            if (!getCustomer.containsKey(receiverId)) print(Constants.INCORRECT_RECEIVERID);
            else print(Constants.RECEIVER_ID_SAME_AS_LOGGEDINUSER_ID);
            print(Constants.ENTER_RECEIVER_BANK_ID);
            receiverId = sc.nextInt();
            properInput = !(receiverId == loggedInCustomer.getId() || !getCustomer.containsKey(receiverId));
        }

        Customer receiver = getCustomer.get(receiverId);
        Account receiverAcc = receiver.getAccount();
        print(Constants.ENTER_TRANSFER_AMT);

        int amt = sc.nextInt();
        float bankBalance;

        if (amt > Constants.TRANSFER_AMOUNT_WITHOUT_CHARGE) {
            print(Constants.TRANSFER_AMT_EXCEEDS_THRESHOLD);
            print("");
            if (loggedInCustomer.getAccount().getBalance() - amt - Constants.TRANSACTION_NOMINAL_FEE < Constants.MINIMUM_BALANCE) {
                print(Constants.NOT_ENOUGH_MINIMUM_BALANCE);
                print(Constants.DISPLAY_ALL_POSSIBLE_OPERATIONS);
                return;
            }

            try {
                bankBalance = AccountOperations.withdraw(loggedInCustomer.getAccount(), Constants.TRANSACTION_NOMINAL_FEE);
                receiver.addTransaction(new Transaction("Nominal Fee", Constants.TRANSACTION_NOMINAL_FEE, bankBalance));
            } catch (NotEnoughBalanceException ne) {
                print(Constants.YOU_NEED + ne.getBalanceNeeded() + "\n" + Constants.BALANCE_INSUFFICIENT);
            } catch (MinimumBalanceViolatedException me) {
                print(me.getMessage());
            } finally {
                print(Constants.DISPLAY_ALL_POSSIBLE_OPERATIONS);
            }
        }

        try {
            bankBalance = AccountOperations.transfer(loggedInCustomer.getAccount(), receiverAcc, amt);
            print(Constants.CURRENT_BANK_BALANCE + bankBalance);
            loggedInCustomer.addTransaction(new Transaction("TransferTo" + receiver.getId(), amt, bankBalance));
            receiver.addTransaction(new Transaction("TransferFrom" + loggedInCustomer.getId(), amt, bankBalance));
        } catch (NotEnoughBalanceException ne) {
            print(Constants.YOU_NEED + ne.getBalanceNeeded() + "\n" + Constants.BALANCE_INSUFFICIENT);
        } catch (MinimumBalanceViolatedException me) {
            print(me.getMessage());
        } finally {
            print(Constants.DISPLAY_ALL_POSSIBLE_OPERATIONS);
        }

    }

    private static void changePasswordHelper(Customer loggedInCustomer) {
        boolean passwordIncorrect = true;
        String newPassword;
        do {
            print(Constants.ENTER_NEW_PASSWORD);
            newPassword = sc.next();

            if (loggedInCustomer.alreadyUsedPassword(newPassword)) {
                print(Constants.ALREADY_USED_PASSWORD);
                continue;
            }

            if (invalidPassword(newPassword)) {
                print(Constants.PASSWORD_CONSTRAINT_VIOLATION);
                continue;
            }

            print(Constants.CONFIRM_PASSWORD);
            String confirmPassword = sc.next();

            if (!confirmPassword.equals(newPassword)) {
                print(Constants.CONFIRM_PASSWORD_NOT_MATCH);
                continue;
            }

            passwordIncorrect = false;
        } while (passwordIncorrect);

        AccountOperations.changePassword(loggedInCustomer, newPassword);
        print(Constants.PASSWORD_CHANGED);
    }

    private static void getTopNCustomersHelper() {
        print(Constants.ENTER_NUMBER_OF_TOP_CUSTOMERS);

        int noOfCustomers = sc.nextInt();

        while (noOfCustomers > customers.size()) {
            print("We have only " + customers.size() + " customers. Please enter a value below " + customers.size());
            noOfCustomers = sc.nextInt();
        }

        PriorityQueue<Customer> minHeap = new PriorityQueue<>(new Comparator<Customer>() {
            @Override
            public int compare(Customer c1, Customer c2) {
                return (c1.getAccount().getBalance() < c2.getAccount().getBalance()) ? -1 : 1;
            }
        });

        for (Customer c : customers) {
            if (minHeap.size() < noOfCustomers) {
                minHeap.add(c);
            } else if ((minHeap.size() > 0 && minHeap.peek().getAccount().getBalance() < c.getAccount().getBalance())) {
                minHeap.poll();
                minHeap.add(c);
            }
        }

        Stack<Customer> topNCustomers = new Stack<>();

        while (!minHeap.isEmpty()) {
            topNCustomers.add(minHeap.poll());
        }

        while (!topNCustomers.empty()) {
            print(topNCustomers.pop().toString());
        }
        print("");
        print(Constants.DISPLAY_ALL_POSSIBLE_OPERATIONS);
    }

    private static void checkBalanceHelper(Customer loggedInCustomer) {
        print(Constants.YOUR_BALANCE + loggedInCustomer.getAccount().getBalance());
    }

    private static void printCustomerTransactions(Customer loggedInCustomer) {
        loggedInCustomer.printTransactions();
        print(Constants.DISPLAY_ALL_POSSIBLE_OPERATIONS);
    }

    private static boolean notInTop3Users(Customer loggedInCustomer) {
        final int NO_OF_CUSTOMERS = 3;

        PriorityQueue<Customer> minHeap = new PriorityQueue<>(new Comparator<Customer>() {
            @Override
            public int compare(Customer c1, Customer c2) {
                return (c1.getAccount().getBalance() < c2.getAccount().getBalance()) ? -1 : 1;
            }
        });

        for (Customer c : customers) {
            if (minHeap.size() < NO_OF_CUSTOMERS) {
                minHeap.add(c);
            } else if (minHeap.peek().getAccount().getBalance() < c.getAccount().getBalance()) {
                minHeap.poll();
                minHeap.add(c);
            }
        }

        while (!minHeap.isEmpty()) {
            //        FIXME: Printing Only for testing purpose.
            Customer c = minHeap.peek();
            System.out.println(c);

            if (minHeap.size() > 0 && minHeap.poll().getId() == loggedInCustomer.getId()) {
                return false;
            }

        }
        return true;
    }
}