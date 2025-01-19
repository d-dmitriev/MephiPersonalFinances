package home.work.finance;

import home.work.finance.dto.Budget;
import home.work.finance.model.User;
import home.work.finance.repository.CategoryRepositoryImpl;
import home.work.finance.repository.WalletRepositoryImpl;
import home.work.finance.service.BudgetService;
import home.work.finance.service.CategoryRepository;
import home.work.finance.service.WalletRepository;
import home.work.finance.service.WalletService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BudgetServiceTest {
    private static final WalletRepository walletRepository = new WalletRepositoryImpl();
    private static final CategoryRepository categoryRepository = new CategoryRepositoryImpl();
    private static final BudgetService budgetService = new BudgetService(walletRepository, categoryRepository);
    private static final WalletService walletService = new WalletService(walletRepository, categoryRepository);

    @Test
    void addCategory() {
        User user = new User("add_category_test","");
        String categoryName = "new_category";
        budgetService.addCategory(user, categoryName, 100);
        assertEquals(categoryName, categoryRepository.findCategoryByUserIdAndName(user.getUsername(), categoryName).getName());
        removeCategory(user.getUsername(), categoryName);
    }

    @Test
    void addCategoryError() {
        User user = new User("test_add_category","");
        String categoryName = "add_category";
        budgetService.addCategory(user, categoryName, 100);
        assertThrows(IllegalArgumentException.class, () -> budgetService.addCategory(user, categoryName, 100));
        removeCategory(user.getUsername(), categoryName);
    }

    @Test
    void renameCategory() {
        User user = new User("rename_category_test","");
        String oldName = "old_name";
        String newName = "new_name";
        budgetService.addCategory(user, oldName, 100);
        budgetService.renameCategory(user, oldName, newName);
        assertEquals(newName, categoryRepository.findCategoryByUserIdAndName(user.getUsername(), newName).getName());
        removeCategory(user.getUsername(), newName);

    }

    @Test
    void updateBudgetLimit() {
        User user = new User("update_category_test","");
        String categoryName = "test_category";
        budgetService.addCategory(user, categoryName, 100);
        budgetService.updateBudgetLimit(user, categoryName, 200);
        assertEquals(200, categoryRepository.findCategoryByUserIdAndName(user.getUsername(), categoryName).getBudgetLimit());
        removeCategory(user.getUsername(), categoryName);
    }

    @Test
    void listCategories() {
        User user = new User("update_category_test","");
        String categoryName = "test_category";
        assertEquals(0, budgetService.listCategories(user).size());
        budgetService.addCategory(user, categoryName, 100);
        assertEquals(1, budgetService.listCategories(user).size());
        removeCategory(user.getUsername(), categoryName);
    }

    @Test
    void calculateBudgetState() {
        User user = new User("update_category_test","");
        String categoryName = "test_category_state";
        String walletName = "test_wallet";
        double budget = 100;
        budgetService.addCategory(user, categoryName, budget);
        walletService.addWallet(user, walletName, 200);
        walletService.addTransaction(user, walletName, 10, categoryName, false);
        walletService.addTransaction(user, walletName, 10, categoryName, false);
        List<Budget> r = new ArrayList<>();
        r.add(new Budget(categoryName, budget, 20, 80));
        assertIterableEquals(r, budgetService.calculateBudgetState(user));
        removeCategory(user.getUsername(), categoryName);
        walletService.removeWallet(user, walletName);
    }

    void removeCategory(String userId, String categoryName) {
        categoryRepository.saveCategories(categoryRepository.loadCategories().stream().filter(item -> !(item.getName().equals(categoryName) && item.getUserId().equals(userId))).toList());
    }
}
