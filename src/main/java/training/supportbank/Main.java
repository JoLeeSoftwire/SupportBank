package training.supportbank;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;

public class Main {


    public static void main(String args[]) throws IOException, ParseException {
        Path path = Paths.get(args[0]);
        String input = Files.readString(path);
        Bank bank = new Bank(input);
    }
}

class Bank {
    LinkedList<Transaction> transactions = new LinkedList<Transaction>();
    HashMap<String, Integer> accounts = new HashMap<String, Integer>();

    Bank(String input) throws ParseException {
        String[] transactionStrings = input.split("\n");
        for (int i = 1; i<transactionStrings.length; i++) {
            Transaction trans = new Transaction(transactionStrings[i]);
            this.transactions.add(trans);
            addToAccount(trans.to, trans.value);
            subFromAccount(trans.from, trans.value);
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
}

class Transaction {
    String from;
    String to;
    int value;
    String description;
    LocalDate date;

    Transaction(String transString) throws ParseException{
        String[] elements = transString.split(",");
        date = LocalDate.parse(elements[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        from = elements[1];
        to = elements[2];
        description = elements[3];
        value = (int) (Float.parseFloat(elements[4]) * 100);
    }
}
