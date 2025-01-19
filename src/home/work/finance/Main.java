package home.work.finance;

import home.work.finance.repository.CategoryRepository;
import home.work.finance.repository.UserRepository;
import home.work.finance.repository.WalletRepository;
import home.work.finance.service.BudgetService;
import home.work.finance.service.UserService;
import home.work.finance.service.WalletService;

import static home.work.finance.ui.Menu.loginMenu;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        WalletRepository walletRepository = new WalletRepository();
        CategoryRepository categoryRepository = new CategoryRepository();

        UserService userService = new UserService(userRepository, walletRepository, categoryRepository);
        WalletService walletService = new WalletService(walletRepository, categoryRepository);
        BudgetService budgetService = new BudgetService(walletRepository, categoryRepository);

        loginMenu(userService, walletService, budgetService);
    }
}