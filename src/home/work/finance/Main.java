package home.work.finance;

import home.work.finance.repository.CategoryRepositoryImpl;
import home.work.finance.repository.UserRepositoryImpl;
import home.work.finance.repository.WalletRepositoryImpl;
import home.work.finance.service.*;

import static home.work.finance.ui.Menu.loginMenu;

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepositoryImpl();
        WalletRepository walletRepository = new WalletRepositoryImpl();
        CategoryRepository categoryRepository = new CategoryRepositoryImpl();

        UserService userService = new UserService(userRepository, walletRepository, categoryRepository);
        WalletService walletService = new WalletService(walletRepository, categoryRepository);
        BudgetService budgetService = new BudgetService(walletRepository, categoryRepository);

        loginMenu(userService, walletService, budgetService);
    }
}