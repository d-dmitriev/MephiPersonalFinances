package home.work.finance.service;

import home.work.finance.model.Category;

import java.util.List;

public interface CategoryRepository {
    Category findCategoryByUserIdAndName(String username, String categoryName);

    List<Category> loadCategories();

    void saveCategories(List<Category> categories);

    List<Category> findCategoriesByUserId(String username);
}
