package home.work.finance.service;

import home.work.finance.dto.BudgetData;
import home.work.finance.dto.Finances;
import home.work.finance.model.*;
import home.work.finance.repository.CategoryRepository;
import home.work.finance.repository.WalletRepository;
import home.work.finance.util.DataValidator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WalletService {
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;

    public WalletService(WalletRepository walletRepository, CategoryRepository categoryRepository) {
        this.walletRepository = walletRepository;
        this.categoryRepository = categoryRepository;
    }

    public void addWallet(User user, String walletName, double initialBalance) {
        checkWalletNotExists(walletName, user.getUsername());
        validateWalletName(walletName);
        validateBalance(initialBalance);

        walletRepository.saveWallet(new Wallet(user.getUsername(), walletName, initialBalance));
    }

    public void removeWallet(User user, String walletName) {
        checkWalletExists(walletName, user.getUsername());
        List<Wallet> wallets = walletRepository.loadWallets().stream().filter(wallet -> !(wallet.getUserId().equals(user.getUsername()) && wallet.getName().equals(walletName))).toList();

        walletRepository.saveWallets(wallets);
    }

    public void renameWallet(User user, String currentName, String newName) {
        checkWalletExists(currentName, user.getUsername());
        validateWalletName(newName);

        List<Wallet> wallets = walletRepository.loadWallets().stream().collect(ArrayList::new, (list, item) -> {
            if (item.getUserId().equals(user.getUsername()) && item.getName().equals(currentName)) {
                item.setName(newName);
            }
            list.add(item);
        }, ArrayList::addAll);

        walletRepository.saveWallets(wallets);
    }

    public void updateWalletBalance(User user, String walletName, double newBalance) {
        checkWalletExists(walletName, user.getUsername());
        validateBalance(newBalance);

        Wallet wallet = walletRepository.loadWalletsByNameAndUser(walletName, user.getUsername());
        wallet.setBalance(newBalance);

        walletRepository.saveWallet(wallet);
    }

    public void transferFunds(User senderUser, String senderWallet, User receiverUser, String receiverWallet, double amount) {
        if (!DataValidator.isPositiveNumber(String.valueOf(amount))) {
            throw new IllegalArgumentException("Сумма перевода должна быть положительной.");
        }

        Wallet sender = walletRepository.loadWalletsByNameAndUser(senderWallet, senderUser.getUsername());
        Wallet receiver = walletRepository.loadWalletsByNameAndUser(receiverWallet, receiverUser.getUsername());

        if (sender.getBalance() < amount) {
            throw new IllegalArgumentException("Недостаточно средств на кошельке отправителя.");
        }

        //TODO Добавить транзакции для фиксации перевода
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        walletRepository.saveWallet(sender);
        walletRepository.saveWallet(receiver);
    }

    public List<Wallet> listWallets(User user) {
        return walletRepository.loadWalletsByUser(user.getUsername());
    }

    public Finances calculateFinances(User user) {
        List<Wallet> wallets = walletRepository.loadWalletsByUser(user.getUsername());

        double totalIncome = 0;
        double totalExpenses = 0;

        for (Wallet wallet : wallets) {
            totalIncome += wallet.getTransactions().stream().filter(t -> t.getAmount() > 0).reduce(0.0, (w1, w2) -> w1 + w2.getAmount(), Double::sum);
            totalExpenses += wallet.getTransactions().stream().filter(t -> t.getAmount() < 0).reduce(0.0, (w1, w2) -> w1 + w2.getAmount(), Double::sum);
        }

        return new Finances(totalIncome, Math.abs(totalExpenses));
    }

    public List<BudgetData> displayBudgetData(User user) {
        List<Wallet> wallets = walletRepository.loadWalletsByUser(user.getUsername());
        List<BudgetData> result = new ArrayList<>();
        for (Wallet wallet : wallets) {
            result.add(new BudgetData(wallet.getName(), wallet.getBalance(), wallet.getTransactions()));
        }
        return result;
    }

    public boolean checkExpenseExceedsIncome(User user) {
        double totalIncome = 0;
        double totalExpenses = 0;

        List<Wallet> wallets = walletRepository.loadWalletsByUser(user.getUsername());
        for (Wallet wallet : wallets) {
            totalIncome += wallet.getTransactions().stream().filter(t -> t.getAmount() > 0).reduce(0.0, (w1, w2) -> w1 + w2.getAmount(), Double::sum);
            totalExpenses += wallet.getTransactions().stream().filter(t -> t.getAmount() < 0).reduce(0.0, (w1, w2) -> w1 + w2.getAmount(), Double::sum);
        }

        return Math.abs(totalExpenses) > totalIncome;
    }

    public void addTransaction(User user, String walletName, double amount, String categoryName, boolean isIncome) {
        Wallet targetWallet = walletRepository.loadWalletsByNameAndUser(walletName, user.getUsername());

        Category category = categoryRepository.findCategoryByUserIdAndName(user.getUsername(), categoryName);

        if (category == null) {
            throw new IllegalArgumentException("Категория с названием \"" + categoryName + "\" не найдена.");
        }

        double adjustedAmount = isIncome ? amount : -amount;
        Transaction transaction = new Transaction(adjustedAmount, category, LocalDate.now());
        targetWallet.addTransaction(transaction);

        walletRepository.saveWallet(targetWallet);
    }

    public void deleteTransaction(User user, String walletName, String transactionId) {
        List<Wallet> wallets = walletRepository.loadWalletsByUser(user.getUsername());
        for (Wallet wallet : wallets) {
            if (wallet.getName().equals(walletName)) {
                Transaction transaction = wallet.findTransactionById(transactionId);
                if (transaction != null) {
                    wallet.removeTransaction(transaction);
                    walletRepository.saveWallet(wallet);
                    return;
                }
            }
        }
    }

    public void editTransaction(User user, String walletName, String transactionId, double newAmount, String newCategoryName, String newDateStr) {
        if (!DataValidator.isValidDate(newDateStr, "yyyy-MM-dd")) {
            throw new IllegalArgumentException("Дата \"" + newDateStr + "\" имеет неверный формат. Ожидается формат yyyy-MM-dd.");
        }

        List<Wallet> wallets = walletRepository.loadWalletsByUser(user.getUsername());
        for (Wallet wallet : wallets) {
            if (wallet.getName().equals(walletName)) {
                Transaction transaction = wallet.findTransactionById(transactionId);
                if (transaction != null) {
                    Category newCategory = categoryRepository.findCategoryByUserIdAndName(user.getUsername(), newCategoryName);

                    if (newCategory == null) {
                        throw new IllegalArgumentException("Категория с названием \"" + newCategoryName + "\" не найдена.");
                    }

                    transaction.setAmount(newAmount);
                    transaction.setCategory(newCategory);
                    transaction.setDate(LocalDate.parse(newDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                    walletRepository.saveWallet(wallet);
                    return;
                }
            }
        }
    }

    public List<Transaction> listTransactions(User user, String walletName) {
        return walletRepository.loadWalletsByUser(user.getUsername()).stream().filter(item -> item.getName().equals(walletName)).findFirst().orElseThrow(() -> new RuntimeException("Нет такого кошелька")).getTransactions();
    }

    private void checkWalletExists(String walletName, String userId) {
        if (walletRepository.loadWallets().stream().noneMatch(wallet -> wallet.getName().equals(walletName) && wallet.getUserId().equals(userId))) {
            throw new IllegalArgumentException("Кошелек с именем " + walletName + " не существует");
        }
    }

    private void checkWalletNotExists(String walletName, String userId) {
        if (walletRepository.loadWallets().stream().anyMatch(wallet -> wallet.getName().equals(walletName) && wallet.getUserId().equals(userId))) {
            throw new IllegalArgumentException("Кошелек с именем " + walletName + " существует");
        }
    }

    private void validateWalletName(String walletName) {
        if (!DataValidator.isNonEmptyString(walletName) || !DataValidator.isStringLengthValid(walletName, 50)) {
            throw new IllegalArgumentException("Некорректное название кошелька.");
        }
    }

    private void validateBalance(double balance) {
        if (!DataValidator.isPositiveNumber(String.valueOf(balance)) || !DataValidator.isNumberInRange(balance, 0, 100_000_000)) {
            throw new IllegalArgumentException("Некорректный баланс.");
        }
    }
}
