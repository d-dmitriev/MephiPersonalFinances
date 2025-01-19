package home.work.finance.repository;

import home.work.finance.model.Category;
import home.work.finance.service.CategoryRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CategoryRepositoryImpl extends FileRepository implements CategoryRepository {
    private static final String FILE_PATH = "data/categories/categories.json";

    protected void init() {
        File file = new File(FILE_PATH);
        try {
            if (file.getParentFile().mkdirs() && file.createNewFile()) {
                saveCategories(new ArrayList<>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании файла категорий: " + e.getMessage());
        }
    }

    public void saveCategories(List<Category> categories) {
        try {
            saveDataToFile(FILE_PATH, categories);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении категорий: " + e.getMessage());
        }
    }

    public List<Category> loadCategories() {
        try {
            return loadDataFromFile(FILE_PATH, Category.class);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при загрузке категорий: " + e.getMessage());
        }
    }

    public List<Category> findCategoriesByUserId(String userId) {
        return loadCategories().stream().filter(category -> category.getUserId().equals(userId)).toList();
    }

    public Category findCategoryByUserIdAndName(String userId, String name) {
        return loadCategories().stream().filter(category -> category.getName().equals(name) && category.getUserId().equals(userId)).findFirst().orElse(null);
    }
}