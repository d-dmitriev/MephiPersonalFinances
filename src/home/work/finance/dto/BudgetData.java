package home.work.finance.dto;

import home.work.finance.model.Transaction;

import java.util.List;

public record BudgetData(String wallet, double budget, List<Transaction> transactions) {
}
