package home.work.finance;

import home.work.finance.dto.Finances;
import home.work.finance.model.User;
import home.work.finance.repository.CategoryRepositoryImpl;
import home.work.finance.repository.WalletRepositoryImpl;
import home.work.finance.service.BudgetService;
import home.work.finance.service.CategoryRepository;
import home.work.finance.service.WalletRepository;
import home.work.finance.service.WalletService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletServiceTests {
    private static final CategoryRepository categoryRepository = new CategoryRepositoryImpl();
    private static final WalletRepository walletRepository = new WalletRepositoryImpl();
    private static final BudgetService budgetService = new BudgetService(walletRepository, categoryRepository);
    private static final WalletService walletService = new WalletService(walletRepository, categoryRepository);

    @Test
    void addWallet() {
        String walletName = "good_wallet";
        User user = new User("user1", "");

        walletService.addWallet(user, walletName, 1);
        assertEquals(walletName, walletRepository.loadWalletsByNameAndUser(walletName, user.getUsername()).getName());
        walletService.removeWallet(user, walletName);
    }

    @Test
    void addWalletError() {
        assertThrows(IllegalArgumentException.class, () -> walletService.addWallet(new User("user1", "12345678"), "", 0));
        assertThrows(IllegalArgumentException.class, () -> walletService.addWallet(new User("user1", "12345678"), "wallet1234wallet1234wallet1234wallet1234wallet1234wallet1234", 0));
//        assertThrows(IllegalArgumentException.class, () -> walletService.addWallet(new User("user1", "12345678"), "wallet1", 1));
        assertThrows(IllegalArgumentException.class, () -> walletService.addWallet(new User("user1", "12345678"), "wallet2", -1));
        assertThrows(IllegalArgumentException.class, () -> walletService.addWallet(new User("user1", "12345678"), "wallet2", 100_000_001));
    }

    @Test
    void removeWallet() {
        String walletName = "wallet2";
        User user = new User("user2", "");
        walletService.addWallet(user, walletName, 1);
        assertEquals(1, walletService.listWallets(user).size());
        walletService.removeWallet(user, walletName);
        assertEquals(0, walletService.listWallets(user).size());
    }

    @Test
    void removeWalletError() {
        User user = new User("user2", "");
        assertThrows(IllegalArgumentException.class, () -> walletService.removeWallet(user, "bad_wallet"));
    }

    @Test
    void renameWallet() {
        String oldName = "old_name";
        String newName = "new_name";
        User user = new User("user2", "");
        walletService.addWallet(user, oldName, 10);
        assertEquals(oldName, walletRepository.loadWalletsByNameAndUser(oldName, user.getUsername()).getName());
        walletService.renameWallet(user, oldName, newName);
        assertEquals(newName, walletRepository.loadWalletsByNameAndUser(newName, user.getUsername()).getName());
//        assertThrows(NoSuchElementException.class, () -> walletRepository.loadWalletsByNameAndUser(oldName, user.getUsername()));
        walletService.removeWallet(user, newName);
    }

    @Test
    void updateWalletBalance() {
        String walletName = "wallet_balance";
        double oldBalance = 10;
        double newBalance = 20;
        User user = new User("user2", "");
        walletService.addWallet(user, walletName, oldBalance);
        assertEquals(oldBalance, walletRepository.loadWalletsByNameAndUser(walletName, user.getUsername()).getBalance());
        walletService.updateWalletBalance(user, walletName, newBalance);
        assertEquals(newBalance, walletRepository.loadWalletsByNameAndUser(walletName, user.getUsername()).getBalance());
        walletService.removeWallet(user, walletName);
    }

    @Test
    void transferFunds() {
        String walletName = "wallet_transfer";
        User fromUser = new User("from_user", "");
        User toUser = new User("to_user", "");
        walletService.addWallet(fromUser, walletName, 200);
        walletService.addWallet(toUser, walletName, 100);
        assertEquals(200, walletRepository.loadWalletsByNameAndUser(walletName, fromUser.getUsername()).getBalance());
        assertEquals(100, walletRepository.loadWalletsByNameAndUser(walletName, toUser.getUsername()).getBalance());
        walletService.transferFunds(fromUser, walletName, toUser, walletName, 100);
        assertEquals(100, walletRepository.loadWalletsByNameAndUser(walletName, fromUser.getUsername()).getBalance());
        assertEquals(200, walletRepository.loadWalletsByNameAndUser(walletName, toUser.getUsername()).getBalance());
        walletService.removeWallet(fromUser, walletName);
        walletService.removeWallet(toUser, walletName);
    }

    @Test
    void listWallets() {
        User user = new User("user3", "");
        assertEquals(0, walletService.listWallets(user).size());
    }

    @Test
    void calculateFinances() {
        User user = new User("wallet_finances_user", "");
        String wallet = "wallet_finances";
        String category = "wallet_finances_test";
        budgetService.addCategory(user, category, 200);
        walletService.addWallet(user, wallet, 100);
        walletService.addTransaction(user, wallet, 10, category, true);
        walletService.addTransaction(user, wallet, 20, category, false);
        assertEquals(new Finances(10, 20), walletService.calculateFinances(user));
        walletService.removeWallet(user, wallet);
        categoryRepository.saveCategories(categoryRepository.loadCategories().stream().filter(item -> !(item.getName().equals(category) && item.getUserId().equals(user.getUsername()))).toList());
    }
}
