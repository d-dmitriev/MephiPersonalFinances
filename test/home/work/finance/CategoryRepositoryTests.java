package home.work.finance;

import home.work.finance.model.Category;
import home.work.finance.repository.CategoryRepository;
import org.junit.jupiter.api.Test;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryRepositoryTests {
    private static final CategoryRepository categoryRepository = new CategoryRepository();

    @Test
    void findCategoriesByUserId() {
        assertEquals(0, categoryRepository.findCategoriesByUserId("test").size());
    }

    @Test
    void findCategoriesByUserIdError() {
        assertEquals(0, categoryRepository.findCategoriesByUserId("test1").size());
    }

    @Test
    void findCategoryByUserIdAndName() {
        String userId = "test_user_for_category";
        String category = "category_for_test";
        List<Category> list = categoryRepository.loadCategories();
        list.add(new Category(userId, category, 100));
        categoryRepository.saveCategories(list);
        assertEquals(category, categoryRepository.findCategoryByUserIdAndName(userId, category).getName());
        categoryRepository.saveCategories(categoryRepository.loadCategories().stream().filter(c -> !(c.getUserId().equals(userId) && c.getName().equals(category))).toList());
    }

    @Test
    void findCategoryByUserIdAndNameError() {
        assertNull(categoryRepository.findCategoryByUserIdAndName("test", "test2"));
    }
}
