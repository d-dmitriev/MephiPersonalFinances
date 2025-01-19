package home.work.finance.service;

import home.work.finance.model.User;

import java.util.List;

public interface UserRepository {
    User findUserByUsername(String username);

    void saveUsers(List<User> users);

    List<User> loadUsers();
}
