package home.work.finance.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Wallet {
    private String userId;
    private String name;
    private double balance;
    private final List<Transaction> transactions;

    @JsonCreator
    public Wallet(@JsonProperty("userId") String userId,
                  @JsonProperty("name") String name,
                  @JsonProperty("balance") double balance,
                  @JsonProperty("transactions") List<Transaction> transactions) {
        this.userId = userId;
        this.name = name;
        this.balance = balance;
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }

    public Wallet(String userId, String name, double balance) {
        this(userId, name, balance, new ArrayList<>());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        balance += transaction.getAmount();
    }

    public void removeTransaction(Transaction transaction) {
        if (transactions.remove(transaction)) {
            balance -= transaction.getAmount();
        }
    }

    public Transaction findTransactionById(String id) {
        for (Transaction transaction : transactions) {
            if (transaction.getId().equals(id)) {
                return transaction;
            }
        }
        return null;
    }

    public String toString() {
        return "Wallet{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                ", transactions=" + transactions.size() +
                '}';
    }
}