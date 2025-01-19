package home.work.finance.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.UUID;

public class Transaction {
    private final String id;
    private double amount;
    private String category;
    private LocalDate date;

    @JsonCreator
    public Transaction(@JsonProperty("id") String id,
                       @JsonProperty("amount") double amount,
                       @JsonProperty("category") String category,
                       @JsonProperty("date") LocalDate date) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public Transaction(double amount, String category, LocalDate date) {
        this(UUID.randomUUID().toString(), amount, category, date);
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", category=" + category +
                ", date=" + date +
                '}';
    }
}