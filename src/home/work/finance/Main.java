package home.work.finance;

import home.work.finance.dto.Finances;
import home.work.finance.model.User;
import home.work.finance.repository.CategoryRepository;
import home.work.finance.repository.UserRepository;
import home.work.finance.repository.WalletRepository;
import home.work.finance.service.BudgetService;
import home.work.finance.service.UserService;
import home.work.finance.service.WalletService;
import home.work.finance.util.DataValidator;

import java.util.Scanner;
import java.util.Set;

import static home.work.finance.ui.Console.*;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        WalletRepository walletRepository = new WalletRepository();
        CategoryRepository categoryRepository = new CategoryRepository();

        UserService userService = new UserService(userRepository, walletRepository, categoryRepository);
        WalletService walletService = new WalletService(walletRepository, categoryRepository);
        BudgetService budgetService = new BudgetService(walletRepository, categoryRepository);

        loginMenu(scanner, userService, walletService, budgetService);
    }

    private static void loginMenu(Scanner scanner, UserService userService,
                                  WalletService walletService, BudgetService budgetService) {
        while (true) {
            printHeader("Добро пожаловать в приложение \"Личные финансы\"!");
            printMenu("""
                    1. Войти
                    2. Зарегистрироваться
                    3. Выйти
                    """);

            try {
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> {
                        printInput("Введите логин: ");
                        String username = scanner.nextLine();
                        printInput("Введите пароль: ");
                        String password = scanner.nextLine();

                        userService.authenticateUser(username, password);
                        User currentUser = userService.getCurrentUser();
                        if (currentUser != null) {
                            printLine("Добро пожаловать, " + currentUser.getUsername() + "!");
                            mainMenu(scanner, currentUser, walletService, budgetService, userService);
                        } else {
                            printLineError("Авторизация не выполнена. Проверьте логин и пароль.");
                        }
                    }
                    case "2" -> {
                        try {
                            printInput("Введите логин: ");
                            String username = scanner.nextLine();
                            printInput("Введите пароль: ");
                            String password = scanner.nextLine();

                            userService.registerUser(username, password);
                            printLine("Регистрация выполнена");
                        } catch (IllegalArgumentException e) {
                            printLineError(e.getMessage());
                        }
                    }
                    case "3" -> {
                        printLine("Спасибо за использование приложения \"Личные финансы\"! До свидания!");
                        return;
                    }
                    default -> printLineError("Неверный выбор. Попробуйте снова.");
                }
            } catch (Exception e) {
                printLineError(e.getMessage());
            }
        }
    }

    private static void mainMenu(Scanner scanner, User currentUser,
                                 WalletService walletService, BudgetService budgetService, UserService userService) {
        while (true) {
            printHeader("Главное меню:");
            printMenu("""
                    1. Управление кошельками
                    2. Управление категориями
                    3. Управление транзакциями
                    4. Управление аккаунтом
                    5. Выйти из аккаунта
                    """);

            try {
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> walletMenu(walletService, userService, currentUser);
                    case "2" -> budgetsMenu(budgetService, currentUser);
                    case "3" -> transactionsMenu(walletService, budgetService, currentUser);
                    case "4" -> usersMenu(userService);
                    case "5" -> {
                        printLine("Выход из аккаунта...");
                        return;
                    }
                    default -> printLineError("Неверный выбор. Попробуйте снова.");
                }
            } catch (Exception e) {
                printLineError(e.getMessage());
            }
        }
    }

    private static void budgetsMenu(BudgetService budgetService, User currentUser) {
        while (true) {
            printHeader("Управление категориями:");
            printMenu("""
                    1. Добавить категорию
                    2. Переименовать категорию
                    3. Обновить лимит категории
                    4. Просмотреть список категорий
                    5. Подсчитать состояние бюджета по категориям
                    6. Вернуться в главное меню
                    """);

            try {
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> {
                        try {
                            printInput("Введите название категории: ");
                            String categoryName = scanner.nextLine();
                            printInput("Введите лимит бюджета (от 0 до 100_000_000): ");
                            double budgetLimit = Double.parseDouble(scanner.nextLine());
                            budgetService.addCategory(currentUser, categoryName, budgetLimit);
                            printLine("Категория добавлена.");
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Введите корректное число для лимита бюджета.");
                        }
                    }
                    case "2" -> {
                        printInput("Введите текущее название категории: ");
                        String currentName = scanner.nextLine();
                        printInput("Введите новое название категории: ");
                        String newName = scanner.nextLine();
                        budgetService.renameCategory(currentUser, currentName, newName);
                        printLine("Категория переименована.");
                    }
                    case "3" -> {
                        try {
                            printInput("Введите название категории: ");
                            String categoryName = scanner.nextLine();
                            printInput("Введите новый лимит бюджета: ");
                            double newLimit = Double.parseDouble(scanner.nextLine());

                            budgetService.updateBudgetLimit(currentUser, categoryName, newLimit);
                            printLine("Лимит обновлен.");
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Введите корректное число для нового лимита.");
                        }
                    }
                    case "4" -> {
                        if (budgetService.listCategories(currentUser).isEmpty()) {
                            printLine("Нет категорий.");
                        } else {
                            printLine("Ваши категории:");
                            budgetService.listCategories(currentUser).forEach(category ->
                                    printLine("- " + category.getName() +
                                            (category.getBudgetLimit() > 0 ? " (Лимит: " + category.getBudgetLimit() + ")" : "")));
                        }
                    }
                    case "5" -> {
                        budgetService.calculateBudgetState(currentUser).forEach(budget ->
                                printLine("- " + budget.name() +
                                        ": Лимит: " + budget.limit() +
                                        ", Расходы: " + budget.expenses() +
                                        ", Остаток: " + budget.remaining())
                        );
                    }
                    case "6" -> {
                        printLine("Выход в главное меню.");
                        return;
                    }
                    default -> printLineError("Неверный выбор. Попробуйте снова.");
                }
            } catch (Exception e) {
                printLineError(e.getMessage());
            }
        }
    }

    private static void transactionsMenu(WalletService walletService, BudgetService budgetService, User currentUser) {
        while (true) {
            printHeader("Управление транзакциями:");
            printMenu("""
                    1. Добавить доход
                    2. Добавить расход
                    3. Просмотреть транзакции
                    4. Удалить транзакцию
                    5. Редактировать транзакцию
                    6. Вернуться в главное меню
                    """);

            try {
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> {
                        try {
                            printInput("Введите название кошелька: ");
                            String walletName = scanner.nextLine();
                            printInput("Введите сумму: ");
                            double amount = Double.parseDouble(scanner.nextLine());
                            printInput("Введите категорию: ");
                            String categoryName = scanner.nextLine();

                            walletService.addTransaction(currentUser, walletName, amount, categoryName, true);
                            checkTransaction(walletService, budgetService, currentUser);
                            printLine("Доход добавлен.");
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Введите корректное число для суммы.");
                        }
                    }
                    case "2" -> {
                        try {
                            printInput("Введите название кошелька: ");
                            String walletName = scanner.nextLine();
                            printInput("Введите сумму: ");
                            double amount = Double.parseDouble(scanner.nextLine());
                            printInput("Введите категорию: ");
                            String categoryName = scanner.nextLine();

                            walletService.addTransaction(currentUser, walletName, amount, categoryName, false);
                            checkTransaction(walletService, budgetService, currentUser);
                            printLine("Расход добавлен.");
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Введите корректное число для суммы.");
                        }
                    }
                    case "3" -> {
                        printInput("Введите название кошелька: ");
                        String walletName = scanner.nextLine();

                        if (walletService.listTransactions(currentUser, walletName).isEmpty()) {
                            printLine("Нет транзакций");
                        } else {
                            printLine("Список транзакций:");
                            walletService.listTransactions(currentUser, walletName).forEach(transaction ->
                                    printFormatedLine("  - Дата: %s, Сумма: %.2f, Категория: %s, ID: %s\n",
                                            transaction.getDate(),
                                            transaction.getAmount(),
                                            transaction.getCategory().getName(),
                                            transaction.getId())
                            );
                        }
                    }
                    case "4" -> {
                        printInput("Введите название кошелька: ");
                        String walletName = scanner.nextLine();
                        printInput("Введите ID транзакции для удаления: ");
                        String transactionId = scanner.nextLine();

                        walletService.deleteTransaction(currentUser, walletName, transactionId);
                        printLine("Транзакция удалена.");
                    }
                    case "5" -> {
                        try {
                            printInput("Введите название кошелька: ");
                            String walletName = scanner.nextLine();
                            printInput("Введите ID транзакции для редактирования: ");
                            String transactionId = scanner.nextLine();
                            printInput("Введите новую сумму транзакции: ");
                            double newAmount = Double.parseDouble(scanner.nextLine());
                            printInput("Введите новую категорию: ");
                            String newCategory = scanner.nextLine();
                            printInput("Введите новую дату транзакции (yyyy-MM-dd): ");
                            String newDateStr = scanner.nextLine();

                            walletService.editTransaction(currentUser, walletName, transactionId, newAmount, newCategory, newDateStr);
                            checkTransaction(walletService, budgetService, currentUser);
                            printLine("Транзакция отредактирована.");
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Введите корректное число для суммы.");
                        }
                    }
                    case "6" -> {
                        printLine("Выход в главное меню.");
                        return;
                    }
                    default -> printLineError("Неверный выбор. Попробуйте снова.");
                }
            } catch (Exception e) {
                printLineError(e.getMessage());
            }
        }
    }

    public static void usersMenu(UserService userService) {
        while (true) {
            printHeader("Управление аккаунтом:");
            printMenu("""
                    1. Изменить логин
                    2. Изменить пароль
                    3. Вернуться в главное меню
                    """);

            try {
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> {
                        printInput("Введите новый логин: ");
                        String newUsername = scanner.nextLine();

                        userService.changeUsername(newUsername);
                        printLine("Логин изменен.");
                    }
                    case "2" -> {
                        printInput("Введите старый пароль: ");
                        String oldPassword = scanner.nextLine();
                        printInput("Введите новый пароль: ");
                        String newPassword = scanner.nextLine();

                        userService.changePassword(oldPassword, newPassword);
                        printLine("Пароль изменен.");
                    }
                    case "3" -> {
                        printLine("Возврат в главное меню.");
                        return;
                    }
                    default -> printLineError("Неверный выбор. Попробуйте снова.");
                }
            } catch (Exception e) {
                printLineError(e.getMessage());
            }
        }
    }

    public static void walletMenu(WalletService walletService, UserService userService, User currentUser) {
        while (true) {
            printHeader("Управление кошельками:");
            printMenu("""
                    1. Добавить кошелёк
                    2. Удалить кошелёк
                    3. Переименовать кошелёк
                    4. Обновить баланс кошелка
                    5. Просмотреть список кошельков
                    6. Подсчитать доходы и расходы
                    7. Вывести данные по кошелькам и бюджету
                    8. Перевести средства между кошельками
                    9. Вернуться в главное меню
                    """);

            try {
                String choice = scanner.nextLine();
                switch (choice) {
                    case "1" -> {
                        try {
                            printInput("Введите название кошелька: ");
                            String walletName = scanner.nextLine();
                            printInput("Введите начальный баланс: ");
                            double balance = Double.parseDouble(scanner.nextLine());

                            walletService.addWallet(currentUser, walletName, balance);
                            printLine("Кошелёк успешно добавлен.");
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Введите корректное число для баланса.");
                        }
                    }
                    case "2" -> {
                        printInput("Введите название кошелька для удаления: ");
                        String walletName = scanner.nextLine();

                        walletService.removeWallet(currentUser, walletName);
                        printLine("Кошелёк успешно удалён.");
                    }
                    case "3" -> {
                        printInput("Введите текущее название кошелька: ");
                        String currentName = scanner.nextLine();
                        printInput("Введите новое название кошелька: ");
                        String newName = scanner.nextLine();

                        walletService.renameWallet(currentUser, currentName, newName);
                        printLine("Название кошелька успешно изменено.");
                    }
                    case "4" -> {
                        try {
                            printInput("Введите название кошелька: ");
                            String walletName = scanner.nextLine();
                            printInput("Введите новый баланс: ");
                            double newBalance = Double.parseDouble(scanner.nextLine());

                            walletService.updateWalletBalance(currentUser, walletName, newBalance);
                            printLine("Баланс кошелька успешно обновлён.");
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Введите корректное число.");
                        }
                    }
                    case "5" -> {
                        if (walletService.listWallets(currentUser).isEmpty()) {
                            printLine("Нет кошельков");
                        } else {
                            printLine("Список кошельков:");
                            walletService.listWallets(currentUser).forEach(wallet -> printLine("- " + wallet.getName() + " (Баланс: " + wallet.getBalance() + ")"));
                        }
                    }
                    case "6" -> {
                        Finances result = walletService.calculateFinances(currentUser);
                        printLine("Общий доход: " + result.totalIncome());
                        printLine("Общие расходы: " + result.totalExpenses());
                    }
                    case "7" -> {
                        if (walletService.displayBudgetData(currentUser).isEmpty()) {
                            printLine("Нет информации.");
                        } else {
                            walletService.displayBudgetData(currentUser).forEach(budgetData -> {
                                printLine("Кошелёк: " + budgetData.wallet());
                                printFormatedLine("Баланс: %.2f\n", budgetData.budget());
                                if (budgetData.transactions().isEmpty()) {
                                    printLine("Нет транзакций.");
                                } else {
                                    printLine("Транзакции:");
                                    budgetData.transactions().forEach(transaction -> printFormatedLine("  - Дата: %s, Сумма: %.2f, Категория: %s\n",
                                            transaction.getDate(), transaction.getAmount(), transaction.getCategory().getName()));
                                }
                            });
                        }
                    }
                    case "8" -> {
                        printInput("Введите название вашего кошелька: ");
                        String senderWallet = scanner.nextLine();
                        printInput("Введите логин получателя: ");
                        String receiverUsername = scanner.nextLine();
                        printInput("Введите название кошелька получателя: ");
                        String receiverWallet = scanner.nextLine();
                        printInput("Введите сумму перевода: ");
                        String amountInput = scanner.nextLine();

                        if (!DataValidator.isNumeric(amountInput) || !DataValidator.isPositiveNumber(amountInput)) {
                            printLineError("Введите положительное число для суммы.");
                            return;
                        }
                        double amount = Double.parseDouble(amountInput);

                        User receiverUser = userService.findUserByUsername(receiverUsername);
                        if (receiverUser == null) {
                            printLineError("Пользователь с логином \"" + receiverUsername + "\" не найден.");
                            return;
                        }
                        walletService.transferFunds(currentUser, senderWallet, receiverUser, receiverWallet, amount);
                        printLine("Средства переведены.");
                    }
                    case "9" -> {
                        printLine("Выход в главное меню.");
                        return;
                    }
                    default -> printLineError("Неверный выбор. Попробуйте снова.");
                }
            } catch (Exception e) {
                printLineError(e.getMessage());
            }
        }
    }

    private static void checkTransaction(WalletService walletService, BudgetService budgetService, User currentUser) {
        Set<String> warnings = budgetService.checkBudgetLimits(currentUser);
        if (!warnings.isEmpty()) {
            warnings.forEach(w -> printLineWarning("Лимит превышен для категории: " + w));
        }

        if (walletService.checkExpenseExceedsIncome(currentUser)) {
            printLineWarning("Расходы превышают доходы.");
        }
    }
}