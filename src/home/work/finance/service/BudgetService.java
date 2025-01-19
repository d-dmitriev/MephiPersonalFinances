package home.work.finance.service;

import home.work.finance.dto.Budget;
import home.work.finance.model.*;
import home.work.finance.repository.CategoryRepository;
import home.work.finance.repository.WalletRepository;
import home.work.finance.util.DataValidator;

import java.util.*;

public class BudgetService {
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;

    public BudgetService(WalletRepository walletRepository, CategoryRepository categoryRepository) {
        this.walletRepository = walletRepository;
        this.categoryRepository = categoryRepository;
    }

    public void addCategory(User user, String categoryName, double budgetLimit) {
        validateCategoryName(categoryName);
        validateBudgetLimit(budgetLimit);

        Category existingCategory = categoryRepository.findCategoryByUserIdAndName(user.getUsername(), categoryName);

        if (existingCategory != null) {
            throw new IllegalArgumentException("Категория с таким названием уже существует.");
        }

        Category newCategory = new Category(user.getUsername(), categoryName, budgetLimit);
        List<Category> categories = categoryRepository.loadCategories();
        categories.add(newCategory);
        categoryRepository.saveCategories(categories);
    }

    public void renameCategory(User user, String currentName, String newName) {
        if (categoryRepository.loadCategories().stream().noneMatch(item -> item.getName().equals(currentName) && item.getUserId().equals(user.getUsername()))) {
            throw new IllegalArgumentException("Категория с названием \"" + currentName + "\" не найдена.");
        }
        validateCategoryName(newName);

        List<Category> categories = categoryRepository.loadCategories().stream().collect(ArrayList::new, (list, item) -> {
            if (item.getUserId().equals(user.getUsername()) && item.getName().equals(currentName)) {
                item.setName(newName);
            }
            list.add(item);
        }, ArrayList::addAll);
        categoryRepository.saveCategories(categories);
    }

    public void updateBudgetLimit(User user, String categoryName, double newLimit) {
        if (categoryRepository.loadCategories().stream().noneMatch(item -> item.getName().equals(categoryName) && item.getUserId().equals(user.getUsername()))) {
            throw new IllegalArgumentException("Категория с названием \"" + categoryName + "\" не найдена.");
        }
        validateCategoryName(categoryName);
        validateBudgetLimit(newLimit);

        List<Category> categories = categoryRepository.loadCategories().stream().collect(ArrayList::new, (list, item) -> {
            if (item.getUserId().equals(user.getUsername()) && item.getName().equals(categoryName)) {
                item.setBudgetLimit(newLimit);
            }
            list.add(item);
        }, ArrayList::addAll);

        categoryRepository.saveCategories(categories);
    }

    public List<Category> listCategories(User user) {
        return categoryRepository.findCategoriesByUserId(user.getUsername());
    }

    public List<Budget> calculateBudgetState(User user) {
        Map<String, Double> expensesByCategory = new HashMap<>();
        List<Category> categories = categoryRepository.findCategoriesByUserId(user.getUsername());

        List<Transaction> transactions = walletRepository.loadWalletsByUser(user.getUsername()).stream().collect(ArrayList::new, (list, item) -> {
            list.addAll(item.getTransactions());
        }, ArrayList::addAll);

        for (Transaction transaction : transactions) {
            String categoryName = transaction.getCategory().getName();
            if (transaction.getAmount() < 0) {
                expensesByCategory.put(categoryName,
                        expensesByCategory.getOrDefault(categoryName, 0.0) + transaction.getAmount());
            }
        }

        List<Budget> result = new ArrayList<>();

        for (Category category : categories) {
            if (category.getBudgetLimit() > 0) {
                double expenses = Math.abs(expensesByCategory.getOrDefault(category.getName(), 0.0));
                double remainingBudget = category.getBudgetLimit() - expenses;

                result.add(new Budget(category.getName(), category.getBudgetLimit(), expenses, remainingBudget));
            }
        }

        return result;
    }

    public Set<String> checkBudgetLimits(User user) {
        List<Category> categories = categoryRepository.findCategoriesByUserId(user.getUsername());

        Map<String, Double> expensesByCategory = calculateExpensesByCategory(user);

        Set<String> warnings = new HashSet<>();
        for (Category category : categories) {
            double expenses = Math.abs(expensesByCategory.getOrDefault(category.getName(), 0.0));
            if (expenses > category.getBudgetLimit()) {
                warnings.add(category.getName());
            }
        }
        return warnings;
    }

    private void validateCategoryName(String categoryName) {
        if (!DataValidator.isNonEmptyString(categoryName) ||
                !DataValidator.isStringLengthValid(categoryName, 30)) {
            throw new IllegalArgumentException("Некорректное название категории.");
        }
    }

    private void validateBudgetLimit(double budgetLimit) {
        if (!DataValidator.isNumberInRange(budgetLimit, 0, 100_000_000)) {
            throw new IllegalArgumentException("Некорректный лимит бюджета.");
        }
    }

    private Map<String, Double> calculateExpensesByCategory(User user) {
        Map<String, Double> expensesByCategory = new HashMap<>();

        List<Transaction> transactions = walletRepository.loadWalletsByUser(user.getUsername()).stream().collect(ArrayList::new, (list, item) -> {
            list.addAll(item.getTransactions());
        }, ArrayList::addAll);

        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                String categoryName = transaction.getCategory().getName();
                expensesByCategory.put(categoryName,
                        expensesByCategory.getOrDefault(categoryName, 0.0) + transaction.getAmount());
            }
        }
        return expensesByCategory;
    }
}