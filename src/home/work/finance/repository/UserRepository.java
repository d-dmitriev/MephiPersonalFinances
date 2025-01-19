package home.work.finance.repository;

import home.work.finance.model.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository extends FileRepository {
    private static final String FILE_PATH = "data/users/users.json";

    protected void init() {
        File file = new File(FILE_PATH);
        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                saveUsers(new ArrayList<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании файла пользователей: " + e.getMessage());
        }
    }

    public void saveUsers(List<User> users) {
        try {
            saveDataToFile(FILE_PATH, users);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении пользователей: " + e.getMessage());
        }
    }

    public List<User> loadUsers() {
        try {
            List<User> users = loadDataFromFile(FILE_PATH, User.class);
            if (users == null) {
                users = new ArrayList<>();
            }
            return users;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке пользователей: " + e.getMessage());
        }
    }

    public User findUserByUsername(String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}