package home.work.finance.service;

import home.work.finance.model.Category;
import home.work.finance.model.User;
import home.work.finance.model.Wallet;
import home.work.finance.repository.CategoryRepository;
import home.work.finance.repository.UserRepository;
import home.work.finance.repository.WalletRepository;
import home.work.finance.util.DataValidator;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;
    private User currentUser;

    public UserService(UserRepository userRepository, WalletRepository walletRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.categoryRepository = categoryRepository;
    }

    public void registerUser(String username, String password) {
        validateUsername(username);
        validatePassword(password);

        List<User> users = userRepository.loadUsers();

        if (users.stream().anyMatch(user -> user.getUsername().equals(username))) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует.");
        }

        users.add(new User(username, password));
        userRepository.saveUsers(users);
    }

    public User findUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public void authenticateUser(String username, String password) {
        validateUsername(username);
        validatePassword(password);

        User user = findUserByUsername(username);

        if (user == null) {
            throw new RuntimeException("Пользователь с логином '" + username + "' не найден.");
        }

        if (user.getPassword().equals(password)) {
            currentUser = user;
            return;
        }

        throw new RuntimeException("Неверный пароль.");
    }

    public void changePassword(String oldPassword, String newPassword) {
        validatePassword(newPassword);

        if (currentUser == null || !currentUser.getPassword().equals(oldPassword)) {
            throw new IllegalArgumentException("Неверный старый пароль.");
        }

        List<User> users = userRepository.loadUsers();

        for (User user : users) {
            if (user.getUsername().equals(currentUser.getUsername())) {
                user.setPassword(newPassword);
                break;
            }
        }

        userRepository.saveUsers(users);
        currentUser.setPassword(newPassword);
    }

    public void changeUsername(String newUsername) {
        validateUsername(newUsername);

        if (currentUser == null) {
            throw new IllegalArgumentException("Пользователь не авторизован.");
        }

        if (findUserByUsername(newUsername) != null) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует.");
        }

        List<User> users = userRepository.loadUsers();

        for (User user : users) {
            if (user.getUsername().equals(currentUser.getUsername())) {
                user.setUsername(newUsername);
                break;
            }
        }

        userRepository.saveUsers(users);
        updateWalletsUserId(currentUser.getUsername(), newUsername);
        updateCategoriesUserId(currentUser.getUsername(), newUsername);
        currentUser.setUsername(newUsername);
    }

    private void updateWalletsUserId(String oldUserId, String newUserId) {
        List<Wallet> wallets = walletRepository.loadWalletsByUser(oldUserId);

        for (Wallet wallet : wallets) {
            wallet.setUserId(newUserId);
        }

        walletRepository.saveWallets(wallets);
    }

    private void updateCategoriesUserId(String oldUserId, String newUserId) {
        List<Category> categories = categoryRepository.findCategoriesByUserId(oldUserId);

        for (Category category : categories) {
            category.setUserId(newUserId);
        }

        categoryRepository.saveCategories(categories);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void validateUsername(String username) {
        if (!DataValidator.isNonEmptyString(username) || !DataValidator.isStringLengthValid(username, 20)
                || !DataValidator.isValidLogin(username)) {
            throw new IllegalArgumentException("Некорректный логин.");
        }
    }

    private void validatePassword(String password) {
        if (!DataValidator.isNonEmptyString(password) || !DataValidator.isValidPassword(password)) {
            throw new IllegalArgumentException("Некорректный пароль.");
        }
    }
}