package training.supportbank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Main {
    private static final Logger LOGGER = LogManager.getLogger();
    public static void main(String args[]) throws IOException, ParseException {
        Bank bank = new Bank();
        
        bank.transactionsFromFile("transactions/Transactions2014.csv");
        bank.transactionsFromFile("transactions/DodgyTransactions2015.csv");

        Scanner scanner = new Scanner(System.in);
        LOGGER.info("Transactions parsed!");
        boolean cont = true;
        while(cont) {
            System.out.println("\nWhat would you like to do?");
            String command = scanner.nextLine();
            if(command.toLowerCase().equals("exit")) {
                cont = false;
            } else if(!command.toLowerCase().startsWith("List ".toLowerCase())) {
                LOGGER.warn("unrecognised command: "+command);
                System.out.println("I don't recognise that command, please either type 'List All' or 'List [Account]'");
            } else {
                String instruction = command.substring("List ".length());
                if(instruction.toLowerCase().equals("All".toLowerCase())) {
                    bank.printAccounts();
                } else if(bank.accounts.containsKey(instruction)) {
                    bank.showAccount(instruction);
                } else {
                    System.out.println("Sorry, there is no account with that name");
                }
            }
        }
        scanner.close();
    }
}

class Bank {
    private static final Logger LOGGER = LogManager.getLogger();
    
    LinkedList<Transaction> transactions = new LinkedList<Transaction>();
    HashMap<String, Integer> accounts = new HashMap<String, Integer>();

    void addTransaction(String input) {
        try {
            Transaction trans = new Transaction(input);
            this.transactions.add(trans);
            addToAccount(trans.to, trans.value);
            subFromAccount(trans.from, trans.value);
        } catch (NumberFormatException e) {
            LOGGER.warn("could not parse transaction, invalid value, skipping");
        } catch (DateTimeParseException e) {
            LOGGER.warn("could not parse transaction, invalid date, skipping");
        }
    }

    void transactionsFromFile(String path) throws IOException {
        Path file = Paths.get(path);
        String input = Files.readString(file);
        String[] transactionStrings = input.split("\n");
        for (int i = 1; i<transactionStrings.length; i++) {
            addTransaction(transactionStrings[i]);                
        }
    }

    void addToAccount(String to, int val) {
        if(accounts.containsKey(to)) {
            accounts.put(to, accounts.get(to) + val);
        } else {
            accounts.put(to, val);
        }
    }

    void subFromAccount(String to, int val) {
        if(accounts.containsKey(to)) {
            accounts.put(to, accounts.get(to) - val);
        } else {
            accounts.put(to, 0 - val);
        }
    }

    void showAccount(String name) {
        System.out.println("Transactions for account "+name+": ");
        System.out.println("Date\t\t| Value\t| Who\t\t| Description");
        System.out.println("-----------------------------------------------------------------");
        for(Transaction t:this.transactions) {
            t.transactionSummary(name);
        }
    }

    void printAccounts() {
        for(String i:this.accounts.keySet()) {
            float balance = this.accounts.get(i);
            System.out.print(i);
            System.out.print(": ");
            System.out.println(balance/100);
        }
    }
}

class Transaction {
    String from;
    String to;
    int value;
    String description;
    LocalDate date;

    Transaction(String transString) {
        String[] elements = transString.split(",");
        date = LocalDate.parse(elements[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        from = elements[1];
        to = elements[2];
        description = elements[3];
        value = (int) (Float.parseFloat(elements[4]) * 100);
    }

    void transactionSummary(String name) {
        if(this.from.equals(name)) {
            System.out.print(this.date);
            System.out.print("\t| ");
            System.out.print((float)this.value/100);
            System.out.print("\t| ");
            System.out.print(this.to);
            System.out.print("\t| ");
            System.out.println(this.description);
        }
        if(this.to.equals(name)) {
            System.out.print(this.date);
            System.out.print("\t| ");
            System.out.print((float)this.value/-100);
            System.out.print("\t| ");
            System.out.print(this.from);
            System.out.print("\t| ");
            System.out.println(this.description);
        }
    }
}
